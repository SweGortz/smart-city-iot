package org.gortz.greeniot.smartcityiot.model.formats.datatype;
/**
 * Converter for different types of sensor formats
 */

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import lombok.NoArgsConstructor;
import org.gortz.greeniot.smartcityiot.database.SensorDataDAO;
import org.gortz.greeniot.smartcityiot.database.entity.Coordinate;
import org.gortz.greeniot.smartcityiot.database.entity.Location;
import org.gortz.greeniot.smartcityiot.database.entity.SensorType;
import org.gortz.greeniot.smartcityiot.dto.sensors.SensorNodeBaseMessage;
import org.gortz.greeniot.smartcityiot.dto.sensors.SensorValue;

@NoArgsConstructor
public  class DataStructureConverter {
    private static final String TAG = DataStructureConverter.class.getSimpleName();
    private SensorDataDAO sensorDataDAO;

    public DataStructureConverter(Context context) {
        sensorDataDAO = SensorDataDAO.getInstance(context);
    }

    /**
     * Convert messages and save result to database
     * @param message that was received
     * @param dataType of message
     * @param location the message was from
     * @param organization sending the message
     * @param nodeId connected to the message
     */
    public void convertDataToDb(String message, String dataType, String location, String organization, String nodeId) {
        SensorNodeBaseMessage data =  convertData( message,  dataType,  location,  organization,  nodeId,null);
        long nodeID = sensorDataDAO.getNodeID(data);

        for(String sensorTypes :data.getData().keySet()){
            for(SensorValue sv : data.getData().get(sensorTypes)){
                sensorDataDAO.createSensorValueMessage(nodeID, sensorTypes, sv);
            }
        }
    }

    /**
     * Convert message to sensor node base message
     * @param message that was received
     * @param dataType of message
     * @param location the message was from
     * @param organization sending the message
     * @param nodeId connected to the message
     * @return sensorNodeBaseMessage version of message
     */
    public SensorNodeBaseMessage  convertData(String message, String dataType, String location, String organization, String nodeId, String sensorType){
        SensorNodeBaseMessage sensorNodeBaseMessage = new SensorNodeBaseMessage();
        sensorNodeBaseMessage.setLocation(new Location(location));
        sensorNodeBaseMessage.setOrganization(organization);
        sensorNodeBaseMessage.setId(nodeId);
        HashMap<String, ArrayList<SensorValue>> sensorData = new HashMap<>();
        try {
        switch (dataType){

            case "Gortz":
                JSONArray jsonArray = new JSONArray(message);
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject j = jsonArray.getJSONObject(i);
                    sensorNodeBaseMessage.setId(j.getString("nodeId"));

                    Calendar timestamp=null;
                    try {
                        timestamp = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                        timestamp.setTimeInMillis(Long.valueOf(j.getString("lastRetrievalTimestamp")));
                    } catch (Exception e) {
                        Log.i(TAG,"Could'nt convert messages lastRetrievalTimestamp to date");
                    }
                    if(!sensorData.containsKey(sensorType))sensorData.put(sensorType, new ArrayList<SensorValue>());
                    sensorData.get(sensorType).add(new SensorValue(j.getDouble("value")));
                }
                break;

            case "senML":

                    JSONArray arr = new JSONArray(message);
                    JSONObject node = arr.getJSONObject(0);
                    sensorNodeBaseMessage.setId(node.getString("bn"));
                    Calendar timestamp = null;
                    if(node.has("bt")) {
                        timestamp = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                        timestamp.setTimeInMillis(node.getLong("bt"));
                    }
                    if(node.has("long") && node.has("lat")){
                        if(sensorNodeBaseMessage.getLocation() != null)
                        sensorNodeBaseMessage.getLocation().setPosition(new Coordinate(node.getDouble("lat"),node.getDouble("long")));
                    }

                    for (int i = 1; i < arr.length(); i++) {
                        JSONObject sensor = arr.getJSONObject(i);
                        String SenMLsensorType = (String) sensor.get("n");
                        SenMLsensorType = SenMLsensorType.substring(SenMLsensorType.lastIndexOf(";") + 1, SenMLsensorType.length());
                        double sensorValue = Double.valueOf(String.valueOf(sensor.get("v")));

                        if(!sensorData.containsKey(SenMLsensorType)) sensorData.put(SenMLsensorType,new ArrayList<SensorValue>());

                        if(timestamp != null){
                            sensorData.get(SenMLsensorType).add(new SensorValue(timestamp,sensorValue));
                        }else{
                            sensorData.get(SenMLsensorType).add(new SensorValue(sensorValue));
                        }
                    }
                break;
            default:
                Log.e(TAG, "Not a supported data type " + dataType);
                break;
        }
        }catch (Exception e){
        Log.e(TAG,"Not a supported message [" + message + "] for " + dataType);
            e.printStackTrace();
    }
        sensorNodeBaseMessage.setData(sensorData);
        return sensorNodeBaseMessage;
    }

    /**
     * Convert response from sensor api call
     * @param responseMessage of api call
     * @param topicStructure to use
     * @return List of sensor types
     */
    public ArrayList<SensorType> convertSensorTypesResponse(String responseMessage, String topicStructure) {
        ArrayList<SensorType> sensorTypes = new ArrayList<>();
         try {

            switch (topicStructure){
                case "Gortz":
                    JSONArray arr = new JSONArray(responseMessage);
                    for(int i = 0; i< arr.length(); i++){
                        JSONObject t = arr.getJSONObject(i);
                        sensorTypes.add(new SensorType(t.getString("name"),t.getString("unit")));
                    }
                    break;

                default:
                    Log.i(TAG,"No support for the ["+topicStructure+"] topicStructure");
            }
         } catch (Exception e) {
             e.printStackTrace();
         }

        return sensorTypes;
    }
}
