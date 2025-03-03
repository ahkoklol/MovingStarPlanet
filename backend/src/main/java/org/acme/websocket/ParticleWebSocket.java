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
        System.out.println("Received WebSocket message: " + message);
    }

    private void sendParticlesToSession(Session session) {
        try {
            session.getBasicRemote().sendText(particleRepository.getAllParticles().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastParticles() {
        String particlesJson = particleRepository.getAllParticles().toString();
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText(particlesJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
