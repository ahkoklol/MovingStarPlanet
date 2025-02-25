package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.acme.model.Body;
import org.acme.model.SimulationResult;
import org.acme.physics.ForceCalculator;

@ApplicationScoped
public class SimulationService {
    private static final double dt = 0.001;

    @Inject
    @Named("gravitationalForceCalculator")
    ForceCalculator forceCalculator;  // Decoupled dependency

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
                double[] acc = forceCalculator.calculate(bodies, x, y, j, i - 1);  // Using injected strategy
                x[j][i] = dt * dt * acc[0] + x[j][i-1] + dt * vx[j][i-1];
                y[j][i] = dt * dt * acc[1] + y[j][i-1] + dt * vy[j][i-1];
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
}
