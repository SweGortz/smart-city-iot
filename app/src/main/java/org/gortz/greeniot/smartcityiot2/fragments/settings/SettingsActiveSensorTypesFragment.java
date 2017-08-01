package org.gortz.greeniot.smartcityiot2.fragments.settings;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import java.util.ArrayList;
import org.gortz.greeniot.smartcityiot2.R;
import org.gortz.greeniot.smartcityiot2.activity.SettingsActivity;
import org.gortz.greeniot.smartcityiot2.database.entity.SensorType;
import org.gortz.greeniot.smartcityiot2.view.NonScrollListView;

/**
 * Display and update which sensor types are active.
 */
public class SettingsActiveSensorTypesFragment extends Fragment {
    private NonScrollListView activeSensorTypeList;
    private SettingsActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_active_sensor_types_view, container, false);
        this.activity = (SettingsActivity) getActivity();
        activity.hideDrawer();
        activeSensorTypeList = (NonScrollListView)v.findViewById(R.id.active_sensor_type_list);
        activeSensorTypeList.setAdapter(new ActiveSensorTypeListviewContactAdapter(getActivity(), activity.getAllSupportedSensorTypes()));

        return v;
    }

    private final class ActiveSensorTypeListviewContactAdapter extends BaseAdapter {
        private ArrayList<SensorType> activeSensorTypes;

        private LayoutInflater mInflater;

        public ActiveSensorTypeListviewContactAdapter(Context photosFragment, ArrayList<SensorType> results){
            activeSensorTypes = results;
            mInflater = LayoutInflater.from(photosFragment);
        }

        @Override
        public int getCount() {
            return activeSensorTypes.size();
        }

        @Override
        public Object getItem(int arg0) {
            return activeSensorTypes.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return activeSensorTypes.get(arg0).getId();
        }

        @Override public boolean areAllItemsEnabled() {
            return true;
        }
        @Override public boolean isEnabled(int position) {
            return true;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ActiveSensorTypeListviewContactAdapter.ViewHolder holder;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.sensor_type_item_with_toggle, null);

                holder = new ActiveSensorTypeListviewContactAdapter.ViewHolder();
                holder.sensorTypeName = (TextView) convertView.findViewById(R.id.sensor_type_name);

                convertView.setTag(holder);

            } else {
                holder = (ActiveSensorTypeListviewContactAdapter.ViewHolder) convertView.getTag();
            }

            Switch activeSensorType = (Switch) convertView.findViewById(R.id.active_type);

            activeSensorType.setOnCheckedChangeListener(null);
            activeSensorType.setChecked(activeSensorTypes.get(position).isActive());

            activeSensorType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    activity.setSensorTypeActive(activeSensorTypes.get(position).getId(), isChecked);
                    System.out.println("Changed toggle to: " + isChecked);
                }
            });
            holder.sensorTypeName.setText(activeSensorTypes.get(position).getName());

            return convertView;
        }

        private class ViewHolder{
            TextView sensorTypeName;
        }
    }
}
