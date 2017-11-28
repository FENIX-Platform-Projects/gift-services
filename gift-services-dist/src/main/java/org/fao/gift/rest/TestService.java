package org.fao.gift.rest;

import org.fao.gift.forum.client.impl.ForumClientRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("test")
public class TestService {

    private static final Logger log = LoggerFactory.getLogger(TestService.class);

//    @Inject
//    GiftBulk giftBulkManager;

    @Inject
    ForumClientRest forumClientRest;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getProcessStatus() {
        try {
            return "OK";

        } catch (Exception e) {
            log.error("", e);
            return "ERROR: " + e.getMessage();
        }
    }


//    @Path("user")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public void createUser(@QueryParam("username") String username) {
//        try {
//            log.info("TEST Service STARTED");
//
//            forumClientRest.createUser(username, username + "@dispostable.com");
//
//        } catch (Exception e) {
//            log.error("", e);
//        }
//    }
//
//    @Path("category")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public void createCategory(@QueryParam("name") String name) {
//        try {
//            log.info("TEST Service STARTED");
//
//            forumClientRest.createCategory(name);
//
//        } catch (Exception e) {
//            log.error("", e);
//        }
//    }
//
//    @Path("topic")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public void createTopic(@QueryParam("cid") long cid, @QueryParam("title") String title, @QueryParam("content") String content) {
//        try {
//            log.info("TEST Service STARTED");
//
//            forumClientRest.createTopic(cid, title, content);
//
//        } catch (Exception e) {
//            log.error("", e);
//        }
//    }
//
//    @Path("post")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public void createPost(@QueryParam("tid") long tid, @QueryParam("content") String content) {
//        try {
//            log.info("TEST Service STARTED");
//
//            forumClientRest.createPost(tid, content);
//
//        } catch (Exception e) {
//            log.error("", e);
//        }
//    }
//
//    @Path("privileges")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public void setPrivileges(@QueryParam("cid") long cid, @QueryParam("uid") long uid) {
//        try {
//            log.info("TEST Service STARTED");
//
//            forumClientRest.setOwner(cid, uid);
//
//        } catch (Exception e) {
//            log.error("", e);
//        }
//    }
}
