package org.gortz.greeniot.greencityiot.dto.sensors;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Unique identifier of sensor node
 */

@Getter
@AllArgsConstructor(suppressConstructorProperties = true)
public class SensorNodeKey {
    /**
     * Sensor name
     * @return Name of sensor
     */
    String sensorName;

    /**
     * Organization name
     * @return Name of organization
     */
    String organizationName;

    /**
     * Equals
     * @param o SensorNodeKey to compare
     * @return True if both name and organization matches, otherwise false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SensorNodeKey that = (SensorNodeKey) o;

        if (!sensorName.equals(that.sensorName)) return false;
        return organizationName.equals(that.organizationName);

    }

    @Override
    public int hashCode() {
        int result = sensorName.hashCode();
        result = 31 * result + organizationName.hashCode();
        return result;
    }
}
