package org.gortz.greeniot.smartcityiot.fragments;

import java.util.Collection;

import org.gortz.greeniot.smartcityiot.dto.sensors.SensorNodeKey;
import org.gortz.greeniot.smartcityiot.dto.sensors.SensorTypeNode;
import org.gortz.greeniot.smartcityiot.database.entity.SensorType;
import org.gortz.greeniot.smartcityiot.fragments.sensors.SensorItemFragment;
import org.gortz.greeniot.smartcityiot.fragments.sensors.SensorsFragment;

public interface CommunicationChannelToActivity{
    /**
     * Go to sensor detail
     * @param sensor to show details of
     * @param sensorTypeID to show values of
     * @return new SensorItemFragment created and shown
     */
    SensorItemFragment goToSensorDetails(SensorTypeNode sensor, int sensorTypeID);

    /**
     * Go to sensor list
     * @param items to show in list
     * @param sensorTypeID to show values of
     * @return new SensorsFragment created and shown
     */
    SensorsFragment goToSensorsList(Collection<SensorTypeNode> items, int sensorTypeID);

    /**
     * Get sensor data of type
     * @param sensorTypeId to show values of
     * @return collection of sensorTypeNodes with sensor data
     */
    Collection<SensorTypeNode> getSensorDataOfType(int sensorTypeId);

    /**
     * Get sensor type by id
     * @param id of sensor type
     * @return sensor type
     */
    SensorType getSensorTypeByID(int id);

    /**
     * Get sensor node of sensor type and sensor key
     * @param nodeKey of node
     * @param sensorTypeID ot type
     * @return SensorTypeNode with sensor data
     */
    SensorTypeNode getSensorNodeOfTypeByID(SensorNodeKey nodeKey, int sensorTypeID);

    /**
     * Get sensor data of type with coordinates
     * @param sensorTypeID of sensor type
     * @return collection of sensorTypeNodes with sensor data
     */
    Collection<SensorTypeNode> getSensorDataOfTypeWithCoordinate(int sensorTypeID);
}
