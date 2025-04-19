package org.fog.federated;

import org.fog.entities.Sensor;
import org.fog.entities.FogDevice;
import org.fog.utils.FogEvents;
import org.fog.utils.FogUtils;
import org.fog.utils.distribution.DeterministicDistribution;
import org.fog.utils.distribution.Distribution;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SensorNode extends Sensor {

    private double cpuPower;
    private int localDataSize;
    private double communicationCost;
    private int trainingIterations;  
    private Random rand;

    private List<Double> localWeights;

    public static final int RECEIVE_REWARD = 2000;
    public static final int START_LOCAL_TRAINING = 2001;

    public SensorNode(String name, int id, FogDevice parentDevice, double uplinkLatency,
                      double cpuPower, int localDataSize, double communicationCost) {
//    	Sensor(String name, String tupleType, int userId, String appId, Distribution transmitDistribution)
        super(name,  name + "_app", parentDevice.getId(),"1", new DeterministicDistribution(5));
        this.cpuPower = cpuPower;
        this.localDataSize = localDataSize;
        this.communicationCost = communicationCost;
        this.rand = new Random(id);
        this.localWeights = new ArrayList<>();
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case RECEIVE_REWARD:
                double rewardRate = (double) ev.getData();
                handleRewardRate(rewardRate);
                break;

            case START_LOCAL_TRAINING:
                startLocalTraining();
                break;

            default:
                super.processEvent(ev);
        }
    }

    private void handleRewardRate(double rewardRate) {

        trainingIterations = Math.max(1, (int) (rewardRate * 10));
        System.out.println(getName() + " rate: " + rewardRate +
                "local repeated" + trainingIterations);
        schedule(getId(), CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST, START_LOCAL_TRAINING);
    }

    private void startLocalTraining() {
        localWeights.clear();
        for (int i = 0; i < 10; i++) {
            localWeights.add(rand.nextDouble());
        }

        double accuracy = 1.0 - (1.0 / (trainingIterations + 1)) + rand.nextDouble() * 0.05;

        ModelParameters params = new ModelParameters(localWeights, accuracy, localDataSize);

        System.out.println(getName() + " acc: " + String.format("%.4f", accuracy));

        send(getGatewayDeviceId(), getLatency(), CloudSimTags.RETURN_ACC_STATISTICS_BY_CATEGORY, params);
    }

    public List<Double> getLocalWeights() {
        return localWeights;
    }

    public int getLocalDataSize() {
        return localDataSize;
    }
}
