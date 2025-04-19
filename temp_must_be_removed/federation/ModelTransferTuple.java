package org.fog.federation;


import org.fog.utils.FogUtils;

import mnist2.NeuralNetwork;

public class ModelTransferTuple {
    private NeuralNetwork model;
    private int sourceId;
    private int destinationId;
    private long dataSize;
    
    public ModelTransferTuple(NeuralNetwork model, int sourceId, int destinationId) {
        this.model = model;
        this.sourceId = sourceId;
        this.destinationId = destinationId;
        this.dataSize = calculateModelSize(model);
    }
    
    private long calculateModelSize(NeuralNetwork model) {
        return 1024 * 1024;
    }
    
    // Getter methods
}