package org.gortz.greeniot.smartcityiot2.fragments.settings;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


import org.gortz.greeniot.smartcityiot2.R;
import org.gortz.greeniot.smartcityiot2.activity.SettingsActivity;
import org.gortz.greeniot.smartcityiot2.database.entity.DataStructure;
import org.gortz.greeniot.smartcityiot2.database.entity.TopicStructure;
import org.gortz.greeniot.smartcityiot2.dto.listitems.SpinnerItemEntry;
import org.gortz.greeniot.smartcityiot2.database.entity.Connection;

/**
 * Display and update an MQTT connection.
 */
public class MQTTFragment extends Fragment {
    private EditText urlEditText;
    private EditText portEditText;
    private EditText topicEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private SettingsActivity activity;
    private boolean update = false;
    private Connection connectionItem;
    private View v;
    private Spinner topicStructureSpinner;
    private Spinner dataStructureSpinner;
    private final String CONNECTION_TYPE = "mqtt";
    private final String CONNECTION_GROUP = "broker";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.activity = (SettingsActivity) getActivity();
        ((SettingsActivity) getActivity()).hideDrawer();
        v = inflater.inflate(R.layout.mqtt_view, container, false);
        Button MQTTActionButton = (Button) v.findViewById(R.id.add_new_mqtt_action_button);
        urlEditText = (EditText) v.findViewById(R.id.urlEditText);
        portEditText = (EditText) v.findViewById(R.id.portEditText);
        topicEditText = (EditText) v.findViewById(R.id.topicEditText);
        usernameEditText = (EditText) v.findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) v.findViewById(R.id.passwordEditText);

        topicStructureSpinner = (Spinner) v.findViewById(R.id.topic_structure_spinner);
        ArrayAdapter<String> topicAdapter = new ArrayAdapter<String>(activity.getApplicationContext(), R.layout.spinner_item, activity.getAllTopicStructuresByType(CONNECTION_GROUP));
        topicAdapter.setDropDownViewResource(R.layout.spinner_item);
        topicStructureSpinner.setAdapter(topicAdapter);

        dataStructureSpinner = (Spinner) v.findViewById(R.id.data_structure_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity.getApplicationContext(), R.layout.spinner_item, activity.getAllDataStructuresByType(CONNECTION_GROUP));
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        dataStructureSpinner.setAdapter(dataAdapter);

        Bundle args = getArguments();

        if(args != null){
            int connectionID = args.getInt("id");
            connectionItem = activity.getConnectionByTypeAndID(CONNECTION_TYPE, connectionID);
            urlEditText.setText(connectionItem.getUrl());
            portEditText.setText(String.valueOf(connectionItem.getPort()));
            topicEditText.setText(connectionItem.getArg0());
            usernameEditText.setText(connectionItem.getUsername());
            passwordEditText.setText(connectionItem.getPassword());
            topicStructureSpinner.setSelection(activity.getTopicStructureListID(connectionItem.getTopicStructure().getId()));
            dataStructureSpinner.setSelection(activity.getDataStructureListID(connectionItem.getDataStructure().getId()));

            update = true;
            MQTTActionButton.setText("Update");
        }

        MQTTActionButton.setOnClickListener(new MqttActionButtonListener());
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((SettingsActivity)getActivity()).showDrawer();
    }

    private class MqttActionButtonListener implements  View.OnClickListener {

        @Override
        public void onClick(View view) {
            Boolean error = false;
            if(urlEditText.getText().toString().matches("")){
                error = true;
                urlEditText.setError("You must enter a URL!");
            }
            if(portEditText.getText().toString().matches("")){
                error = true;
                portEditText.setError("You must enter a Port number!");
            }
            if(topicEditText.getText().toString().matches("")){
                error = true;
                topicEditText.setError("You must enter a Topic!");
            }
            if(!error){
                if(!update){
                    Connection mqttConnection = new Connection(CONNECTION_TYPE, urlEditText.getText().toString(), Integer.valueOf(portEditText.getText().toString()),topicEditText.getText().toString(),usernameEditText.getText().toString(),passwordEditText.getText().toString(),true, new TopicStructure(((SpinnerItemEntry<Integer, String>)topicStructureSpinner.getSelectedItem()).getKey()), new DataStructure(((SpinnerItemEntry<Integer, String>)dataStructureSpinner.getSelectedItem()).getKey()));
                    activity.addConnection(mqttConnection);
               }else {
                    connectionItem.setUrl(urlEditText.getText().toString());
                    connectionItem.setPort(Integer.valueOf(portEditText.getText().toString()));
                    connectionItem.setArg0(topicEditText.getText().toString());
                    connectionItem.setUsername(usernameEditText.getText().toString());
                    connectionItem.setPassword(passwordEditText.getText().toString());
                    connectionItem.setDataStructure(new DataStructure(((SpinnerItemEntry<Integer, String>)dataStructureSpinner.getSelectedItem()).getKey()));
                    connectionItem.setTopicStructure(new TopicStructure(((SpinnerItemEntry<Integer, String>)topicStructureSpinner.getSelectedItem()).getKey()));
                    activity.updateConnection(connectionItem);
                }
              activity.onBackPressed();
            }
        }
    }

}
