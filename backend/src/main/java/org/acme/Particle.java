package com.example.model;

public class Particle {

    public double x, y, vx, vy, mass;

    public Particle(double x, double y, double vx, double vy, double mass) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
    }

    public void update(double dt, double ax, double ay) {
        vx += ax * dt;
        vy += ay * dt;
        x += vx * dt;
        y += vy * dt;
    }
}
