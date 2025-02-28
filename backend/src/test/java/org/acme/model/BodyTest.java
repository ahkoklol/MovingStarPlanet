package org.acme.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BodyTest {

    @Test
    void testDefaultConstructor() {
        Body body = new Body();
        assertEquals(0.0, body.getMass());
        assertNull(body.getPosition());
        assertNull(body.getVelocity());
    }

    @Test
    void testParameterizedConstructor() {
        double mass = 100.0;
        double[] position = {1.0, 2.0};
        double[] velocity = {0.5, -0.5};

        Body body = new Body(mass, position, velocity);

        assertEquals(mass, body.getMass());
        assertArrayEquals(position, body.getPosition());
        assertArrayEquals(velocity, body.getVelocity());
    }

    @Test
    void testMassGetterSetter() {
        Body body = new Body();
        body.setMass(150.0);
        assertEquals(150.0, body.getMass());
    }

    @Test
    void testPositionGetterSetter() {
        Body body = new Body();
        double[] position = {3.0, 4.0};
        body.setPosition(position);
        assertArrayEquals(position, body.getPosition());
    }

    @Test
    void testVelocityGetterSetter() {
        Body body = new Body();
        double[] velocity = {0.1, -0.1};
        body.setVelocity(velocity);
        assertArrayEquals(velocity, body.getVelocity());
    }

    @Test
    void testZeroAndNegativeMass() {
        Body body = new Body();
        body.setMass(0.0);
        assertEquals(0.0, body.getMass());

        body.setMass(-100.0);
        assertEquals(-100.0, body.getMass());
    }

    @Test
    void testNullPositionAndVelocity() {
        Body body = new Body();
        body.setPosition(null);
        body.setVelocity(null);

        assertNull(body.getPosition());
        assertNull(body.getVelocity());
    }

    @Test
    void testIncorrectArrayLengths() {
        Body body = new Body();

        double[] position1 = {1.0};
        double[] velocity1 = {0.1};

        body.setPosition(position1);
        body.setVelocity(velocity1);

        assertEquals(1, body.getPosition().length);
        assertEquals(1, body.getVelocity().length);

        double[] position2 = {1.0, 2.0, 3.0};
        double[] velocity2 = {0.1, 0.2, 0.3};

        body.setPosition(position2);
        body.setVelocity(velocity2);

        assertEquals(3, body.getPosition().length);
        assertEquals(3, body.getVelocity().length);
    }

    @Test
    void testExtremeValues() {
        Body body = new Body();

        double extremeMass = Double.MAX_VALUE;
        double[] extremePosition = {Double.MAX_VALUE, Double.MIN_VALUE};
        double[] extremeVelocity = {-Double.MAX_VALUE, -Double.MIN_VALUE};

        body.setMass(extremeMass);
        body.setPosition(extremePosition);
        body.setVelocity(extremeVelocity);

        assertEquals(extremeMass, body.getMass());
        assertArrayEquals(extremePosition, body.getPosition());
        assertArrayEquals(extremeVelocity, body.getVelocity());
    }
}
