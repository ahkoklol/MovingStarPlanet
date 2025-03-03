package org.acme.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.Body;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ParticleRepository {
    private final List<Body> particles = Collections.synchronizedList(new ArrayList<>());

    public List<Body> getAllParticles() {
        return new ArrayList<>(particles);
    }

    public void addParticle(Body body) {
        particles.add(body);
    }

    public boolean removeParticle(double[] position) {
        return particles.removeIf(body ->
                body.getPosition()[0] == position[0] && body.getPosition()[1] == position[1]
        );
    }

    public void clearParticles() {
        particles.clear();
    }
}
