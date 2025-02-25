package org.acme.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.acme.model.Body;
import org.acme.model.SimulationResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class SimulationResourceTest {

    @Test
    public void testInitializeEndpoint() {
        List<Body> bodies = given()
                .when().get("/api/simulation/init")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(new TypeRef<>() {});

        // Debugging: Print the response to see what the endpoint returns
        System.out.println("Response from /api/simulation/init: " + bodies);

        // Additional assertions
        assertEquals(4, bodies.size());
        assertEquals(100000, bodies.get(0).getMass());
        assertEquals(0.0, bodies.get(0).getPosition()[0]);
        assertEquals(0.0, bodies.get(0).getPosition()[1]);
    }


    @Test
    public void testSimulateEndpoint() {
        List<Body> requestBodies = List.of(
                new Body(100000, new double[]{0, 0}, new double[]{0, 0}),
                new Body(1, new double[]{0, 1}, new double[]{0, 0.1}),
                new Body(1, new double[]{1, 0}, new double[]{0, 0}),
                new Body(1, new double[]{1, 1}, new double[]{0, -0.1})
        );

        SimulationResult result = given()
                .contentType(ContentType.JSON)
                .queryParam("totalTime", 1.0)
                .body(requestBodies)
                .when().post("/api/simulation")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("positions.size()", greaterThan(0))
                .body("velocities.size()", greaterThan(0))
                .body("times.size()", greaterThan(0))
                .extract().as(SimulationResult.class);

        // Debugging output for better visibility
        System.out.println("Simulation Result: " + result);

        // Additional assertions
        assertEquals(4, result.getPositions().size());
        assertEquals(4, result.getVelocities().size());
        assertEquals(true, result.getTimes().size() > 0);
    }

    @Test
    public void testSimulateEndpointWithInvalidBody() {
        // Testing with an invalid body with negative mass
        List<Body> invalidBodies = List.of(
                new Body(-100, new double[]{0, 0}, new double[]{0, 0})
        );

        given()
                .contentType(ContentType.JSON)
                .queryParam("totalTime", 1.0)
                .body(invalidBodies)
                .when().post("/api/simulation")
                .then()
                .statusCode(200)  // Ensure the application gracefully handles invalid input
                .body("positions.size()", greaterThan(0))
                .body("velocities.size()", greaterThan(0))
                .body("times.size()", greaterThan(0));
    }
}
