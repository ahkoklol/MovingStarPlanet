package org.acme.physics;

import org.acme.model.Body;
import java.util.List;

public interface ForceCalculator {
    double[] calculate(List<Body> bodies, double[][] x, double[][] y, int i, int t);
}
