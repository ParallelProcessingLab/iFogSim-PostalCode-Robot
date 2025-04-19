package org.fog.federated;

import org.cloudbus.cloudsim.core.CloudSim;
import org.fog.entities.FogDevice;
import org.fog.utils.FogUtils;

import java.util.*;

public class MainSimulation {

    public static void main(String[] args) {
        try {
        	int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            boolean traceFlag = false;
            CloudSim.init(numUsers, calendar, traceFlag);


            List<SensorNode> sensors = new ArrayList<>();
            List<Integer> sensorIds = new ArrayList<>();
            Random rand = new Random(42);
            int numSensors = 5;
            
            
            IncentiveFogDevice bs = new IncentiveFogDevice("BaseStation", sensorIds);
            bs.setLevel(1);
            bs.setParentId(-1);
            bs.setUplinkLatency(1.0);
            
            for (int i = 0; i < numSensors; i++) {
                double cpuPower = 250 + rand.nextInt(300);
                int dataSize = 100 + rand.nextInt(200);
                double commCost = 5 + rand.nextDouble() * 5;

                SensorNode sensor = new SensorNode("Sensor" + i, i, bs, 1.0,
                        cpuPower, dataSize, commCost);
                sensors.add(sensor);
                sensorIds.add(sensor.getId());
            }
            

            for (SensorNode sensor : sensors) {
                sensor.setGatewayDeviceId(bs.getId());
                sensor.setGatewayDeviceId(bs.getId());
            }

            
            bs.schedule(bs.getId(), 2.0, IncentiveFogDevice.START_ROUND);

            
            

            
            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            System.out.println("\n Federated learning simulation with incentive mechanism completed!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
