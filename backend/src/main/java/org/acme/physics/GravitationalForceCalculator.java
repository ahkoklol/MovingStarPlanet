package org.acme.physics;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.Body;
import java.util.List;

@ApplicationScoped
public class GravitationalForceCalculator implements ForceCalculator {
    private static final double G = 6.67e-6;  // Gravitational constant

    @Override
    public double[] calculate(List<Body> bodies, double[][] x, double[][] y, int i, int t) {
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
