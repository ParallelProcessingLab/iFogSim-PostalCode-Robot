package org.fog.federation;

import org.fog.entities.FogDevice;

import mnist2.MNISTDataset;
import mnist2.NeuralNetwork;

import java.util.List;

public class FedAvgClient {
    private FogDevice hostDevice;
    private NeuralNetwork localModel;
    private List<MNISTDataset.DataPoint> localData;
    
    public FedAvgClient(FogDevice hostDevice, List<MNISTDataset.DataPoint> trainingData) {
        this.hostDevice = hostDevice;
        this.localData = trainingData;
    }
    
    public void receiveGlobalModel(NeuralNetwork globalModel) {
        this.localModel = globalModel.copy();
        startLocalTraining();
    }
    
    private void startLocalTraining() {
        
        
        int localEpochs = 3;
        int batchSize = 32;
        
        for (int epoch = 0; epoch < localEpochs; epoch++) {
            for (MNISTDataset.DataPoint data : localData) {
                localModel.train(data.pixels, data.label);
            }
        }
        
        sendModelToServer();
    }
    
    private void sendModelToServer() {
       
    }
}