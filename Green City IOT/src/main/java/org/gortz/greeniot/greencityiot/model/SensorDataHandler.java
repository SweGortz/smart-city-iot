package org.gortz.greeniot.greencityiot.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.gortz.greeniot.greencityiot.dto.sensors.SensorNodeKey;
import org.gortz.greeniot.greencityiot.dto.sensors.SensorTypeNode;
import org.gortz.greeniot.greencityiot.dto.sensors.SensorValue;

/**
 * Holder of all sensor the local sensor data
 */
public class SensorDataHandler {
    private String TAG = SensorDataHandler.class.getSimpleName();
    private HashMap<String, HashMap<SensorNodeKey, SensorTypeNode>> map = new HashMap<>();
    private static SensorDataHandler instance = null;

    public static SensorDataHandler getInstance(){
        if(instance == null){
            instance = new SensorDataHandler();
        }
        return instance;
    }

    /**
     * Get all local sensor data of a sensor type
     * @param type to get data of
     * @return collection of sensorTypeNodes with sensor data
     */
    public Collection<SensorTypeNode> getDataByType(String type){
        if(map.get(type) == null)
            return new ArrayList<>();
        else
            return map.get(type).values();
    }

    /**
     * Get sensor type node by type and name
     * @param sensorType of node to retrieve
     * @param nodeKey of node  to retrieve
     * @return SensorTypeNode with sensor values
     */
    public SensorTypeNode getSensorTypeNodeByTypeAndName(String sensorType, SensorNodeKey nodeKey){
        Log.i(TAG, "retrieved node with name: " + nodeKey.getSensorName() + " and sensorType: " + sensorType);
        return map.get(sensorType).get(nodeKey);
    }

    /**
     * Updates local sensor values
     * @param sensorType to update
     * @param sensorNode to update
     */
    public void update(String sensorType, SensorTypeNode sensorNode) {
        if(!map.containsKey(sensorType)){
            map.put(sensorType,new HashMap<SensorNodeKey, SensorTypeNode>());
            map.get(sensorType).put(new SensorNodeKey(sensorNode.getName(), sensorNode.getOrganization()),new SensorTypeNode(sensorNode.getName(),sensorNode.getOrganization(),sensorNode.getLocation()));
        }else if(!map.get(sensorType).containsKey(sensorNode)) {
            map.get(sensorType).put(new SensorNodeKey(sensorNode.getName(), sensorNode.getOrganization()),new SensorTypeNode(sensorNode.getName(),sensorNode.getOrganization(),sensorNode.getLocation()));
        }
        for(SensorValue sensorValue : sensorNode.getValueList()){
            map.get(sensorType).get(new SensorNodeKey(sensorNode.getName(), sensorNode.getOrganization())).addValue(sensorValue);
        }
        //map.get(sensorType).get(new SensorNodeKey(sensorNode.getName(), sensorNode.getOrganization())).addValue(lastRetrievalTimestamp,value);
    }
}
