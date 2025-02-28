package org.acme.resource;

import org.acme.model.Body;
import org.acme.model.SimulationResult;
import org.acme.service.SimulationService;
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

    @GET
    @Path("/init")
    public List<Body> initialize() {
        return List.of(
                new Body(100000, new double[]{0, 0}, new double[]{0, 0}),
                new Body(1, new double[]{0, 1}, new double[]{0, 0.1}),
                new Body(1, new double[]{1, 0}, new double[]{0, 0}),
                new Body(1, new double[]{1, 1}, new double[]{0, -0.1})
        );
    }

    @POST
    public Response simulate(@QueryParam("totalTime") @DefaultValue("5.0") double totalTime, List<Body> bodies) {
        if (bodies == null || bodies.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Body list cannot be null or empty.")
                    .build();
        }

        try {
            SimulationResult result = simulationService.simulate(bodies, totalTime);
            return Response.ok(result).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
}
