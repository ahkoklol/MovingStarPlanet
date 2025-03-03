package org.acme.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.acme.model.Body;
import org.acme.model.SimulationResult;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class SimulationResourceTest {

    @Test
    public void testInitializeEndpoint() {
        // Ensure repository has at least 4 bodies
        if (given().when().get("/api/simulation/init").then().extract().as(new TypeRef<List<Body>>() {}).isEmpty()) {
            Body body1 = new Body(100, new double[]{0, 0}, new double[]{0, 0});
            Body body2 = new Body(50, new double[]{1, 1}, new double[]{0, 0});
            Body body3 = new Body(75, new double[]{2, 2}, new double[]{0, 0});
            Body body4 = new Body(90, new double[]{3, 3}, new double[]{0, 0});

            given().contentType(ContentType.JSON).body(body1).when().post("/api/simulation/create").then().statusCode(201);
            given().contentType(ContentType.JSON).body(body2).when().post("/api/simulation/create").then().statusCode(201);
            given().contentType(ContentType.JSON).body(body3).when().post("/api/simulation/create").then().statusCode(201);
            given().contentType(ContentType.JSON).body(body4).when().post("/api/simulation/create").then().statusCode(201);
        }

        List<Body> bodies = given()
                .when().get("/api/simulation/init")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(new TypeRef<>() {});

        assertTrue(bodies.size() >= 4); // Ensure at least 4 bodies exist
    }

    @Test
    public void testDeleteParticle() {
        // Create a particle first
        Body newParticle = new Body(5, new double[]{2, 3}, new double[]{0.2, -0.1});
        given()
                .contentType(ContentType.JSON)
                .body(newParticle)
                .when().post("/api/simulation/create")
                .then()
                .statusCode(201);

        // Now delete the particle using query parameters
        given()
                .queryParam("x", 2.0)
                .queryParam("y", 3.0)
                .when().delete("/api/simulation/delete")
                .then()
                .statusCode(200)
                .body(containsString("Particle deleted successfully."));
    }

    @Test
    public void testClearAllParticles() {
        // Create two particles
        Body particle1 = new Body(5, new double[]{2, 3}, new double[]{0.2, -0.1});
        Body particle2 = new Body(3, new double[]{4, 5}, new double[]{0.1, 0.2});

        given()
                .contentType(ContentType.JSON)
                .body(particle1)
                .when().post("/api/simulation/create")
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(particle2)
                .when().post("/api/simulation/create")
                .then()
                .statusCode(201);

        // Clear all particles
        given()
                .when().delete("/api/simulation/clear")
                .then()
                .statusCode(200)
                .body(containsString("All particles cleared successfully."));
    }

    @Test
    public void testCreateValidParticle() {
        Body newParticle = new Body(5, new double[]{2, 3}, new double[]{0.2, -0.1});

        given()
                .contentType(ContentType.JSON)
                .body(newParticle)
                .when().post("/api/simulation/create")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON);
    }

    @Test
    public void testCreateParticleWithNegativeMass() {
        Body invalidParticle = new Body(-10, new double[]{0, 0}, new double[]{1, 1});

        given()
                .contentType(ContentType.JSON)
                .body(invalidParticle)
                .when().post("/api/simulation/create")
                .then()
                .statusCode(400)
                .body(containsString("Mass must be positive."));
    }

    @Test
    public void testSimulationAfterCreatingParticle() {
        Body newParticle = new Body(5, new double[]{2, 3}, new double[]{0.2, -0.1});

        given()
                .contentType(ContentType.JSON)
                .body(newParticle)
                .when().post("/api/simulation/create")
                .then()
                .statusCode(201);

        // Fetch updated list of bodies before asserting count
        List<Body> bodies = given()
                .when().get("/api/simulation/init")
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<>() {});

        assertTrue(bodies.size() > 4); // Ensure at least one new particle was added

        SimulationResult result = given()
                .contentType(ContentType.JSON)
                .queryParam("totalTime", 1.0)
                .body(bodies)
                .when().post("/api/simulation")
                .then()
                .statusCode(200)
                .extract().as(SimulationResult.class);

        assertEquals(bodies.size(), result.getPositions().size()); // Ensure all particles were simulated
    }

}
