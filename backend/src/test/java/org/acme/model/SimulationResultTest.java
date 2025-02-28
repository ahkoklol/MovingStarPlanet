package org.acme.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class SimulationResultTest {

    @Test
    void testDefaultConstructor() {
        SimulationResult result = new SimulationResult();
        assertNull(result.getPositions());
        assertNull(result.getVelocities());
        assertNull(result.getTimes());
    }

    @Test
    void testParameterizedConstructor() {
        List<double[]> positions = new ArrayList<>();
        List<double[]> velocities = new ArrayList<>();
        List<Double> times = new ArrayList<>();

        positions.add(new double[]{1.0, 2.0});
        velocities.add(new double[]{0.5, -0.5});
        times.add(1.0);

        SimulationResult result = new SimulationResult(positions, velocities, times);

        assertEquals(positions, result.getPositions());
        assertEquals(velocities, result.getVelocities());
        assertEquals(times, result.getTimes());
    }

    @Test
    void testPositionsGetterSetter() {
        SimulationResult result = new SimulationResult();
        List<double[]> positions = new ArrayList<>();
        positions.add(new double[]{3.0, 4.0});

        result.setPositions(positions);

        assertEquals(positions, result.getPositions());
    }

    @Test
    void testVelocitiesGetterSetter() {
        SimulationResult result = new SimulationResult();
        List<double[]> velocities = new ArrayList<>();
        velocities.add(new double[]{0.1, -0.1});

        result.setVelocities(velocities);

        assertEquals(velocities, result.getVelocities());
    }

    @Test
    void testTimesGetterSetter() {
        SimulationResult result = new SimulationResult();
        List<Double> times = new ArrayList<>();
        times.add(5.0);

        result.setTimes(times);

        assertEquals(times, result.getTimes());
    }

    @Test
    void testEmptyLists() {
        SimulationResult result = new SimulationResult();
        result.setPositions(new ArrayList<>());
        result.setVelocities(new ArrayList<>());
        result.setTimes(new ArrayList<>());

        assertTrue(result.getPositions().isEmpty());
        assertTrue(result.getVelocities().isEmpty());
        assertTrue(result.getTimes().isEmpty());
    }

    @Test
    void testNullElementsInLists() {
        SimulationResult result = new SimulationResult();

        List<double[]> positions = new ArrayList<>();
        positions.add(null);
        result.setPositions(positions);

        List<double[]> velocities = new ArrayList<>();
        velocities.add(null);
        result.setVelocities(velocities);

        List<Double> times = new ArrayList<>();
        times.add(null);
        result.setTimes(times);

        assertNull(result.getPositions().get(0));
        assertNull(result.getVelocities().get(0));
        assertNull(result.getTimes().get(0));
    }

    @Test
    void testVaryingLengths() {
        SimulationResult result = new SimulationResult();

        List<double[]> positions = new ArrayList<>();
        positions.add(new double[]{1.0, 2.0});
        positions.add(new double[]{3.0});
        result.setPositions(positions);

        List<double[]> velocities = new ArrayList<>();
        velocities.add(new double[]{0.1});
        velocities.add(new double[]{0.2, 0.3, 0.4});
        result.setVelocities(velocities);

        List<Double> times = new ArrayList<>();
        times.add(1.0);
        result.setTimes(times);

        assertEquals(2, result.getPositions().size());
        assertEquals(2, result.getVelocities().size());
        assertEquals(1, result.getTimes().size());
    }

    @Test
    void testExtremeValues() {
        SimulationResult result = new SimulationResult();

        List<double[]> positions = new ArrayList<>();
        positions.add(new double[]{Double.MAX_VALUE, Double.MIN_VALUE});
        result.setPositions(positions);

        List<double[]> velocities = new ArrayList<>();
        velocities.add(new double[]{-Double.MAX_VALUE, -Double.MIN_VALUE});
        result.setVelocities(velocities);

        List<Double> times = new ArrayList<>();
        times.add(Double.POSITIVE_INFINITY);
        times.add(Double.NEGATIVE_INFINITY);
        result.setTimes(times);

        assertArrayEquals(new double[]{Double.MAX_VALUE, Double.MIN_VALUE}, result.getPositions().get(0));
        assertArrayEquals(new double[]{-Double.MAX_VALUE, -Double.MIN_VALUE}, result.getVelocities().get(0));
        assertEquals(Double.POSITIVE_INFINITY, result.getTimes().get(0));
        assertEquals(Double.NEGATIVE_INFINITY, result.getTimes().get(1));
    }
}
