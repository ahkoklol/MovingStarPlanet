package org.acme.resource;

import org.acme.model.Body;
import org.acme.model.SimulationResult;
import org.acme.repository.ParticleRepository;
import org.acme.service.SimulationService;
import org.acme.websocket.ParticleWebSocket;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/simulation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulationResource {

    @Inject
    SimulationService simulationService;

    @Inject
    ParticleRepository particleRepository;  // Inject repository

    @Inject
    ParticleWebSocket particleWebSocket; // Inject WebSocket broadcaster

    @GET
    @Path("/init")
    public List<Body> initialize() {
        return particleRepository.getAllParticles(); // Return stored particles
    }

    @POST
    @Path("/create")
    public Response createParticle(Body body) {
        if (body.getMass() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Mass must be positive.").build();
        }
        if (body.getPosition().length != 2 || body.getVelocity().length != 2) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Position and velocity must have two components.").build();
        }

        particleRepository.addParticle(body);
        particleWebSocket.broadcastParticles(); // Broadcast updates

        return Response.status(Response.Status.CREATED).entity(body).build();
    }

    @DELETE
    @Path("/delete")
    public Response deleteParticle(@QueryParam("x") double x, @QueryParam("y") double y) {
        boolean removed = particleRepository.removeParticle(new double[]{x, y});
        if (!removed) {
            return Response.status(Response.Status.NOT_FOUND).entity("Particle not found.").build();
        }

        particleWebSocket.broadcastParticles(); // Notify all clients
        return Response.ok("Particle deleted successfully.").build();
    }


    @DELETE
    @Path("/clear")
    public Response clearAllParticles() {
        particleRepository.clearParticles();
        particleWebSocket.broadcastParticles(); // Notify all clients

        return Response.ok("All particles cleared successfully.").build();
    }

    @POST
    public Response simulate(@QueryParam("totalTime") @DefaultValue("5.0") double totalTime, List<Body> bodies) {
        if (bodies == null || bodies.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Body list cannot be null or empty.").build();
        }

        try {
            SimulationResult result = simulationService.simulate(bodies, totalTime);
            return Response.ok(result).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
