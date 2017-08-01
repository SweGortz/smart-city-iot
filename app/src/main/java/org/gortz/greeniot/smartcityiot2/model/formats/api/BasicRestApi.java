package org.gortz.greeniot.smartcityiot2.model.formats.api;

import java.util.Calendar;

import org.gortz.greeniot.smartcityiot2.dto.connection.RestApiCall;

public interface BasicRestApi {
    RestApiCall getAllSensorTypesTopic();
    RestApiCall getDataFromSensorOfDataTypeTopic(String sensorID, String sensorType, int amountOfData, Calendar lastCall);
    RestApiCall getLatestSensorValuesOfSensorTypeTopic(String sensorTypeName,Calendar currentTime);
}
