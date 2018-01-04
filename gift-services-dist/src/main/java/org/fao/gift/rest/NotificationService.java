package org.fao.gift.rest;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.gift.common.dto.MeWithUser;
import org.fao.gift.common.dto.Survey;
import org.fao.gift.common.dto.User;
import org.fao.gift.config.email.MailTemplate;
import org.fao.gift.forum.client.ForumClient;
import org.fao.gift.forum.client.utils.JsonUtil;
import org.fao.gift.notification.impl.NotificationLogic;
import org.fao.gift.rest.spi.NotificationSpi;
import org.fao.gift.services.SurveyLogic;
import org.fao.gift.services.UserLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static org.fao.gift.services.UserLogic.normalizeUsername;

@Path("msd/resources")
public class NotificationService implements NotificationSpi {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Inject NotificationLogic notificationLogic;
    @Inject UserLogic userLogic;
    @Inject SurveyLogic surveyLogic;
    @Inject ForumClient forumClient;
    @Inject MailService mailService;

    private static final String GENERAL_TOPIC_TITLE = "Discussions and feedback";
    private static final String GENERAL_TOPIC_CONTENT = "This is the place to discuss";

    @Override
    public <T extends MeIdentification> Response insertMetadata(MeWithUser meForum) throws Exception {
        // Get user and metadata
        User user = meForum.getUser();
        MeIdentification metadata = meForum.getMetadata();
        log.info("insertMetadata - START - {}", user);

        // check parameters
        checkParameters(meForum);

        // Normalize username for special characters
        normalizeUsername(user);

        User existingUser = userLogic.getUser(user.getUsername());

        long userForumId;
        // create user if he doesn't exist
        if (existingUser == null) {

            // create user in the forum
            userForumId = forumClient.createUser(user.getUsername(), user.getEmail());
            log.info("user forum Id ",userForumId);

            // set forumID and create user in our db
            user.setForumId(userForumId);
            log.info("starting user creation in db");
            userLogic.create(user.getForumId(), user.getName(), user.getUsername(), user.getRole(), user.getInstitution(), user.getEmail());

        } else {
            userForumId = existingUser.getForumId();
        }

        Response response = notificationLogic.insertMetadata(metadata);
        log.info("insertMetadata - data insertion status code: {}, updating forum. {}", response.getStatus(), user);
        String jsonResponse = response.readEntity(String.class);

        if (Response.Status.OK.getStatusCode() == response.getStatus() || Response.Status.CREATED.getStatusCode() == response.getStatus()) {
            // Create associated category
            long categoryId = forumClient.createCategory(metadata.getTitle().get("EN").toString());
            log.info("category Id", categoryId);


            // Create associated topic
            long topicId = forumClient.createTopic(categoryId, GENERAL_TOPIC_TITLE, GENERAL_TOPIC_CONTENT);
            log.info("topic Id", topicId);

            // Set privileges to the owner
            forumClient.setOwner(categoryId, userForumId);


            String uid = JsonUtil.resolve(jsonResponse, "uid").asText();

            // Save the user-survey association on the DB
            surveyLogic.create(uid, categoryId, userForumId, topicId);

            log.info("starting sending email asynchrously");


            //Send an email for the creation of the metadata
            mailService.sendMail(MailTemplate.md_download, user.getEmail(), user.getName(), uid);
            return Response.ok(jsonResponse).build();
        }

        return Response.status(response.getStatus()).entity(jsonResponse).build();
    }

    @Override
    public <T extends MeIdentification> Response updateMetadata(MeWithUser meForum) throws Exception {

        // Get user and metadata
        User user = meForum.getUser();
        MeIdentification metadata = meForum.getMetadata();
        log.info("insertMetadata - START - {}", user);

        // check parameters
        checkParameters(meForum);
        if(meForum.getMetadata().getUid() == null)
            throw new Exception("Error: metadata without uid");

        //get user from forum
        User existingUser = userLogic.getUser(user.getUsername());
        if (existingUser == null)
            throw new Exception("this user "+user.getUsername()+" has not been registered");


        Response response = notificationLogic.updateMetadata(metadata);

        String jsonResponse = response.readEntity(String.class);
        if (Response.Status.OK.getStatusCode() == response.getStatus() || Response.Status.CREATED.getStatusCode() == response.getStatus()) {
            // Take survey from forum logic
            Survey survey = surveyLogic.getSurvey(metadata.getUid());

            if(survey == null)
                throw new Exception("this survey "+metadata.getTitle()+" has not been saved");

            long topicId = survey.getDefaultTopicId();

            // send message on forum
            forumClient.createPost(topicId, "The user "+existingUser.getUsername()+ " has modified some metadata fields");

            //Rebuild the response
            return Response.ok(jsonResponse).build();
        }

        return Response.status(response.getStatus()).entity(jsonResponse).build();
    }

    @Override
    public <T extends MeIdentification> Response appendMetadata(MeWithUser meForum) throws Exception {
        notificationLogic.updateMetadata(meForum.getMetadata());
        return Response.ok().build();
    }

    @Override
    public Response deleteMetadataByUID(String uid) throws Exception {
        return notificationLogic.deleteMetadataByUID(uid);
    }

    @Override
    public Response deleteMetadata(String uid, String version) throws Exception {
        return  notificationLogic.deleteMetadata(uid, version);
    }


    // Utils
    private void checkParameters (MeWithUser meForum) {
        if (meForum.getUser() == null || meForum.getUser().getUsername() == null)
            throw new IllegalArgumentException("missing user's mandatory info");
        if (meForum.getMetadata() == null )
            throw new IllegalArgumentException("missing metadata mandatory info");
    }
}
