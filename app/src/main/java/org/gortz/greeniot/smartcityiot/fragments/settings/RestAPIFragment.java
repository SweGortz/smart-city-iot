package org.gortz.greeniot.smartcityiot.fragments.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.gortz.greeniot.smartcityiot.R;
import org.gortz.greeniot.smartcityiot.activity.SettingsActivity;
import org.gortz.greeniot.smartcityiot.database.entity.DataStructure;
import org.gortz.greeniot.smartcityiot.database.entity.TopicStructure;
import org.gortz.greeniot.smartcityiot.database.entity.Connection;
import org.gortz.greeniot.smartcityiot.dto.listitems.SpinnerItemEntry;

/**
 * Display and update a RestAPI connection.
 */
public class RestAPIFragment extends Fragment {
    private EditText urlEditText;
    private EditText portEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private SettingsActivity activity;
    private boolean update = false;
    private Connection connectionItem;
    private View v;
    private Spinner topicStructureSpinner;
    private Spinner dataStructureSpinner;
    private final String CONNECTION_TYPE = "restapi";
    private final String CONNECTION_GROUP = "api";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.activity = (SettingsActivity) getActivity();
        ((SettingsActivity) getActivity()).hideDrawer();
        v = inflater.inflate(R.layout.rest_api_view, container, false);
        Button RestAPIActionButton = (Button) v.findViewById(R.id.add_new_rest_api_action_button);
        urlEditText = (EditText) v.findViewById(R.id.urlEditText);
        portEditText = (EditText) v.findViewById(R.id.portEditText);
        usernameEditText = (EditText) v.findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) v.findViewById(R.id.passwordEditText);

        topicStructureSpinner = (Spinner) v.findViewById(R.id.topic_structure_spinner);
        ArrayAdapter<String> topicAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, activity.getAllTopicStructuresByType(CONNECTION_GROUP));
        topicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topicStructureSpinner.setAdapter(topicAdapter);

        dataStructureSpinner = (Spinner) v.findViewById(R.id.data_structure_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, activity.getAllDataStructuresByType(CONNECTION_GROUP));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataStructureSpinner.setAdapter(dataAdapter);

        Bundle args = getArguments();
        if(args != null){
            int connectionID = args.getInt("id");
            connectionItem = activity.getConnectionByTypeAndID(CONNECTION_TYPE, connectionID);
            urlEditText.setText(connectionItem.getUrl());
            portEditText.setText(String.valueOf(connectionItem.getPort()));
            usernameEditText.setText(connectionItem.getUsername());
            passwordEditText.setText(connectionItem.getPassword());
            topicStructureSpinner.setSelection(activity.getTopicStructureListID(connectionItem.getTopicStructure().getId()));
            dataStructureSpinner.setSelection(activity.getDataStructureListID(connectionItem.getDataStructure().getId()));

            update = true;
            RestAPIActionButton.setText("Update");
        }

        RestAPIActionButton.setOnClickListener(new RestAPIActionButtonListener());
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((SettingsActivity)getActivity()).showDrawer();
    }

    private class RestAPIActionButtonListener implements  View.OnClickListener {

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

            if(!error){
                if(!update){
                    try{
                        Connection restAPIConnection = new Connection(CONNECTION_TYPE, urlEditText.getText().toString(), Integer.valueOf(portEditText.getText().toString()),"",usernameEditText.getText().toString(),passwordEditText.getText().toString(),true, new TopicStructure(((SpinnerItemEntry<Integer, String>)topicStructureSpinner.getSelectedItem()).getKey()), new DataStructure(((SpinnerItemEntry<Integer, String>)dataStructureSpinner.getSelectedItem()).getKey()));
                        activity.addConnection(restAPIConnection);
                    }
                    catch(Exception e){
                        System.out.println("Could not get all values.");
                    }

               }else {
                    connectionItem.setUrl(urlEditText.getText().toString());
                    connectionItem.setPort(Integer.valueOf(portEditText.getText().toString()));
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
