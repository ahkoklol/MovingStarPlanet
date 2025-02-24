package org.acme.model;

public class Body {

    // Position of the particle
    public double x, y;

    // Velocity of the particle
    public double vx, vy;

    // Mass of the particle
    public double mass;

    public Body(double x, double y, double vx, double vy, double mass) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
    }

    // Updates the position and velocity of the particle based on acceleration.
    public void update(double dt, double ax, double ay) {

        // Update velocity using acceleration
        vx += ax * dt;
        vy += ay * dt;

        // Update position using the updated velocity
        x += vx * dt;
        y += vy * dt;
    }
}
