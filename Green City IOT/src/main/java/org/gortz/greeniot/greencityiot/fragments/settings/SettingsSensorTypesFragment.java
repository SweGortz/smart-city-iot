package org.gortz.greeniot.greencityiot.fragments.settings;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import org.gortz.greeniot.greencityiot.R;
import org.gortz.greeniot.greencityiot.activity.SettingsActivity;
import org.gortz.greeniot.greencityiot.database.entity.SensorType;
import org.gortz.greeniot.greencityiot.database.entity.TypeAlias;
import org.gortz.greeniot.greencityiot.view.NonScrollListView;

/**
 * Handling sensor type and alias settings
 */
public class SettingsSensorTypesFragment extends Fragment {
    private NonScrollListView sensorTypeList;
    private NonScrollListView typeAliasList;
    private SettingsActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_sensor_types_view, container, false);
        this.activity = (SettingsActivity) getActivity();
        activity.hideDrawer();


        sensorTypeList = (NonScrollListView)v.findViewById(R.id.sensor_type_list);
        sensorTypeList.setAdapter(new SensorTypeListviewContactAdapter(getActivity(), activity.getAllSensorTypes()));
        sensorTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SensorTypeFragment sensorTypeFragment = new SensorTypeFragment();
                Bundle args = new Bundle();
                args.putInt("id",(int)id);
                sensorTypeFragment.setArguments(args);
                activity.goToFragment(sensorTypeFragment);
            }
        });

        typeAliasList = (NonScrollListView)v.findViewById(R.id.type_alias_list);
        typeAliasList.setAdapter(new TypeAliasListviewContactAdapter(getActivity(), activity.getAllTypeAliases()));
        typeAliasList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TypeAliasFragment typeAliasFragment = new TypeAliasFragment();
                Bundle args = new Bundle();
                TypeAlias typeAlias = (TypeAlias)typeAliasList.getItemAtPosition(position);
                args.putInt("id",typeAlias.getId());
                args.putInt("typeID",typeAlias.getTypeID());
                args.putString("name",typeAlias.getName());
                typeAliasFragment.setArguments(args);
                activity.goToFragment(typeAliasFragment);
            }
        });
        return v;
    }

    /**
     * Handles sensor type list
     */
    private final class SensorTypeListviewContactAdapter extends BaseAdapter {
        private ArrayList<SensorType> sensorTypes;

        private LayoutInflater mInflater;

        public SensorTypeListviewContactAdapter(Context photosFragment, ArrayList<SensorType> results){
            sensorTypes = results;
            mInflater = LayoutInflater.from(photosFragment);
        }

        /**
         * Get current size of list
         * @return size of list
         */
        @Override
        public int getCount() {
            return sensorTypes.size();
        }

        /**
         * Get item at position in list
         * @param position of item
         * @return item at position
         */
        @Override
        public Object getItem(int position) {
            return sensorTypes.get(position);
        }

        /**
         * Get item id at position
         * @param position of item
         * @return id of item
         */
        @Override
        public long getItemId(int position) {
            return sensorTypes.get(position).getId();
        }

        /**
         * Check if all items are enabled
         * @return true if all items are enabled
         */
        @Override public boolean areAllItemsEnabled() {
            return true;
        }

        /**
         * Check if item at position is enable
         * @param position of item
         * @return true if item at location is enabled
         */
        @Override public boolean isEnabled(int position) {
            return true;
        }

        /**
         * Create view for list items
         * @param position of item
         * @param convertView of item
         * @param parent of item
         * @return view of current list item
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            SettingsSensorTypesFragment.SensorTypeListviewContactAdapter.ViewHolder holder;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.sensor_type_item, null);

                holder = new SettingsSensorTypesFragment.SensorTypeListviewContactAdapter.ViewHolder();
                holder.sensorTypeName = (TextView) convertView.findViewById(R.id.sensor_type_name);
                convertView.setTag(holder);

            } else {
                holder = (SettingsSensorTypesFragment.SensorTypeListviewContactAdapter.ViewHolder) convertView.getTag();
            }

            holder.sensorTypeName.setText(sensorTypes.get(position).getName());

            return convertView;
        }

        private class ViewHolder{
            TextView sensorTypeName;
        }
    }

    /**
     * Handles sensor alias list
     */
    private final class TypeAliasListviewContactAdapter extends BaseAdapter {
        private ArrayList<TypeAlias> typeAliases;

        private LayoutInflater mInflater;

        public TypeAliasListviewContactAdapter(Context photosFragment, ArrayList<TypeAlias> results){
            typeAliases = results;
            mInflater = LayoutInflater.from(photosFragment);
        }

        /**
         * Get current size of list
         * @return size of list
         */
        @Override
        public int getCount() {
            return typeAliases.size();
        }

        /**
         * Get item at position in list
         * @param position of item
         * @return item at position
         */
        @Override
        public Object getItem(int position) {
            return typeAliases.get(position);
        }

        /**
         * Get item id at position
         * @param position of item
         * @return id of item
         */
        @Override
        public long getItemId(int position) {
            return typeAliases.get(position).getId();
        }

        /**
         * Check if all items are enabled
         * @return true if all items are enabled
         */
        @Override public boolean areAllItemsEnabled() {
            return true;
        }

        /**
         * Check if item at position is enable
         * @param position of item
         * @return true if item at location is enabled
         */
        @Override public boolean isEnabled(int position) {
            return true;
        }

        /**
         * Create view for list items
         * @param position of item
         * @param convertView of item
         * @param parent of item
         * @return view of current list item
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            SettingsSensorTypesFragment.TypeAliasListviewContactAdapter.ViewHolder holder;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.type_alias_item, null);

                holder = new SettingsSensorTypesFragment.TypeAliasListviewContactAdapter.ViewHolder();
                holder.typeAliasName = (TextView) convertView.findViewById(R.id.type_alias_name);
                convertView.setTag(holder);

            } else {
                holder = (SettingsSensorTypesFragment.TypeAliasListviewContactAdapter.ViewHolder) convertView.getTag();
            }

            holder.typeAliasName.setText(typeAliases.get(position).getName());

            return convertView;
        }

        private class ViewHolder{
            TextView typeAliasName;
        }
    }
}
