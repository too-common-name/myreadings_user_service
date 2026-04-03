package org.modular.playground.user.web.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.modular.playground.user.core.usecases.UserService;
import org.modular.playground.user.infrastructure.persistence.postgres.mapper.UserMapper;
import org.modular.playground.user.web.dto.UserResponseDTO;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/api/internal/users")
@Produces(MediaType.APPLICATION_JSON)
public class InternalUserController {

    private static final Logger LOGGER = Logger.getLogger(InternalUserController.class);

    @Inject
    UserService userService;

    @Inject
    UserMapper userMapper;

    @GET
    @Path("/{userId}")
    public Response getUserById(@PathParam("userId") UUID userId) {
        LOGGER.debugf("Internal lookup for user ID: %s", userId);
        return userService.findUserByIdInternal(userId)
                .map(user -> Response.ok(userMapper.toResponseDTO(user)).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Path("/batch")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUsersByIds(List<UUID> userIds) {
        LOGGER.debugf("Internal batch lookup for %d users", userIds.size());
        List<UserResponseDTO> users = userService.findUsersByIds(userIds).stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
        return Response.ok(users).build();
    }
}
