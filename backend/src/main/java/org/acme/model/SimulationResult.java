package org.acme.model;

import java.util.List;

public class SimulationResult {
    private List<double[]> positions;
    private List<double[]> velocities;
    private List<Double> times;

    public SimulationResult() {}

    public SimulationResult(List<double[]> positions, List<double[]> velocities, List<Double> times) {
        this.positions = positions;
        this.velocities = velocities;
        this.times = times;
    }

    public List<double[]> getPositions() {
        return positions;
    }

    public void setPositions(List<double[]> positions) {
        this.positions = positions;
    }

    public List<double[]> getVelocities() {
        return velocities;
    }

    public void setVelocities(List<double[]> velocities) {
        this.velocities = velocities;
    }

    public List<Double> getTimes() {
        return times;
    }

    public void setTimes(List<Double> times) {
        this.times = times;
    }
}
