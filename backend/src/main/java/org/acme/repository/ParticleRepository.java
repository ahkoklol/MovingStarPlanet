package org.acme.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.Body;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class ParticleRepository {
    private final List<Body> particles = Collections.synchronizedList(new ArrayList<>());

    public List<Body> getAllParticles() {
        return new ArrayList<>(particles);
    }

    public void addParticle(Body body) {
        particles.add(body);
    }

    public void clearParticles() {
        particles.clear();
    }
}
