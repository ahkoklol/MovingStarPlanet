package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import org.acme.model.Body;
import org.acme.model.SimulationResult;
import java.util.ArrayList;
import java.util.List;

/**
 * This service handles the physics calculations, including distances, accelerations, and updating positions and velocities.
 */
@ApplicationScoped
public class SimulationService {
    private static final double G = 6.67e-6;  // Gravitational constant
    private static final double dt = 0.001;

    public SimulationResult simulate(List<Body> bodies, double totalTime) {
        int n = (int) (totalTime / dt);
        int k = bodies.size();

        List<double[]> positions = new ArrayList<>();
        List<double[]> velocities = new ArrayList<>();
        List<Double> times = new ArrayList<>();

        double[][] x = new double[k][n];
        double[][] y = new double[k][n];
        double[][] vx = new double[k][n];
        double[][] vy = new double[k][n];

        for (int i = 0; i < k; i++) {
            x[i][0] = bodies.get(i).getPosition()[0];
            y[i][0] = bodies.get(i).getPosition()[1];
            vx[i][0] = bodies.get(i).getVelocity()[0];
            vy[i][0] = bodies.get(i).getVelocity()[1];
        }

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < k; j++) {
                double[] acc = acceleration(bodies, x, y, j, i-1);
                x[j][i] = dt * dt * acc[0] * G + x[j][i-1] + dt * vx[j][i-1];
                y[j][i] = dt * dt * acc[1] * G + y[j][i-1] + dt * vy[j][i-1];
                vx[j][i] = (x[j][i] - x[j][i-1]) / dt;
                vy[j][i] = (y[j][i] - y[j][i-1]) / dt;
            }
            times.add(i * dt);
        }

        for (int j = 0; j < k; j++) {
            positions.add(x[j]);
            velocities.add(vx[j]);
        }

        return new SimulationResult(positions, velocities, times);
    }

    private double[] acceleration(List<Body> bodies, double[][] x, double[][] y, int i, int t) {
        double A = 0;
        double B = 0;
        for (int j = 0; j < bodies.size(); j++) {
            if (j == i) continue;
            double dist = Math.sqrt(Math.pow(x[i][t] - x[j][t], 2) + Math.pow(y[i][t] - y[j][t], 2));
            A += (bodies.get(j).getMass() * (x[j][t] - x[i][t])) / Math.pow(dist, 3);
            B += (bodies.get(j).getMass() * (y[j][t] - y[i][t])) / Math.pow(dist, 3);
        }
        return new double[]{A, B};
    }
}