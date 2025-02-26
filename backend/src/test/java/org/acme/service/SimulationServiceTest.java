package org.acme.service;

import org.acme.model.Body;
import org.acme.model.SimulationResult;
import org.acme.physics.ForceCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SimulationServiceTest {

    @Mock
    ForceCalculator forceCalculator;

    @InjectMocks
    SimulationService simulationService;

    private List<Body> bodies;

    @BeforeEach
    public void setup() {
        bodies = List.of(
                new Body(100000, new double[]{0, 0}, new double[]{0, 0}),
                new Body(1, new double[]{0, 1}, new double[]{0, 0.1}),
                new Body(1, new double[]{1, 0}, new double[]{0, 0}),
                new Body(1, new double[]{1, 1}, new double[]{0, -0.1})
        );
    }

    @Test
    public void testSimulateValidInput() {
        when(forceCalculator.calculate(anyList(), any(double[][].class), any(double[][].class), anyInt(), anyInt()))
                .thenReturn(new double[]{0, 0});

        SimulationResult result = simulationService.simulate(bodies, 1.0);

        assertNotNull(result);
        assertEquals(4, result.getPositions().size());
        assertEquals(4, result.getVelocities().size());
        assertTrue(result.getTimes().size() > 0);

        int expectedSteps = (int) Math.ceil(1.0 / 0.001);
        int actualSteps = result.getTimes().size();
        assertEquals(expectedSteps, actualSteps,
                "Expected steps to be exactly " + expectedSteps + " but was " + actualSteps);
    }

    @Test
    public void testSimulateZeroTotalTime() {
        SimulationResult result = simulationService.simulate(bodies, 0.0);

        assertNotNull(result);
        assertEquals(4, result.getPositions().size());
        assertEquals(4, result.getVelocities().size());
        assertEquals(1, result.getTimes().size());
        assertEquals(0.0, result.getTimes().get(0));
    }

    @Test
    public void testSimulateNoBodies() {
        List<Body> emptyBodies = new ArrayList<>();

        SimulationResult result = simulationService.simulate(emptyBodies, 1.0);

        assertNotNull(result);
        assertEquals(0, result.getPositions().size());
        assertEquals(0, result.getVelocities().size());
        assertEquals(0, result.getTimes().size());
    }

    @Test
    public void testSimulateNegativeMass() {
        List<Body> invalidBodies = List.of(
                new Body(-100, new double[]{0, 0}, new double[]{0, 0}),
                new Body(1, new double[]{1, 1}, new double[]{0, -0.1})
        );

        // Removed unnecessary stubbing because an exception is expected before calling the mock

        // Expect IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            simulationService.simulate(invalidBodies, 1.0);
        });
    }
}
