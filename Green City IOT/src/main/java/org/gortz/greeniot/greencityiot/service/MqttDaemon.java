package org.gortz.greeniot.greencityiot.service;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gortz.greeniot.greencityiot.model.formats.datatype.DataStructureConverter;

/**
 * MQTT connection object
 */
public class MqttDaemon {
    private static final String TAG = MqttDaemon.class.getSimpleName();
    private Pattern pattern;
    private int connectionTries=0;
    private final int maxConnectionTries=3;
    private DataStructureConverter dataStructureConverter;

    /**
     * Starts a connection to a MQTT server
     * @param context of the application
     * @param broker url to connect to
     * @param port of connection
     * @param username of connection
     * @param password of connection
     * @param topic to listen on
     * @param topicReqEx to retrieve data from topic
     * @param locationRegExId to find the location group in the regex
     * @param organizationRegExId to find the organization group int the regex
     * @param sensorNodeIdRegExId to find the sensor node id in the regex
     * @param dataType of received message
     */
    public void startMqtt(final Context context, final String broker,final int port,final String username,final String password, final String topic, final String topicReqEx, final int locationRegExId, final int organizationRegExId, final int sensorNodeIdRegExId, final String dataType) {
        connectionTries++;

        dataStructureConverter = new DataStructureConverter(context);
        Log.i(TAG, "MQTT Start "+broker);
        final MqttAndroidClient client = new MqttAndroidClient(context, "tcp://"+broker+":"+port, "mySmartCity-" + generateRandomId());

        pattern = Pattern.compile(topicReqEx);

        MqttConnectOptions options = new MqttConnectOptions();
        // Set authentication if it exist
        if(username != null){
            options.setUserName(username);
            options.setPassword(password.toCharArray());
        }

        try{
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "Connection to " + broker + " was Successful");
                    client.setCallback(new MqttCallback() {

                        @Override
                        public void connectionLost(Throwable cause) {
                            cause.printStackTrace();
                            Log.d(TAG, "Connection to " + broker + "  lost, will try again in 10sec");
                            if(connectionTries <= maxConnectionTries) {
                                connectionTries++;
                                startMqtt(context, broker, port, username, password, topic, topicReqEx, locationRegExId, organizationRegExId, sensorNodeIdRegExId, dataType);
                                Toast.makeText(context, "Connection to " + broker + " lost, will try again", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            String location;
                            String organization;
                            String nodeName;
                            try {
                                Matcher m = pattern.matcher(topic);
                                m.find();
                                location = m.group(locationRegExId);
                                organization = m.group(organizationRegExId);
                                nodeName = m.group(sensorNodeIdRegExId);

                            }catch (Exception e){
                                Log.e(TAG,"could not parse ["+topic+"]");
                                return;
                            }
                            dataStructureConverter.convertDataToDb(message.toString(),dataType, location,organization,nodeName);
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });

                    try {
                        client.subscribe(topic, 0);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Connection to "+ broker + " failed ["+exception.getMessage()+"]");
                    Toast toast = Toast.makeText(context, "Connection to "+ broker + " failed ["+exception.getMessage()+"]", Toast.LENGTH_LONG);
                    toast.show();
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate random id to connection
     * @return  a random connection id
     */
    private int generateRandomId() {
        return ThreadLocalRandom.current().nextInt(100, 10000 + 1);
    }
}
