package org.gortz.greeniot.greencityiot.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import org.gortz.greeniot.greencityiot.database.SensorDataDAO;
import org.gortz.greeniot.greencityiot.database.entity.SensorType;
import org.gortz.greeniot.greencityiot.database.entity.Connection;
import org.gortz.greeniot.greencityiot.model.formats.api.BasicRestApi;
import org.gortz.greeniot.greencityiot.model.formats.datatype.DataStructureConverter;

/**
 * Background service that takes care of broker connections and cleanup
 */
public class SubscriberService extends IntentService {
    private static final String TAG = SubscriberService.class.getSimpleName();
    private static final int SLEEP_BETWEEN_DB_CLEANUP = 120;
    private ArrayList<MqttDaemon> mqttDaemons = new ArrayList<>();
    private SensorDataDAO sensorDataDAO;

    private static final String ACTION_START_LISTEN_DAEMON = "se.kth.greeniot.greencityiot.service.action.start.listen.daemon";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"on create of pull service");

    }
    public SubscriberService() {
        super("SubscriberService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if(ACTION_START_LISTEN_DAEMON.equals(action)){
                sensorDataDAO = SensorDataDAO.getInstance(this);
                dataCleanUpThread().start();
                getApiSensorTypes();
                startDaemon();
            }
        }
    }

    /**
     * retrieves all sensor types from api connections
     */
    private void getApiSensorTypes() {
        Log.i(TAG,"Retrieving all sensor types from api");
        for(Connection api : sensorDataDAO.getAllActiveConnectionOfType("restapi")){
            Log.i(TAG,"Requesting sensorTypes from "+ api.getUrl());
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

                BasicRestApi restApi = (BasicRestApi) Class.forName("se.kth.greeniot.greencityiot.model.formats.api."+api.getTopicStructure().getName()).newInstance();

                String restRequest = api.getUrl() +":" +api.getPort()+ restApi.getAllSensorTypesTopic().getTopic();
                String result = restTemplate.getForObject("http://"+restRequest, String.class);

                DataStructureConverter dataStructureConverter = new DataStructureConverter(this);

                ArrayList<SensorType> sensorTypes = dataStructureConverter.convertSensorTypesResponse(result,api.getTopicStructure().getName());

                for(SensorType sensorType : sensorTypes){
                    Log.i(TAG,"Api connection "+api.getUrl()+" supports "+sensorType.getName());
                    sensorDataDAO.createSensorTypeAndAlias(sensorType);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    AlarmManager alarmManager;

    /**
     * Starts the active broker connections
     */
    private void startDaemon(){
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        for(Connection connection : sensorDataDAO.getActiveConnections()){
            try {
                switch (connection.getConnectionType()) {
                    case "mqtt":
                        MqttDaemon mqttDaemonConnection = new MqttDaemon();
                        mqttDaemonConnection.startMqtt(this, connection.getUrl(), connection.getPort(), connection.getUsername(), connection.getPassword(), connection.getArg0(), connection.getTopicStructure().getRegex(), connection.getTopicStructure().getLocationRegexID(), connection.getTopicStructure().getOrganizationRegexID(), connection.getTopicStructure().getNodeNameRegexID(), connection.getDataStructure().getName());
                        mqttDaemons.add(mqttDaemonConnection);
                        break;
                    default:
                        Log.e(TAG, "Not a supported connection type " + connection.getConnectionType());
                }
            } catch (Exception e) {
                Log.e(TAG, "Something happen with a broker service");
                e.printStackTrace();
            }
        }
        sleep();
    }

    /**
     * Makes the backgrounds service wait and not return (to avoid lost of references to the MQTT threads
     */
    private void sleep(){
        while (true){
            try {
                Thread.sleep(100000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Starts the data clean up thread
     * @return thread of data cleanup
     */
    private Thread dataCleanUpThread(){
       return new Thread(new Runnable() {
            public void run() {
                dataCleanUpService();
            }
        });
    }

    /**
     * Starts the data clean up service
     */
    private void dataCleanUpService(){
        while (true) {
            sensorDataDAO.sensorDataCleanUp();
            try {
                Thread.sleep(SLEEP_BETWEEN_DB_CLEANUP * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
