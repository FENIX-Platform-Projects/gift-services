package org.fao.gift.forum.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.fao.gift.forum.client.ForumClient;
import org.fao.gift.forum.client.ForumConfigStore;
import org.fao.gift.forum.client.dto.Category;
import org.fao.gift.forum.client.dto.CategoryPrivileges;
import org.fao.gift.forum.client.dto.Post;
import org.fao.gift.forum.client.dto.Topic;
import org.fao.gift.forum.client.dto.User;
import org.fao.gift.forum.client.qualifier.Rest;
import org.fao.gift.forum.client.utils.JsonUtil;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Rest
@Default
public class ForumClientRest implements ForumClient {

    private static final Logger log = LoggerFactory.getLogger(ForumClientRest.class);

    // API
    private static final String DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON;
    private static final String API_URL = "/api/v2";
    private static final String API_USER = "/users";
    private static final String API_CATEGORY = "/categories";
    private static final String API_TOPIC = "/topics";
    private static final String API_PRIVILEGES = "/privileges";

    // Response properties
    private static final String RESPONSE_BODY = "payload";
    private static final String CATEGORY_ID = "cid";
    private static final String POST_ID = "pid";
    private static final String TOPIC_ID = "tid";
    private static final String TOPIC_RESPONSE = "topicData";
    private static final String USER_ID = "uid";

    @Inject
    ForumConfigStore config;

    public ForumClientRest() {
    }

    @Override
    public long createUser(String username, String email) {
        return create(new User(username, email.toLowerCase()), API_URL.concat(API_USER), RESPONSE_BODY, USER_ID);
    }

    @Override
    public long createCategory(String name) {
        return create(new Category(name), API_URL.concat(API_CATEGORY), RESPONSE_BODY, CATEGORY_ID);
    }

    @Override
    public long createTopic(long categoryId, String title, String content) {
        return create(new Topic(categoryId, title, content), API_URL.concat(API_TOPIC), RESPONSE_BODY, TOPIC_RESPONSE, TOPIC_ID);
    }

    @Override
    public long createPost(long topicId, String content) {
        return create(new Post(content), API_URL.concat(API_TOPIC).concat("/").concat(String.valueOf(topicId)), RESPONSE_BODY, POST_ID);
    }

    @Override
    public void setOwner(long categoryId, long userId) {
        set(new CategoryPrivileges(new String[]{String.valueOf(userId)}),
                API_URL.concat(API_CATEGORY).concat("/").concat(String.valueOf(categoryId).concat(API_PRIVILEGES))
        );
    }


    // Auxiliary methods

    /**
     * Create a resource on the forum, according to the type of 'resource'
     *
     * @param resource         an object representing a forum resource, like Category, User, Topic or Post
     * @param apiUrl           the URL of the forum API to invoke
     * @param resourceTreePath a list of node names expected in the output, from root (excluded) to the node of interest
     * @param <T>              the type of the resource to be created (e.g. {@link Category}, {@link Topic}, {@link Post}, {@link User})
     * @return the ID of the created resource.
     */
    private <T> long create(T resource, String apiUrl, String... resourceTreePath) {
        final Long assignedId = handleRequest(resource, apiUrl, "POST", resourceTreePath).asLong();
        log.info("create - {} - assigned ID {}", resource, assignedId); // the last element of resourceTreePath contains the id label of the resource, e.g. "uid", "cid"
        return assignedId;
    }

    /**
     * Update a resource on the forum, according to the type of 'resource'
     *
     * @param resource an object representing a forum resource, like Category, User, Topic or Post
     * @param apiUrl   the URL of the forum API to invoke
     * @param <T>      the type of the resource to be created (e.g. {@link Category}, {@link Topic}, {@link Post}, {@link User})
     */
    private <T> void set(T resource, String apiUrl) {
        handleRequest(resource, apiUrl, "PUT", RESPONSE_BODY);
        log.info("set - {} - done", resource);
    }

    private <T> JsonNode handleRequest(T resource, String apiUrl, String httpMethod, String... resourceTreePath) {
        final String TARGET_URI = config.get("gift.forum.url").concat(apiUrl);
        final String TOKEN = config.get("gift.forum.api.token");

        Response response = null;
        try {
            response = makeRequest(TARGET_URI, TOKEN, resource, httpMethod);

            String responseString = response.readEntity(String.class);
            log.debug("handleRequest - response: {}, code: {}", responseString, response.getStatus());

            switch (response.getStatus()) {
                case 200:
                    return JsonUtil.resolve(responseString, resourceTreePath);

                default:
                    throw new Exception(responseString);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * Fires a REST request
     *
     * @param targetURI  URI of the targeted API
     * @param token      the Bearer token for forum API authentication
     * @param entity     the resource to be sent to the forum
     * @param httpMethod the HTTP method, like POST, GET or PUT
     * @param <T>        the type of the resource to be created (e.g. {@link Category}, {@link Topic}, {@link Post}, {@link User})
     * @return the forum API Response
     */
    private static <T> Response makeRequest(String targetURI, String token, T entity, String httpMethod) {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(targetURI);
        return target.request()
                .header("Authorization", "Bearer ".concat(token))
                .method(httpMethod, Entity.entity(entity, DEFAULT_MEDIA_TYPE));
    }
}
