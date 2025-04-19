package org.fog.federation;


import org.fog.entities.FogDevice;
import org.fog.federation.FedAvgServer;

import mnist2.MNISTDataset;

import java.util.List;

public class FedAvgFogSim {
    public static void main(String[] args) {
        List<FogDevice> fogDevices = createFogDevices();
        FogDevice cloud = createCloud();
        
        FedAvgServer fedAvgServer = new FedAvgServer(cloud, fogDevices, 0.01);
        
        distributeTrainingData(fogDevices);
        
        int numRounds = 50;
        for (int round = 0; round < numRounds; round++) {
            fedAvgServer.startTrainingRound(round);
            
            FogSimulator.simulate();
        }
    }
    
    private static void distributeTrainingData(List<FogDevice> clients) {
        MNISTDataset dataset = new MNISTDataset("mnist_train.csv", "mnist_test.csv");
        List<List<MNISTDataset.DataPoint>> clientData = dataset.splitData(clients.size(), true);
        
        for (int i = 0; i < clients.size(); i++) {
            FedAvgClient client = new FedAvgClient(clients.get(i), clientData.get(i));
            clients.get(i).setFedAvgClient(client);
        }
    }
}