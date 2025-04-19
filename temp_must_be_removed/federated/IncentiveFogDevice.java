package org.fog.federated;

import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.models.PowerModelCubic;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;

import java.util.*;

public class IncentiveFogDevice extends FogDevice {

    private List<Integer> registeredSensors;
    private List<ModelParameters> collectedModels;
    private double rewardRate = 0.2;
    private double targetAccuracy = 0.9;
    private double globalAccuracy = 0.0;

    public static final int START_ROUND = 3000;

    public IncentiveFogDevice(String name, List<Integer> sensorIds) throws Exception {
        super(name,1,1,1,1,1,new PowerModelCubic(1, 1));
        this.registeredSensors = sensorIds;
        this.collectedModels = new ArrayList<>();
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case START_ROUND:
                startTrainingRound();
                break;

            case CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST:
                ModelParameters params = (ModelParameters) ev.getData();
                handleReceivedModel(params);
                break;

            default:
                super.processEvent(ev);
        }
    }

    private void startTrainingRound() {
        collectedModels.clear();

        System.out.println("\n=== Start of a new round of federal education ===");
        System.out.println("Send reward rate: " + String.format("%.4f", rewardRate));

        for (int sensorId : registeredSensors) {
            send(sensorId, 2, SensorNode.RECEIVE_REWARD, rewardRate);
        }
    }

    private void handleReceivedModel(ModelParameters params) {
        collectedModels.add(params);
        System.out.println(" Get local model (accuracy: " + String.format("%.4f", params.getAccuracy()) + ")");

        if (collectedModels.size() == registeredSensors.size()) {
            aggregateModelsAndEvaluate();
            updateRewardRate();

            if (globalAccuracy >= targetAccuracy) {
                System.out.println("\n Target accuracy achieved! Training completed.");
            } else {

                schedule(getId(), 3.0, START_ROUND);
            }
        }
    }

    private void aggregateModelsAndEvaluate() {
        int modelSize = collectedModels.get(0).getWeights().size();
        List<Double> aggregated = new ArrayList<>(Collections.nCopies(modelSize, 0.0));
        int totalData = 0;

        for (ModelParameters mp : collectedModels) {
            int dataSize = mp.getDataSize();
            List<Double> weights = mp.getWeights();
            totalData += dataSize;

            for (int i = 0; i < modelSize; i++) {
                aggregated.set(i, aggregated.get(i) + weights.get(i) * dataSize);
            }
        }

        for (int i = 0; i < modelSize; i++) {
            aggregated.set(i, aggregated.get(i) / totalData);
        }


        double sum = 0.0;
        for (double w : aggregated) sum += w;
        globalAccuracy = Math.min(1.0, 0.8 + 0.2 * (sum / modelSize));

        System.out.println(" Global model accuracy: " + String.format("%.4f", globalAccuracy));
    }

    private void updateRewardRate() {
        if (globalAccuracy < targetAccuracy) {
            rewardRate += 0.05;
        } else {
            rewardRate = Math.max(0.1, rewardRate - 0.02);
        }
    }
}
