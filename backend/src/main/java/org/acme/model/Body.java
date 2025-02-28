package org.acme.model;

/**
 * This model class represents a body in the simulation with its mass, position, and velocity.
 */
public class Body {
    private double mass;
    private double[] position;
    private double[] velocity;

    public Body() {}

    public Body(double mass, double[] position, double[] velocity) {
        this.mass = mass;
        this.position = position;
        this.velocity = velocity;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    public double[] getVelocity() {
        return velocity;
    }

    public void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }
}