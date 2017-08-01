package org.gortz.greeniot.smartcityiot2.dto.sensors;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.gortz.greeniot.smartcityiot2.model.SensorDataHandler;

/**
 * Container for request of new sensor values.
 */

@Getter
@Setter
@AllArgsConstructor(suppressConstructorProperties = true)
public class UpdateSensorDTO {
    /**
     * SensorDataHandler contains all SensorTypeNodes.
     *
     * @param sensorDataHandler Set new SensorDataHandler.
     * @return SensorDataHandler.
     */
    SensorDataHandler sensorDataHandler;

    /**
     * Sensor type name of current request.
     *
     * @param currentSensorTypeName Set name of current sensor type.
     * @return Name of current sensor type.
     */
    String currentSensorTypeName;

    /**
     * Timestamp of when last retrieval was made.
     *
     * @param lastRetrievalTimestamp Set timestamp for when last retrieval was made.
     * @param Get timestamp of when last retrieval was made.
     */
    Calendar lastRetrievalTimestamp;
}
