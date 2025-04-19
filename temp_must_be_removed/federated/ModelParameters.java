package org.fog.federated;

import java.util.List;

public class ModelParameters {
    private List<Double> weights;
    private double accuracy;
    private int dataSize;

    public ModelParameters(List<Double> weights, double accuracy, int dataSize) {
        this.weights = weights;
        this.accuracy = accuracy;
        this.dataSize = dataSize;
    }

    public List<Double> getWeights() {
        return weights;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setWeights(List<Double> weights) {
        this.weights = weights;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }
}
