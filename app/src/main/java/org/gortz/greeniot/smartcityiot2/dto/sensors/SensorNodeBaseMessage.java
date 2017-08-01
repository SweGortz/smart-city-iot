package org.gortz.greeniot.smartcityiot2.dto.sensors;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import org.gortz.greeniot.smartcityiot2.database.entity.Location;

/**
 * Container for sensor messages
 */
@Setter
@Getter
public class SensorNodeBaseMessage {
    /**
     * ID of message
     *
     * @param id set ID
     * @return id
     */
    private String id;

    /**
     * Location of message source
     * @param location Set location of message source
     * @return Location of message source
     */
    private Location location;

    /**
     * Organization of sender
     * @param organization Set organization
     * @return Organization
     */
    private String organization;

    /**
     * Data sensor values
     * @param data Set HashMap containing sensor data
     * @return Data
     */
    private HashMap<String, ArrayList<SensorValue>> data;
}
