package org.acme.model;

import org.acme.model.Body;

public class ParticleFactory {

    public static Body createParticle(double mass, double[] position, double[] velocity) {
        if (mass <= 0) {
            throw new IllegalArgumentException("Mass must be positive.");
        }
        if (position.length != 2 || velocity.length != 2) {
            throw new IllegalArgumentException("Position and velocity must have two components.");
        }
        return new Body(mass, position, velocity);
    }
}
