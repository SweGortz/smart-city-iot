package org.gortz.greeniot.smartcityiot2.fragments.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.gortz.greeniot.smartcityiot2.R;
import org.gortz.greeniot.smartcityiot2.activity.SettingsActivity;
import org.gortz.greeniot.smartcityiot2.dto.listitems.SpinnerItemEntry;

/**
 * Handler for type alias settings
 */
public class TypeAliasFragment extends Fragment {
    private TextView aliasNameTextView;
    private SettingsActivity activity;
    private View v;
    private Spinner aliasSpinner;
    private int spinnerItemSelected;
    private int sensorTypeID;
    private int typeAliasID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.activity = (SettingsActivity) getActivity();
        ((SettingsActivity) getActivity()).hideDrawer();
        v = inflater.inflate(R.layout.sensor_alias_view, container, false);
        Button updateButton = (Button) v.findViewById(R.id.update_button);
        Button releaseAliasButton = (Button) v.findViewById(R.id.release_alias_button);
        Button deleteAliasButton = (Button) v.findViewById(R.id.delete_alias_button);
        aliasNameTextView = (TextView) v.findViewById(R.id.alias_name);

        aliasSpinner = (Spinner) v.findViewById(R.id.alias_spinner);
        ArrayAdapter<String> aliasAdapter = new ArrayAdapter<String>(activity.getApplicationContext(), R.layout.spinner_item, activity.getAllSensorTypeNames());
        aliasAdapter.setDropDownViewResource(R.layout.spinner_item);
        aliasSpinner.setAdapter(aliasAdapter);

        Bundle args = getArguments();

        if(args != null){
            typeAliasID = args.getInt("id");
            sensorTypeID = args.getInt("typeID");
            aliasNameTextView.setText(args.getString("name"));
            int intColor = aliasNameTextView.getCurrentTextColor();
            System.out.println("color id is: " + intColor);
            //System.out.println("color is: " + activity.getColor(color));
            //int intColor = -16776961;
            String hexColor = "#" + Integer.toHexString(intColor).substring(2);
            System.out.println("color is: " + hexColor);

            aliasSpinner.setSelection(activity.getSensorTypeNameListID(sensorTypeID));
            aliasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spinnerItemSelected = ((SpinnerItemEntry<Integer, String>)parent.getItemAtPosition(position)).getKey();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            //Hur vet man om den har ett alias eller inte? När sidan laddas ska den ju sitta på No Alias om den inte är kopplad och Sensor Type Name om den är kopplad som alias till någon annan typ.
            /*
            * Vad händer när en ny sensorTyp kommer in via ett meddelande?
            * Alt. Det hamnar som en ny sensor typ med t.ex. displaynamn "temp" och så skapas ett alias i listan med samma namn d.v.s. "temp" och ett typeID som pekar på id för sensor typen.
            *
            * Vad händer isåfall om man kopplar temp som alias till temperatur?
            * Kommer Sensor typen "temp" finnas kvar?
            * Kommer Alias temp i alias listan byta nodeID till det för temperature istället?
            * */

            updateButton.setText("Update");
            releaseAliasButton.setText("Release");
            deleteAliasButton.setText("Delete Alias");

        }

        updateButton.setOnClickListener(new UpdateButtonListener());
        releaseAliasButton.setOnClickListener(new ReleaseAliasButtonListener());
        deleteAliasButton.setOnClickListener(new DeleteAliasButtonListener());
        return v;
    }

    /**
     * Called when the view is being destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((SettingsActivity)getActivity()).showDrawer();
    }

    /**
     * Update button click handler
     */
    private class UpdateButtonListener implements  View.OnClickListener {

        @Override
        public void onClick(View view) {
            if(spinnerItemSelected != sensorTypeID){
                activity.updateTypeAlias(typeAliasID, spinnerItemSelected);
            }
            activity.onBackPressed();
        }
    }

     /**
     * Release alias button click handler
     */
    private class ReleaseAliasButtonListener implements  View.OnClickListener {

        @Override
        public void onClick(View view) {
            activity.releaseAsAliasByID(typeAliasID);
            activity.onBackPressed();
        }
    }

    /**
     * Delete alias button click handler
     */
    private class DeleteAliasButtonListener implements  View.OnClickListener {

        @Override
        public void onClick(View view) {
            activity.deleteTypeAlias(typeAliasID);
            activity.onBackPressed();
        }
    }

}
