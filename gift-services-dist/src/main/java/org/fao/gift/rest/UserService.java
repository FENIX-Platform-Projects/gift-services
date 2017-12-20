package org.fao.gift.rest;

import org.fao.gift.common.dto.Outcome;
import org.fao.gift.common.dto.Survey;
import org.fao.gift.common.dto.User;
import org.fao.gift.forum.client.ForumClient;
import org.fao.gift.rest.spi.UserSpi;
import org.fao.gift.services.UserLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.sql.SQLException;
import java.util.List;

import static org.fao.gift.common.utils.RestUtils.getBaseResponse;
import static org.fao.gift.services.UserLogic.normalizeUsername;


@Path("user")
public class UserService implements UserSpi {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    private UserLogic userLogic;

    @Inject
    private ForumClient forumClient;

    @Override
    public Response getUser(String username) {
        try {
            User user = userLogic.getUser(username);
            return user != null ? Response.ok(user).build() : getBaseResponse(Status.NO_CONTENT);

        } catch (SQLException e) {
            return Response.serverError().build();
        }
    }

    @Override
    public Response getJwt(String username) {
        try {
            User user = userLogic.getUser(UserLogic.normalize(username));
            if (user == null) {
                return getBaseResponse(Status.NO_CONTENT);
            }

            User newUser = new User();
            newUser.setJwt(user.getJwt());
            newUser.setForumId(user.getForumId());
            return Response.ok(newUser).build();

        } catch (SQLException e) {
            return Response.serverError().build();
        }
    }

    //    @Override
    public Response createUser(User user) {
        if (user == null) {
            return getBaseResponse(Status.BAD_REQUEST);
        }

        normalizeUsername(user);

        try {
            userLogic.create(user.getForumId(), user.getName(), user.getUsername(), user.getRole(), user.getInstitution(), user.getEmail());
            return getBaseResponse(Status.CREATED);

        } catch (IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(Outcome.create(String.format("%s: %s", Status.BAD_REQUEST.getReasonPhrase(), e.getMessage())))
                    .build();

        } catch (EntityExistsException e) {
            return getBaseResponse(Status.CONFLICT);

        } catch (SQLException e) {
            final String ERR_MESSAGE = "Could not persist user";
            log.error(ERR_MESSAGE, e);
            return Response.serverError()
                    .entity(Outcome.create(ERR_MESSAGE))
                    .build();
        }
    }

    @Override
    public Response createAndOrGetUser(User user) {
        try {
            if (user == null || user.getUsername() == null) {
                return Response.status(Status.BAD_REQUEST).entity("Bad username").build();
            }
            normalizeUsername(user);

            User existingUser = userLogic.getUser(user.getUsername());
            // create user if he doesn't exist
            if (existingUser != null) {
                return Response.ok(existingUser).build();
            }
            log.info("Creating new user: {}", user);


            // create user in the forum
            long userForumId = forumClient.createUser(user.getUsername(), user.getEmail());
            // set forumID and create user in our db
            user.setForumId(userForumId);
            userLogic.create(user.getForumId(), user.getName(), user.getUsername(), user.getRole(), user.getInstitution(), user.getEmail());

            return getUser(user.getUsername());

        } catch (IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(Outcome.create(String.format("%s: %s", Status.BAD_REQUEST.getReasonPhrase(), e.getMessage())))
                    .build();

        } catch (SQLException e) {
            final String ERR_MESSAGE = "Could not create user";
            log.error(ERR_MESSAGE, e);
            return Response.serverError()
                    .entity(Outcome.create(ERR_MESSAGE))
                    .build();
        }
    }

    private Response getUserSurveys(long forumId) {
        try {
            List<Survey> userSurveys = userLogic.getUserSurveys(forumId);
            return Response.ok(userSurveys).build();
        } catch (SQLException e) {
            final String ERR_MESSAGE = "Could not retrieve user's surveys";
            log.error(ERR_MESSAGE, e);
            return Response.serverError()
                    .entity(Outcome.create(ERR_MESSAGE))
                    .build();
        }
    }

    @Override
    public Response getUserSurveys(String username) {
        try {
            List<Survey> userSurveys = userLogic.getUserSurveys(username);
            return Response.ok(userSurveys).build();
        } catch (SQLException e) {
            final String ERR_MESSAGE = "Could not retrieve user's surveys";
            log.error(ERR_MESSAGE, e);
            return Response.serverError()
                    .entity(Outcome.create(ERR_MESSAGE))
                    .build();
        }
    }
}