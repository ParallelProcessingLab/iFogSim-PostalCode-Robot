package org.fog.federation;

import org.fog.entities.FogDevice;

import mnist2.NeuralNetwork;

import java.util.List;

public class FedAvgServer {
    private NeuralNetwork globalModel;
    private FogDevice hostDevice;
    private List<FogDevice> clients;
    
    public FedAvgServer(FogDevice hostDevice, List<FogDevice> clients, double learningRate) {
        this.hostDevice = hostDevice;
        this.clients = clients;
        this.globalModel = new NeuralNetwork(learningRate);
    }
    
    public void federatedAveraging(List<NeuralNetwork> clientModels) {
       globalModel.resetWeightsToZero();
        
        for (NeuralNetwork model : clientModels) {
            globalModel.addModelWeights(model);
        }
        
        globalModel.divideWeights(clientModels.size());
    }
    
    public void startTrainingRound(int round) {
        List<FogDevice> selectedClients = selectClientsForRound(round);
        
        for (FogDevice client : selectedClients) {
            sendModelToClient(client, globalModel.copy());
        }
    }
    
    private List<FogDevice> selectClientsForRound(int round) {
        return clients.subList(0, (int)(clients.size() * 0.3));
    }
    
    private void sendModelToClient(FogDevice client, NeuralNetwork model) {
        
    }
}