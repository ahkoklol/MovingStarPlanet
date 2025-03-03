package org.acme.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import jakarta.websocket.*;
import org.acme.model.Body;
import org.junit.jupiter.api.*;
import org.acme.model.SimulationResult;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class SimulationResourceTest {

    private static final String WEBSOCKET_URI = "ws://localhost:8080/ws/particles";
    private static WebSocketContainer container;
    private static Session session;
    private static BlockingQueue<String> messages;

    @BeforeAll
    public static void setupWebSocket() throws Exception {
        container = ContainerProvider.getWebSocketContainer();
        messages = new LinkedBlockingQueue<>();
        session = container.connectToServer(new Endpoint() {
            @Override
            public void onOpen(Session session, EndpointConfig config) {
                session.addMessageHandler((MessageHandler.Whole<String>) messages::offer);
            }
        }, URI.create(WEBSOCKET_URI));
    }

    @AfterAll
    public static void closeWebSocket() throws Exception {
        if (session != null) {
            session.close();
        }
    }

    @Test
    public void testInitializeEndpoint() {
        List<Body> bodies = given()
                .when().get("/api/simulation/init")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(new TypeRef<>() {});

        assertEquals(4, bodies.size()); // Default 4 bodies exist
    }

    @Test
    public void testCreateValidParticle() throws InterruptedException {
        Body newParticle = new Body(5, new double[]{2, 3}, new double[]{0.2, -0.1});

        given()
                .contentType(ContentType.JSON)
                .body(newParticle)
                .when().post("/api/simulation/create")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON);

        // WebSocket should receive an update
        String message = messages.poll(5, TimeUnit.SECONDS);
        assertNotNull(message);
        assertTrue(message.contains("2.0")); // Check if new particle is broadcasted
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
    public void testSimulationAfterCreatingParticle() throws InterruptedException {
        Body newParticle = new Body(5, new double[]{2, 3}, new double[]{0.2, -0.1});

        given()
                .contentType(ContentType.JSON)
                .body(newParticle)
                .when().post("/api/simulation/create")
                .then()
                .statusCode(201);

        // Ensure WebSocket receives the update
        String message = messages.poll(5, TimeUnit.SECONDS);
        assertNotNull(message);
        assertTrue(message.contains("2.0"));

        List<Body> bodies = given()
                .when().get("/api/simulation/init")
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<>() {});

        assertEquals(5, bodies.size());

        SimulationResult result = given()
                .contentType(ContentType.JSON)
                .queryParam("totalTime", 1.0)
                .body(bodies)
                .when().post("/api/simulation")
                .then()
                .statusCode(200)
                .extract().as(SimulationResult.class);

        assertEquals(5, result.getPositions().size()); // Verify all particles included in simulation
    }
}
