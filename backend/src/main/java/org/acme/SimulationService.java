package com.example.service;

import com.example.model.Particle;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SimulationService {

    private static final double G = 6.67430e-11; // Gravitational constant
    private List<Particle> particles = new ArrayList<>();

    public SimulationService() {
        // Initialize particles (example: Earth & Moon)
        particles.add(new Particle(100, 100, 0, 0, 5.972e24));  // Earth
        particles.add(new Particle(200, 100, 0, 1, 7.348e22));  // Moon
    }

    // Returns the list of particles in the simulation
    public List<Particle> getParticles() {
        return particles;
    }

    // Advances the simulation by a specified time step
    public void step(double dt) {
        int n = particles.size();
        double[] ax = new double[n];
        double[] ay = new double[n];

        // Compute forces for each particle
        for (int i = 0; i < n; i++) {
            Particle p1 = particles.get(i);
            ax[i] = 0;
            ay[i] = 0;

            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                Particle p2 = particles.get(j);

                double dx = p2.x - p1.x;
                double dy = p2.y - p1.y;
                double dist = Math.sqrt(dx * dx + dy * dy) + 1e-10; // Prevent division by zero
                double force = G * p1.mass * p2.mass / (dist * dist);

                ax[i] += force * dx / (p1.mass * dist);
                ay[i] += force * dy / (p1.mass * dist);
            }
        }

        // Update positions based on forces
        for (int i = 0; i < n; i++) {
            particles.get(i).update(dt, ax[i], ay[i]);
        }
    }
}
