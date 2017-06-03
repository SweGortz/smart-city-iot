package org.gortz.greeniot.smartcityiot.dto.sensors;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gortz.greeniot.smartcityiot.database.entity.SensorType;
import org.gortz.greeniot.smartcityiot.dto.listitems.SpinnerItemEntry;

/**
 * Container for list of sensor types from DB, list to display them in a spinner and a HashMap between DB and spinner item id.
 */

@Getter
@NoArgsConstructor
public class SensorTypeDbDTO {
    /**
     * Sensor type names in a list for spinners.
     *
     * @return ArrayList with sensor type names.
     */
    ArrayList<SpinnerItemEntry<Integer, String>> sensorTypeNames = new ArrayList<>();

    /**
     * Sensor type map which maps a spinner item entry ID to a database ID.
     *
     * @return HashMap with Spinner item ID and database ID.
     */
    HashMap<Integer, Integer> sensorTypeMap = new HashMap<>();

    /**
     * Sensor types from DB.
     *
     * @return HashMap with sensor types and id.
     */
    HashMap<Integer,SensorType> sensorTypes = new HashMap<>();
}
