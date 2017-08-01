package org.gortz.greeniot.smartcityiot2.fragments.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.gortz.greeniot.smartcityiot2.R;
import org.gortz.greeniot.smartcityiot2.activity.SettingsActivity;
import org.gortz.greeniot.smartcityiot2.database.entity.Coordinate;
import org.gortz.greeniot.smartcityiot2.database.entity.Location;

/**
 * Display and update GPS coordinates for a Location.
 */
public class LocationFragment extends Fragment {
    private EditText lonEditText;
    private EditText latEditText;
    private TextView locationName;
    private View v;
    private SettingsActivity activity;
    Location locationItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.activity = (SettingsActivity) getActivity();
        activity.hideDrawer();
        v = inflater.inflate(R.layout.location_item_view, container, false);
        Button updateButton = (Button) v.findViewById(R.id.update_button);
        lonEditText = (EditText) v.findViewById(R.id.lon_EditText);
        latEditText = (EditText) v.findViewById(R.id.lat_EditText);
        locationName = (TextView) v.findViewById(R.id.location_name);
        Bundle args = getArguments();

        if(args != null) {
            int locationID = args.getInt("id");
            locationItem = activity.getLocationByID(locationID);
            locationName.setText(locationItem.getName());

            if(locationItem.getPosition() != null){
                latEditText.setText(String.valueOf(locationItem.getPosition().getLat()));
                lonEditText.setText(String.valueOf(locationItem.getPosition().getLon()));
            }
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
            if(lonEditText.getText().toString().matches("")){
                error = true;
                lonEditText.setError("You must enter a value for Longitude!");
            }
            if(latEditText.getText().toString().matches("")){
                error = true;
                latEditText.setError("You must enter a value for Latitude");
            }
            if(!error){
                Location location = new Location(locationItem.getId(), locationName.getText().toString(), new Coordinate(Double.valueOf(latEditText.getText().toString()), Double.valueOf(lonEditText.getText().toString())));
                activity.updateLocation(location);
                activity.onBackPressed();
            }
        }
    }

}
