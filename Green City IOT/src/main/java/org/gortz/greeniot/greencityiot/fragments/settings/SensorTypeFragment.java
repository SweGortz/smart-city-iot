package org.gortz.greeniot.greencityiot.fragments.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.gortz.greeniot.greencityiot.R;
import org.gortz.greeniot.greencityiot.activity.SettingsActivity;
import org.gortz.greeniot.greencityiot.database.entity.SensorLimits;
import org.gortz.greeniot.greencityiot.database.entity.SensorType;

/**
 * Display and update a sensor type.
 */
public class SensorTypeFragment extends Fragment {
    private EditText typeNameEditText;
    private EditText unitEditText;
    private EditText lowEditText;
    private EditText medEditText;
    private EditText highEditText;
    private SettingsActivity activity;
    private SensorType sensorTypeItem;
    private View v;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.activity = (SettingsActivity) getActivity();
        ((SettingsActivity) getActivity()).hideDrawer();
        v = inflater.inflate(R.layout.sensor_type_view, container, false);
        Button updateButton = (Button) v.findViewById(R.id.update_button);
        typeNameEditText = (EditText) v.findViewById(R.id.type_name);
        unitEditText = (EditText) v.findViewById(R.id.unit);
        lowEditText = (EditText) v.findViewById(R.id.low);
        medEditText = (EditText) v.findViewById(R.id.med);
        highEditText = (EditText) v.findViewById(R.id.high);

        Bundle args = getArguments();

        if(args != null){
            int sensorTypeID = args.getInt("id");
            sensorTypeItem = activity.getSensorTypeByID(sensorTypeID);
            typeNameEditText.setText(sensorTypeItem.getName());
            unitEditText.setText(String.valueOf(sensorTypeItem.getUnit()));
            lowEditText.setText(String.valueOf(sensorTypeItem.getLimits().getLow()));
            medEditText.setText(String.valueOf(sensorTypeItem.getLimits().getMedium()));
            highEditText.setText(String.valueOf(sensorTypeItem.getLimits().getHigh()));

            updateButton.setText("Update");
        }

        updateButton.setOnClickListener(new UpdateButtonListener());
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((SettingsActivity)getActivity()).showDrawer();
    }

    private class UpdateButtonListener implements  View.OnClickListener {

        @Override
        public void onClick(View view) {
            Boolean error = false;

            if(typeNameEditText.getText().toString().matches("")){
                error = true;
                typeNameEditText.setError("You must enter a name!");
            }
            if(lowEditText.getText().toString().matches("")){
                error = true;
                lowEditText.setError("You must enter a low limit number!");
            }
            if(medEditText.getText().toString().matches("")){
                error = true;
                medEditText.setError("You must enter a medium limit number!");
            }
            if(highEditText.getText().toString().matches("")){
                error = true;
                highEditText.setError("You must enter a high limit number!");
            }

            if(!error){
                //Automatically set sensor type as supported when it is updated.
                SensorType sensorType = new SensorType(sensorTypeItem.getId(),typeNameEditText.getText().toString(),unitEditText.getText().toString(), sensorTypeItem.isActive(), true, new SensorLimits(Double.valueOf(lowEditText.getText().toString()), Double.valueOf(medEditText.getText().toString()), Double.valueOf(highEditText.getText().toString())));
                activity.updateSensorType(sensorType);
                activity.onBackPressed();
            }
        }
    }
}
