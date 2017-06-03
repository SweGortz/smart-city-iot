package org.gortz.greeniot.smartcityiot.fragments.sensors;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import org.gortz.greeniot.smartcityiot.R;
import org.gortz.greeniot.smartcityiot.activity.SensorActivity;
import org.gortz.greeniot.smartcityiot.activity.base.CommunicationChannel;
import org.gortz.greeniot.smartcityiot.dto.sensors.SensorNodeKey;
import org.gortz.greeniot.smartcityiot.database.entity.SensorType;
import org.gortz.greeniot.smartcityiot.dto.sensors.SensorTypeNode;
import org.gortz.greeniot.smartcityiot.fragments.sensors.base.SensorOptionFragment;

/**
 * List visualization of sensor values.
 */
public class SensorsFragment extends SensorOptionFragment implements CommunicationChannel{
    private final String TAG = SensorsFragment.class.getSimpleName();
    private SensorActivity activity;
    private SensorType sensorType;
    private ListView lv;
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private ListviewContactAdapter listViewContactAdapter;
    private String[] sensorNames;
    private String[] organizationNames;
    private boolean fromMapCluster = false;

    @Override
    public void changeViewOption(int viewOption) {
        System.out.println("View option: " + viewOption);
        lv.setAdapter(new ListviewContactAdapter(getActivity(), new ArrayList<>(activity.getSensorDataOfType(viewOption))));
        sensorType = activity.getSensorTypeByID(viewOption);
        System.out.println("Standard sensor type: " + sensorType.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (SensorActivity) getActivity();
        activity.invalidateOptionsMenu();
        activity.showDrawer();
        activity.setCurrentFragment(this);
        View v = inflater.inflate(R.layout.sensors_view, container, false);
        lv = (ListView)v.findViewById(android.R.id.list);
        lv.setOnItemClickListener(new OnSensorItemClickListener());

        Bundle args = getArguments();
        if(args != null){
            int sensorTypeID = args.getInt("typeID", -1);
            activity.setCurrentSensorTypeId(sensorTypeID);
            fromMapCluster = true;
            sensorType = activity.getSensorTypeByID(sensorTypeID);
            ArrayList<SensorTypeNode> sensorNodes = new ArrayList<>();
            sensorNames = args.getStringArray("sensorNames");
            organizationNames = args.getStringArray("organizationNames");
            for(int i = 0; i < sensorNames.length; i++){
                sensorNodes.add(activity.getSensorNodeOfTypeByID(new SensorNodeKey(sensorNames[i],organizationNames[i]),sensorTypeID));
            }
            listViewContactAdapter = new ListviewContactAdapter(getActivity(), sensorNodes);
            lv.setAdapter(listViewContactAdapter);
        }else {
            listViewContactAdapter = new ListviewContactAdapter(getActivity(), new ArrayList<>(activity.getSensorDataOfType(activity.getCurrentSensorTypeId())));
            lv.setAdapter(listViewContactAdapter);
            sensorType = activity.getSensorTypeByID(activity.getCurrentSensorTypeId());
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer1 = new RefreshList();
        mHandler.postDelayed(mTimer1, 300);
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer1);
        super.onPause();
    }

    private class OnSensorItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i("test","Going to id:"+id+"'s detail with type: " + sensorType.getId());
            ListView l = (ListView) parent;
            activity.goToSensorDetails((SensorTypeNode) l.getItemAtPosition(position), sensorType.getId());

        }
    }


    private final class ListviewContactAdapter extends BaseAdapter {
        private ArrayList<SensorTypeNode> sensors;

        private LayoutInflater mInflater;

        public ListviewContactAdapter(Context photosFragment, ArrayList<SensorTypeNode> results){
            sensors = results;
            mInflater = LayoutInflater.from(photosFragment);
        }

        @Override
        public int getCount() {
            return sensors.size();
        }

        @Override
        public Object getItem(int arg0) {
            return sensors.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        public void resetData(ArrayList<SensorTypeNode> list){
            sensors = list;
            notifyDataSetChanged();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.sensor_item, null);
                holder = new ViewHolder();
                holder.sensorName = (TextView) convertView.findViewById(R.id.sensor_name);
                holder.sensorId = (TextView) convertView.findViewById(R.id.sensorid);
                holder.sensorValue = (TextView) convertView.findViewById(R.id.sensorvalue);
                holder.organization = (TextView) convertView.findViewById(R.id.organization);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.sensorName.setText(sensors.get(position).getLocation().getName());
            holder.sensorId.setText(String.valueOf(sensors.get(position).getName()));
            holder.sensorValue.setText(String.valueOf(sensors.get(position).getLatestValue().getValue()) + " " + sensorType.getUnit());
            holder.organization.setText(sensors.get(position).getOrganization());

            return convertView;
        }

        private class ViewHolder{
            TextView sensorName,sensorId,sensorValue, organization;
        }
    }

    private class RefreshList implements Runnable{
        @Override
        public void run() {
            Log.i(TAG, "Refreshing sensor list");
            ArrayList<SensorTypeNode> list;
            if(fromMapCluster){
                list = new ArrayList<>();
                int sensorTypeID = activity.getCurrentSensorTypeId();
                for(int i = 0; i < sensorNames.length; i++){
                    list.add(activity.getSensorNodeOfTypeByID(new SensorNodeKey(sensorNames[i],organizationNames[i]),sensorTypeID));
                }
            }
            else{
                list = new ArrayList<>(activity.getSensorDataOfType(activity.getCurrentSensorTypeId()));
            }

            listViewContactAdapter.resetData(list);
            mHandler.postDelayed(this, 10000);
        }
    }
}
