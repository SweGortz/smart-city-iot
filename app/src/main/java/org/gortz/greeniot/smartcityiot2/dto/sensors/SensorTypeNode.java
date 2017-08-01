package org.gortz.greeniot.smartcityiot2.dto.sensors;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.Calendar;
import java.util.List;

import org.gortz.greeniot.smartcityiot2.model.queue.SensorValueQueue;
import org.gortz.greeniot.smartcityiot2.database.entity.Location;

/**
 * Container for a sensor node with its sensor values of a specific type.
 */
public class SensorTypeNode  implements ClusterItem {

    private SensorValueQueue valueList;
    private String name;
    private String organization;
    private Location location;

    /**
     * SensorTypeNode
     * @param name Name of sensor node.
     * @param organization Name of the organization the sensor belongs to.
     * @param location Location of sensor node.
     */
    public SensorTypeNode(String name, String organization, Location location) {
        this.valueList = new SensorValueQueue(10);
        this.name = name;
        this.organization = organization;
        if(location != null) {
            this.location = location;
        }else{
            this.location = new Location(name);
        }
    }

    public WeightedLatLng getWeightedLatLng(){
        return new WeightedLatLng(location.getPosition().getLatLng(),getLatestValue().getValue());
    }

    /**
     * Get list of all values for sensor node of specific type.
     * @return List with SensorValues.
     */
    public List<SensorValue> getValueList() {
        return valueList.getValues();
    }

    /**
     * Get name of sensor.
     * @return Sensor name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get name of organization.
     * @return organization name.
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * Get location of sensor node.
     * @return Location object of sensor node.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Add new value to sensor node of specific type with lastRetrievalTimestamp.
     * @param timestamp Set lastRetrievalTimestamp of when sensor reading was collected.
     * @param value Set sensor value.
     */
    public void addValue(Calendar timestamp, Double value){
        valueList.enqueue(new SensorValue(timestamp, value));
    }

    /**
     * Get LatLng object with position.
     * @return LatLng object containing position of sensor node.
     */
    @Override
    public LatLng getPosition() {
        return getLocation().getPosition().getLatLng();
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public String getSnippet() {
        return "";
    }

    /**
     * Get the most recent sensor value.
     * @return Newest SensorValue.
     */
    public SensorValue getLatestValue(){
        return valueList.peekEnd();
    }

    /**
     * Get the oldest sensor value.
     * @return Oldest SensorValue.
     */
    public SensorValue getEarliestValue(){
        return valueList.peekStart();
    }

    /**
     * Add new value to sensor node of specific type.
     *
     * @param sensorValue Set sensor value.
     */
    public void addValue(SensorValue sensorValue) {
        valueList.enqueue(sensorValue);
    }
}
