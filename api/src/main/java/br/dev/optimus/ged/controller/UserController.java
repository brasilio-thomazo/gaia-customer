package br.dev.optimus.ged.controller;

import br.dev.optimus.ged.model.User;
import br.dev.optimus.ged.repository.UserRepository;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {
    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GET
    public Response index() {
        return Response.ok(repository.listAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response show(Long id) {
        return Response.ok(repository.findById(id)).build();
    }

    @POST
    public Response store(User.DTO dto) {
        return Response.status(Response.Status.CREATED)
                .entity(repository.create(dto))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(Long id, User.DTO dto) {
        return Response.ok(repository.update(id, dto)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response destroy(Long id) {
        repository.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("/{id}/restore")
    public Response restore(Long id) {
        return Response.ok(repository.restore(id)).build();
    }

}
