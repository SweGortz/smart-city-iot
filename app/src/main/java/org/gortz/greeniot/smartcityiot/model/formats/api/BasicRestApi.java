package org.gortz.greeniot.smartcityiot.model.formats.api;

import java.util.Calendar;

import org.gortz.greeniot.smartcityiot.dto.connection.RestApiCall;

public interface BasicRestApi {
    RestApiCall getAllSensorTypesTopic();
    RestApiCall getDataFromSensorOfDataTypeTopic(String sensorID, String sensorType, int amountOfData, Calendar lastCall);
    RestApiCall getLatestSensorValuesOfSensorTypeTopic(String sensorTypeName,Calendar currentTime);
}
