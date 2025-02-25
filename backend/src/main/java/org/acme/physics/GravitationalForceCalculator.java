package org.acme.physics;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.Body;
import java.util.List;

@ApplicationScoped
@Named("gravitationalForceCalculator")
public class GravitationalForceCalculator implements ForceCalculator {
    private static final double G = 6.67e-6;  // Gravitational constant

    @Override
    public double[] calculate(List<Body> bodies, double[][] x, double[][] y, int i, int t) {
        double A = 0;
        double B = 0;

        // If the current body has zero or negative mass, return zero acceleration
        if (bodies.get(i).getMass() <= 0) return new double[]{0.0, 0.0};

        for (int j = 0; j < bodies.size(); j++) {
            if (j == i) continue;

            // Ignore zero or negative mass
            if (bodies.get(j).getMass() <= 0) continue;

            double dist = Math.sqrt(Math.pow(x[i][t] - x[j][t], 2) + Math.pow(y[i][t] - y[j][t], 2));

            if (dist == 0) {
                throw new IllegalArgumentException("Zero distance between bodies detected.");
            }

            A += (bodies.get(j).getMass() * (x[j][t] - x[i][t])) / Math.pow(dist, 3);
            B += (bodies.get(j).getMass() * (y[j][t] - y[i][t])) / Math.pow(dist, 3);
        }
        return new double[]{A, B};
    }

}
