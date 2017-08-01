package org.gortz.greeniot.smartcityiot2.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import java.util.ArrayList;

import org.gortz.greeniot.smartcityiot2.R;
import org.gortz.greeniot.smartcityiot2.activity.SettingsActivity;
import org.gortz.greeniot.smartcityiot2.view.NonScrollListView;
import org.gortz.greeniot.smartcityiot2.database.entity.Connection;

/**
 * Displays all connections and handles creation of new connections.
 */
public class SettingsConnectionsFragment extends Fragment {
    private NonScrollListView mqttList;
    private NonScrollListView apiList;
    private SettingsActivity activity;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.settings_connections_view, container, false);
        this.activity = (SettingsActivity) getActivity();
        activity.hideDrawer();
        mqttList = (NonScrollListView)v.findViewById(R.id.mqtt_list);
        mqttList.setAdapter(new MqttListviewContactAdapter(getActivity(), activity.getAllConnectionsByType("mqtt")));
        mqttList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MQTTFragment mqttFragment = new MQTTFragment();
                Bundle args = new Bundle();
                args.putInt("id",(int) id);
                mqttFragment.setArguments(args);
                activity.goToFragment(mqttFragment);
            }
        });

        apiList = (NonScrollListView)v.findViewById(R.id.api_list);
        apiList.setAdapter(new APIListviewContactAdapter(getActivity(), activity.getAllConnectionsByType("restapi")));
        apiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RestAPIFragment restAPIFragment = new RestAPIFragment();
                Bundle args = new Bundle();
                args.putInt("id",(int) id);
                restAPIFragment.setArguments(args);
                activity.goToFragment(restAPIFragment);
            }
        });

        return v;
    }

    private final class MqttListviewContactAdapter extends BaseAdapter {
        private ArrayList<Connection> connections;
        private final String CONNECTION_TYPE = "mqtt";

        private LayoutInflater mInflater;

        public MqttListviewContactAdapter(Context photosFragment, ArrayList<Connection> results){
            connections = results;
            mInflater = LayoutInflater.from(photosFragment);
        }

        @Override
        public int getCount() {
            return connections.size();
        }

        @Override
        public Object getItem(int arg0) {
            return connections.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return connections.get(arg0).getId();
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            MqttListviewContactAdapter.ViewHolder holder;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.connection_item_broker, null);

                holder = new MqttListviewContactAdapter.ViewHolder();
                holder.connectionURL = (TextView) convertView.findViewById(R.id.connection_url);
                holder.connectionDescription = (TextView) convertView.findViewById(R.id.connection_description);
                convertView.setTag(holder);

            } else {
                holder = (MqttListviewContactAdapter.ViewHolder) convertView.getTag();
            }

            Switch activeConnection = (Switch) convertView.findViewById(R.id.active_connection);

            activeConnection.setOnCheckedChangeListener(null);
            activeConnection.setChecked(connections.get(position).isActive());

            holder.connectionDescription.setText(connections.get(position).getArg0());
            holder.connectionURL.setText(connections.get(position).getUrl()+":"+String.valueOf(connections.get(position).getPort()));

            activeConnection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    activity.setConnectionsActive(CONNECTION_TYPE, connections.get(position).getId(),isChecked);
                }
            });
            return convertView;
        }

        private class ViewHolder{
            TextView connectionURL,connectionDescription;
        }
    }

    private final class APIListviewContactAdapter extends BaseAdapter {
        private ArrayList<Connection> connections;
        private final String CONNECTION_TYPE = "restapi";


        private LayoutInflater mInflater;

        public APIListviewContactAdapter(Context photosFragment, ArrayList<Connection> results){
            connections = results;
            mInflater = LayoutInflater.from(photosFragment);
        }

        @Override
        public int getCount() {
            return connections.size();
        }

        @Override
        public Object getItem(int arg0) {
            return connections.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return connections.get(arg0).getId();
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            APIListviewContactAdapter.ViewHolder holder;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.connection_item_api, null);

                holder = new APIListviewContactAdapter.ViewHolder();
                holder.connectionURL = (TextView) convertView.findViewById(R.id.connection_url);
                convertView.setTag(holder);

            } else {
                holder = (APIListviewContactAdapter.ViewHolder) convertView.getTag();
            }

            Switch activeConnection = (Switch) convertView.findViewById(R.id.active_api);

            activeConnection.setOnCheckedChangeListener(null);
            activeConnection.setChecked(connections.get(position).isActive());

            holder.connectionURL.setText(connections.get(position).getUrl()+":"+String.valueOf(connections.get(position).getPort()));

            activeConnection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    activity.setConnectionsActive(CONNECTION_TYPE, connections.get(position).getId(),isChecked);
                }
            });
            return convertView;
        }

        private class ViewHolder{
            TextView connectionURL;
        }
    }
}
