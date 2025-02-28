package org.acme.physics;

import org.acme.model.Body;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GravitationalForceCalculatorTest {

    GravitationalForceCalculator calculator = new GravitationalForceCalculator();

    @Test
    void testBasicCalculation() {
        List<Body> bodies = new ArrayList<>();
        bodies.add(new Body(100.0, new double[]{0.0, 0.0}, new double[]{0.0, 0.0}));
        bodies.add(new Body(1.0, new double[]{1.0, 0.0}, new double[]{0.0, 0.0}));

        double[][] x = {
                {0.0, 0.0},
                {1.0, 1.0}
        };
        double[][] y = {
                {0.0, 0.0},
                {0.0, 0.0}
        };

        double[] acc = calculator.calculate(bodies, x, y, 1, 0);

        assertTrue(acc[0] < 0);  // Acceleration should be towards the larger mass
        assertEquals(0.0, acc[1], 1e-9); // No Y acceleration
    }

    @Test
    void testZeroDistance() {
        List<Body> bodies = new ArrayList<>();
        bodies.add(new Body(100.0, new double[]{0.0, 0.0}, new double[]{0.0, 0.0}));
        bodies.add(new Body(1.0, new double[]{0.0, 0.0}, new double[]{0.0, 0.0}));

        double[][] x = {
                {0.0, 0.0},
                {0.0, 0.0}
        };
        double[][] y = {
                {0.0, 0.0},
                {0.0, 0.0}
        };

        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculate(bodies, x, y, 1, 0);
        });
    }

    @Test
    void testZeroAndNegativeMass() {
        List<Body> bodies = new ArrayList<>();
        bodies.add(new Body(0.0, new double[]{0.0, 0.0}, new double[]{0.0, 0.0}));
        bodies.add(new Body(-1.0, new double[]{1.0, 0.0}, new double[]{0.0, 0.0}));
        bodies.add(new Body(100.0, new double[]{2.0, 0.0}, new double[]{0.0, 0.0}));

        double[][] x = {
                {0.0, 0.0},
                {1.0, 1.0},
                {2.0, 2.0}
        };
        double[][] y = {
                {0.0, 0.0},
                {0.0, 0.0},
                {0.0, 0.0}
        };

        double[] acc = calculator.calculate(bodies, x, y, 0, 0);

        assertEquals(0.0, acc[0], 1e-9);
        assertEquals(0.0, acc[1], 1e-9);
    }

    @Test
    void testMultipleBodies() {
        List<Body> bodies = new ArrayList<>();
        bodies.add(new Body(100.0, new double[]{0.0, 0.0}, new double[]{0.0, 0.0}));
        bodies.add(new Body(1.0, new double[]{1.0, 0.0}, new double[]{0.0, 0.0}));
        bodies.add(new Body(1.0, new double[]{0.0, 1.0}, new double[]{0.0, 0.0}));

        double[][] x = {
                {0.0, 0.0},
                {1.0, 1.0},
                {0.0, 0.0}
        };
        double[][] y = {
                {0.0, 0.0},
                {0.0, 0.0},
                {1.0, 1.0}
        };

        double[] acc = calculator.calculate(bodies, x, y, 1, 0);

        assertTrue(acc[0] < 0);  // Acceleration towards the larger mass
        assertTrue(acc[1] > 0);  // Acceleration due to the third body
    }

    @Test
    void testExtremeValues() {
        List<Body> bodies = new ArrayList<>();
        bodies.add(new Body(Double.MAX_VALUE, new double[]{0.0, 0.0}, new double[]{0.0, 0.0}));
        bodies.add(new Body(1.0, new double[]{1.0, 0.0}, new double[]{0.0, 0.0}));

        double[][] x = {
                {0.0, 0.0},
                {1.0, 1.0}
        };
        double[][] y = {
                {0.0, 0.0},
                {0.0, 0.0}
        };

        double[] acc = calculator.calculate(bodies, x, y, 1, 0);

        assertTrue(acc[0] < 0);  // Acceleration should be towards the extremely large mass
        assertEquals(0.0, acc[1], 1e-9); // No Y acceleration
    }

    @Test
    void testSingleBody() {
        List<Body> bodies = new ArrayList<>();
        bodies.add(new Body(100.0, new double[]{0.0, 0.0}, new double[]{0.0, 0.0}));

        double[][] x = {
                {0.0, 0.0}
        };
        double[][] y = {
                {0.0, 0.0}
        };

        double[] acc = calculator.calculate(bodies, x, y, 0, 0);

        assertEquals(0.0, acc[0], 1e-9);
        assertEquals(0.0, acc[1], 1e-9);
    }
}
