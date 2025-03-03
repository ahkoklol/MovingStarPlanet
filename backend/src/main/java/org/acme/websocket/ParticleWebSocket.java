package org.acme.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.acme.model.Body;
import org.acme.repository.ParticleRepository;
import jakarta.inject.Inject;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
@ServerEndpoint("/ws/particles")
public class ParticleWebSocket {

    @Inject
    ParticleRepository particleRepository;

    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        sendParticlesToSession(session); // Send initial data when a client connects
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if (message.startsWith("delete:")) {
            String[] parts = message.split(":");
            if (parts.length == 3) {
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                boolean removed = particleRepository.removeParticle(new double[]{x, y});
                if (removed) {
                    broadcastParticles();
                }
            }
        }
    }

    private void sendParticlesToSession(Session session) {
        try {
            String json = objectMapper.writeValueAsString(particleRepository.getAllParticles());
            session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastParticles() {
        try {
            String json = objectMapper.writeValueAsString(particleRepository.getAllParticles());
            for (Session session : sessions) {
                session.getBasicRemote().sendText(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
