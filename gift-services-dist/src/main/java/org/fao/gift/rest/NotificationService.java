package org.fao.gift.rest;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.gift.common.dto.MeWithUser;
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

@Path("msd/resources")
public class NotificationService implements NotificationSpi {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Inject
    NotificationLogic notificationLogic;

    @Inject
    UserLogic userLogic;

    @Inject
    SurveyLogic surveyLogic;

    @Inject
    ForumClient forumClient;

    @Inject
    MailService mailService;

    private static final String GENERAL_TOPIC_TITLE = "Discussions and feedback";
    private static final String GENERAL_TOPIC_CONTENT = "This is the place to discuss";

    @Override
    public <T extends MeIdentification> Response insertMetadata(MeWithUser meForum) throws Exception {
        User user = meForum.getUser();
        MeIdentification metadata = meForum.getMetadata();
        log.info("insertMetadata - START - {}", user);
        if (user == null || user.getUsername() == null)
            throw new IllegalArgumentException("missing user's mandatory info");

        User existingUser = userLogic.getUser(user.getUsername());
        long userForumId;
        // create user if he doesn't exist
        if (existingUser == null) {
            // create user in the forum
            userForumId = forumClient.createUser(user.getUsername(), user.getEmail());
            // set forumID and create user in our db
            user.setForumId(userForumId);
            userLogic.create(user.getForumId(), user.getName(), user.getUsername(), user.getRole(), user.getInstitution(), user.getEmail());

        } else {
            userForumId = existingUser.getForumId();
        }
        Response response = notificationLogic.insertMetadata(user, metadata);
        log.info("insertMetadata - data insertion status code: {}, updating forum. {}", response.getStatus(), user);

        if (Response.Status.OK.getStatusCode() == response.getStatus() || Response.Status.CREATED.getStatusCode() == response.getStatus()) {
            // Create associated category
            long categoryId = forumClient.createCategory(metadata.getTitle().get("EN").toString());
            // Create associated topic
            long topicId = forumClient.createTopic(categoryId, GENERAL_TOPIC_TITLE, GENERAL_TOPIC_CONTENT);
            // Set privileges to the owner
            forumClient.setOwner(categoryId, userForumId);


            String jsonResponse = response.readEntity(String.class);
            String uid = JsonUtil.resolve(jsonResponse, "uid").asText();
            // Save the user-survey association on the DB
            surveyLogic.create(uid, categoryId, userForumId, topicId);

            mailService.sendMail(MailTemplate.MD_CREATION, user.getEmail(), user.getName(), uid);
            return Response.ok(jsonResponse).build();
        }

        return Response.serverError().build();
    }

    @Override
    public <T extends MeIdentification> Response updateMetadata(MeWithUser meForum) throws Exception {
        notificationLogic.updateMetadata(meForum.getMetadata());
        return Response.ok().build();
    }

    @Override
    public <T extends MeIdentification> Response appendMetadata(MeWithUser meForum) throws Exception {
        notificationLogic.updateMetadata(meForum.getMetadata());
        return Response.ok().build();
    }

    @Override
    public Response deleteMetadataByUID(String uid) throws Exception {
        notificationLogic.deleteMetadataByUID(uid);
        return Response.ok().build();
    }

    @Override
    public Response deleteMetadata(String uid, String version) throws Exception {
        notificationLogic.deleteMetadata(uid, version);
        return Response.ok().build();
    }
}
