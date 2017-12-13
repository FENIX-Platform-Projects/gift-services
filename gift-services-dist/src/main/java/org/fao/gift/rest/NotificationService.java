package org.fao.gift.rest;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.gift.common.dto.User;
import org.fao.gift.forum.client.ForumClient;
import org.fao.gift.forum.client.impl.ForumClientRest;
import org.fao.gift.notification.Notification;
import org.fao.gift.notification.impl.NotificationLogic;
import org.fao.gift.rest.spi.NotificationSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("msd/resources")
public class NotificationService implements NotificationSpi{

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Inject NotificationLogic notificationLogic;
    @Inject UserService userService;
    @Inject ForumClient forumClient;
    private static final String GENERAL_TOPIC_TITLE = "Discussions and feedback";
    private static final String GENERAL_TOPIC_CONTENT = "This is the place to discuss";

    @Override
    public <T extends MeIdentification> org.fao.fenix.commons.msd.dto.templates.identification.MeIdentification insertMetadata(User user, T metadata) throws Exception {
        Response response = null;
        if(user== null || user.getUsername()==null)
            throw new Exception("user or username is mandatory");

        long forumClientID = -1 ;

        // create user if he doesn't exist
        if(userService.getUser(user.getUsername())== null) {
            // create user in the forum
            forumClientID = forumClient.createUser(user.getUsername(), user.getEmail());
            // set forumID and create user in our db
            user.setForumId(forumClientID);
            userService.createUser(user);
        }
       notificationLogic.insertMetadata(user, metadata);
        // Create associated category
        long categoryId = forumClient.createCategory(metadata.getTitle().get("EN").toString());
        // Create associated topic
        forumClient.createTopic(categoryId, GENERAL_TOPIC_TITLE,GENERAL_TOPIC_CONTENT);
        // Set privileges to the owner
        forumClient.setOwner(categoryId,forumClientID);

        return null;
    }

    @Override
    public <T extends MeIdentification> org.fao.fenix.commons.msd.dto.templates.identification.MeIdentification updateMetadata(User user, T metadata) throws Exception {
        notificationLogic.updateMetadata(metadata);
        return null;
    }

    @Override
    public <T extends MeIdentification> org.fao.fenix.commons.msd.dto.templates.identification.MeIdentification appendMetadata(User user, T metadata) throws Exception {
        notificationLogic.updateMetadata(metadata);
        return null;
    }



    @Override
    public String deleteMetadataByUID(String uid) throws Exception {
        notificationLogic.deleteMetadataByUID(uid);
        return null;
    }

    @Override
    public String deleteMetadata(String uid, String version) throws Exception {
        notificationLogic.deleteMetadata(uid,version);
        return null;
    }

}
