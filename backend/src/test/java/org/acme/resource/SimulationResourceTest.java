package org.acme.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.acme.model.Body;
import org.acme.model.SimulationResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class SimulationResourceTest {

    @Test
    void testInitialize() {
        List<Body> bodies = given()
                .when().get("/api/simulation/init")
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList(".", Body.class);

        assertEquals(4, bodies.size());

        // Check the first body's properties
        assertEquals(100000, bodies.get(0).getMass());
        assertArrayEquals(new double[]{0.0, 0.0}, bodies.get(0).getPosition());
        assertArrayEquals(new double[]{0.0, 0.0}, bodies.get(0).getVelocity());

        // Check the second body's properties
        assertEquals(1, bodies.get(1).getMass());
        assertArrayEquals(new double[]{0.0, 1.0}, bodies.get(1).getPosition());
        assertArrayEquals(new double[]{0.0, 0.1}, bodies.get(1).getVelocity());
    }

    @Test
    void testSimulate() {
        List<Body> bodies = List.of(
                new Body(100000, new double[]{0, 0}, new double[]{0, 0}),
                new Body(1, new double[]{0, 1}, new double[]{0, 0.1}),
                new Body(1, new double[]{1, 0}, new double[]{0, 0}),
                new Body(1, new double[]{1, 1}, new double[]{0, -0.1})
        );

        SimulationResult result = given()
                .contentType("application/json")
                .body(bodies)
                .queryParam("totalTime", 5.0)
                .when().post("/api/simulation")
                .then()
                .statusCode(200)
                .extract().as(SimulationResult.class);

        // Basic checks on result structure
        assertNotNull(result);
        assertNotNull(result.getPositions());
        assertNotNull(result.getVelocities());
        assertNotNull(result.getTimes());

        // Check that positions and velocities are returned for all bodies
        assertEquals(4, result.getPositions().size());
        assertEquals(4, result.getVelocities().size());
    }

    @Test
    void testSimulateNoBodies() {
        List<Body> bodies = List.of();  // Empty list

        SimulationResult result = given()
                .contentType("application/json")
                .body(bodies)
                .queryParam("totalTime", 5.0)
                .when().post("/api/simulation")
                .then()
                .statusCode(200)
                .extract().as(SimulationResult.class);

        // Result should have no positions, velocities, or times
        assertNotNull(result);
        assertTrue(result.getPositions().isEmpty());
        assertTrue(result.getVelocities().isEmpty());
        assertTrue(result.getTimes().isEmpty());
    }

    @Test
    void testSimulateNegativeTotalTime() {
        List<Body> bodies = List.of(
                new Body(100000, new double[]{0, 0}, new double[]{0, 0}),
                new Body(1, new double[]{0, 1}, new double[]{0, 0.1}),
                new Body(1, new double[]{1, 0}, new double[]{0, 0}),
                new Body(1, new double[]{1, 1}, new double[]{0, -0.1})
        );

        given()
                .contentType("application/json")
                .body(bodies)
                .queryParam("totalTime", -5.0)
                .when().post("/api/simulation")
                .then()
                .statusCode(400)
                .body("message", containsString("Total time must be positive"));
    }
}
