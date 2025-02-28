package org.acme.physics;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.Body;
import java.util.List;

@ApplicationScoped
public interface ForceCalculator {
    double[] calculate(List<Body> bodies, double[][] x, double[][] y, int i, int t);
}
