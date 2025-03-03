package org.acme.model;

import org.acme.model.Body;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParticleFactoryTest {

    @Test
    public void testCreateValidParticle() {
        Body particle = ParticleFactory.createParticle(10, new double[]{1, 2}, new double[]{0.5, -0.5});
        assertNotNull(particle);
        assertEquals(10, particle.getMass());
        assertArrayEquals(new double[]{1, 2}, particle.getPosition());
        assertArrayEquals(new double[]{0.5, -0.5}, particle.getVelocity());
    }

    @Test
    public void testCreateParticleWithNegativeMass() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ParticleFactory.createParticle(-5, new double[]{1, 2}, new double[]{0, 0})
        );
        assertEquals("Mass must be positive.", exception.getMessage());
    }

    @Test
    public void testCreateParticleWithInvalidPositionArray() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ParticleFactory.createParticle(5, new double[]{1}, new double[]{0, 0})
        );
        assertEquals("Position and velocity must have two components.", exception.getMessage());
    }

    @Test
    public void testCreateParticleWithInvalidVelocityArray() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ParticleFactory.createParticle(5, new double[]{1, 2}, new double[]{0})
        );
        assertEquals("Position and velocity must have two components.", exception.getMessage());
    }
}
