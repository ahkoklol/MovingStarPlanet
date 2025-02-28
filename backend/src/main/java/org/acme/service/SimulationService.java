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
    ForceCalculator forceCalculator;  // Decoupled dependency

    public SimulationResult simulate(List<Body> bodies, double totalTime) {
        if (bodies == null || bodies.isEmpty() || totalTime < 0) {
            return new SimulationResult(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        // Check for negative mass
        for (Body body : bodies) {
            if (body.getMass() <= 0) {
                throw new IllegalArgumentException("Mass must be positive.");
            }
        }

        List<double[]> positions = new ArrayList<>();
        List<double[]> velocities = new ArrayList<>();
        List<Double> times = new ArrayList<>();
        int k = bodies.size();

        // Special case: totalTime is 0.0
        if (totalTime == 0.0) {
            times.add(0.0);
            for (int j = 0; j < k; j++) {
                positions.add(new double[]{bodies.get(j).getPosition()[0]});
                velocities.add(new double[]{bodies.get(j).getVelocity()[0]});
            }
            return new SimulationResult(positions, velocities, times);
        }

        int n = Math.max(1, (int) Math.ceil(totalTime / dt));

        double[][] x = new double[k][n + 1];
        double[][] y = new double[k][n + 1];
        double[][] vx = new double[k][n + 1];
        double[][] vy = new double[k][n + 1];

        // Initialize positions and velocities
        for (int i = 0; i < k; i++) {
            x[i][0] = bodies.get(i).getPosition()[0];
            y[i][0] = bodies.get(i).getPosition()[1];
            vx[i][0] = bodies.get(i).getVelocity()[0];
            vy[i][0] = bodies.get(i).getVelocity()[1];
        }

        // Perform simulation
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < k; j++) {
                double[] acc = forceCalculator.calculate(bodies, x, y, j, i - 1);
                x[j][i] = dt * dt * acc[0] + x[j][i-1] + dt * vx[j][i-1];
                y[j][i] = dt * dt * acc[1] + y[j][i-1] + dt * vy[j][i-1];
                vx[j][i] = (x[j][i] - x[j][i-1]) / dt;
                vy[j][i] = (y[j][i] - y[j][i-1]) / dt;
            }
            times.add(i * dt);
        }

        // Add final positions and velocities
        for (int j = 0; j < k; j++) {
            positions.add(x[j]);
            velocities.add(vx[j]);
        }

        return new SimulationResult(positions, velocities, times);
    }
}
