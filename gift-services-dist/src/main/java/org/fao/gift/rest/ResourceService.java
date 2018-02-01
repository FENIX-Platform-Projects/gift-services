package org.fao.gift.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
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
@Api(value = "metadata")

public class ResourceService implements NotificationSpi {

    private static final Logger log = LoggerFactory.getLogger(ResourceService.class);

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
        log.info("insertMetadata - START - {}", meForum);

        // check all parameters
        log.info("checking Parameters");
        checkParameters(meForum);

        User user = meForum.getUser();
        log.info("checking user attributes");
        checkUserAttributes(user);
        log.info("insertMetadata - user - {}", user);

        MeIdentification metadata = meForum.getMetadata();
        log.info("checking metadata attributes");
        checkMetadataAttributes(metadata);
        log.info("insertMetadata - metadata - {}", metadata);

        // Normalize username for special characters
        log.info("Normalize username for special characters ");
        normalizeUsername(user);

        User existingUser = userLogic.getUser(user.getUsername());
        log.info("getExistingUser - {}", existingUser);

        long userForumId;
        // create user if he doesn't exist
        if (existingUser == null) {

            // create user in the forum
            userForumId = forumClient.createUser(user.getUsername(), user.getEmail());
            log.info("create forum Id ",userForumId);

            // set forumID and create user in our db
            user.setForumId(userForumId);
            log.info("starting user creation in db");
            userLogic.create(user.getForumId(), user.getName(), user.getUsername(), user.getRole(), user.getInstitution(), user.getEmail());

        } else {
            userForumId = existingUser.getForumId();
            log.info("existing user forum id: "+userForumId);

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
            log.info("setOwner where user Forum Id: "+userForumId + " and category ID is "+categoryId);
            forumClient.setOwner(categoryId, userForumId);


            String uid = JsonUtil.resolve(jsonResponse, "uid").asText();
            log.info("UID from D3S is : "+uid);

            // Save the user-survey association on the DB
            log.info("create user-survey association on the DB");
            surveyLogic.create(uid, categoryId, userForumId, topicId);


            //Send an email for the creation of the metadata
            log.info("starting sending email asynchrously");
            mailService.sendMail(MailTemplate.md_download, user.getEmail(), user.getName(), uid);
            return Response.ok(jsonResponse).build();
        }

        return Response.status(response.getStatus()).entity(jsonResponse).build();
    }

    @Override
    public <T extends MeIdentification> Response updateMetadata(MeWithUser meForum) throws Exception {
        log.info("updateMetadata - START - {}");

        // check parameters
        checkParameters(meForum);

        // Get user and metadata and check attributes
        User user = meForum.getUser();
        log.info("checking user attributes");
        checkUserAttributes(user);
        log.info("insertMetadata - user - {}", user);


        MeIdentification metadata = meForum.getMetadata();
        log.info("checking metadata attributes");
        checkMetadataAttributes(metadata);
        log.info("insertMetadata - metadata - {}", metadata);

        if(meForum.getMetadata().getUid() == null)
            throw new Exception("Error: metadata without uid");

        //get user from forum
        User existingUser = userLogic.getUser(user.getUsername());
        log.info("existingUser - existingUser - {}", existingUser);
        if (existingUser == null)
            throw new Exception("this user "+user.getUsername()+" has not been registered");
        Response response = notificationLogic.updateMetadata(metadata);

        String jsonResponse = response.readEntity(String.class);
        log.info("response  from D3s - {}" + response);

        if (Response.Status.OK.getStatusCode() == response.getStatus() || Response.Status.CREATED.getStatusCode() == response.getStatus()) {
            // Take survey from forum logic
            Survey survey = surveyLogic.getSurvey(metadata.getUid());
            log.info("survey from forum logic - {}" , survey);

            if(survey == null)
                throw new Exception("this survey "+metadata.getTitle()+" has not been saved");

            long topicId = survey.getDefaultTopicId();
            log.info("topicId from forum logic - {}" , topicId);

            // send message on forum
            forumClient.createPost(topicId, "The user "+existingUser.getUsername()+ " has modified some metadata fields");
            log.info("updateMetadata - END - {}");

            //Rebuild the response
            return Response.ok(jsonResponse).build();
        }
        log.info("some errors have been found on the D3S response" , response);

        return Response.status(response.getStatus()).entity(jsonResponse).build();
    }

    @Override
    public <T extends MeIdentification> Response appendMetadata(MeWithUser meForum) throws Exception {
        notificationLogic.updateMetadata(meForum.getMetadata());
        return Response.ok().build();
    }

    @Override
    public Response deleteMetadataByUID(String uid) throws Exception {
        log.info("deleteMetadataByUID - START - {}");
        return notificationLogic.deleteMetadataByUID(uid);
    }

    @Override
    public Response deleteMetadata(String uid, String version) throws Exception {
        log.info("deleteMetadata - START - {}");
        return  notificationLogic.deleteMetadata(uid, version);
    }


    // Utils
    private void checkParameters (MeWithUser meForum) {
        if(meForum == null)
            throw new IllegalArgumentException("missing user and metadata info");
        if (meForum.getUser() == null || meForum.getUser().getUsername() == null)
            throw new IllegalArgumentException("missing user's mandatory info");
        if (meForum.getMetadata() == null )
            throw new IllegalArgumentException("missing metadata mandatory info");
    }

    private void checkUserAttributes (User user) {
        if(user.getName() == null)
            throw new IllegalArgumentException("Name is mandatory for the user");
        if(user.getUsername() == null)
            throw new IllegalArgumentException("Username is mandatory for the user");
        if(user.getEmail() == null)
            throw new IllegalArgumentException("Email is mandatory for the user");
        if(user.getRole() == null)
            throw new IllegalArgumentException("Role is mandatory for the user");
        if(user.getInstitution() == null)
            throw new IllegalArgumentException("Institution is mandatory for the user");
    }

    private void checkMetadataAttributes (MeIdentification metadata) {

        if(metadata.getTitle() == null)
            throw new IllegalArgumentException("Title is mandatory into the metadata");
    }
}
