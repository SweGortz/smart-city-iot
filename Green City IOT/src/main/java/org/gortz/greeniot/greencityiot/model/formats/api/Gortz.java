package org.gortz.greeniot.greencityiot.model.formats.api;

import org.springframework.http.HttpMethod;

import java.util.Calendar;

import org.gortz.greeniot.greencityiot.dto.connection.RestApiCall;

/**
 * Example of api call structure
 */
public class Gortz implements BasicRestApi {

    /**
     * Call to get all sensor type topics
     * @return endpoint to call
     */
    @Override
    public RestApiCall getAllSensorTypesTopic() {
        return new RestApiCall("/sensortypes", HttpMethod.GET);
    }

    /**
     * Get sensor data from sensor with sensor type and sensor id
     * @param sensorID of sensor
     * @param sensorType to retrieve
     * @param amountOfData to request
     * @param lastCall that was made
     * @return endpoint to call
     */
    @Override
    public RestApiCall getDataFromSensorOfDataTypeTopic(String sensorID, String sensorType, int amountOfData, Calendar lastCall) {
        return new RestApiCall("/sensors/"+sensorID+"/sensortypes/"+sensorType+"/size/"+amountOfData,HttpMethod.GET);
    }

    /**
     * Get the latest sensor values of a sensor type
     * @param sensorType name
     * @param currentTime of call
     * @return endpoint to call
     */
    @Override
    public RestApiCall getLatestSensorValuesOfSensorTypeTopic(String sensorType, Calendar currentTime) {
        return new RestApiCall("/sensors/sensortypes/"+sensorType+"/size/1", HttpMethod.GET);
    }

}
