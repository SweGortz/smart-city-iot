package org.gortz.greeniot.smartcityiot.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import org.gortz.greeniot.smartcityiot.R;
import org.gortz.greeniot.smartcityiot.activity.SettingsActivity;
import org.gortz.greeniot.smartcityiot.database.entity.Coordinate;
import org.gortz.greeniot.smartcityiot.database.entity.Location;
import org.gortz.greeniot.smartcityiot.view.NonScrollListView;

/**
 * Handles location settings
 */
public class SettingsLocationsFragment extends Fragment {
    SettingsActivity activity;
    NonScrollListView locationsWithCoordsList;
    NonScrollListView locationsWithoutCoordsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_locations_view, container, false);
        activity = (SettingsActivity) getActivity();
        activity.hideDrawer();

        locationsWithCoordsList = (NonScrollListView)v.findViewById(R.id.with_location_list);
        locationsWithoutCoordsList = (NonScrollListView)v.findViewById(R.id.without_location_list);

        ArrayList<Location> locationsWithCoords = activity.getAllLocationsWithCoords();
        ArrayList<Location> locationsWithoutCoords = activity.getAllLocationsWithoutCoords();
        locationsWithCoordsList.setAdapter(new SettingsLocationsFragment.LocationsListviewContactAdapter(getActivity(), locationsWithCoords));
        locationsWithoutCoordsList.setAdapter(new SettingsLocationsFragment.LocationsListviewContactAdapter(getActivity(), locationsWithoutCoords));

        locationsWithoutCoordsList.setOnItemClickListener(new OnLocationItemClickListener());
        locationsWithCoordsList.setOnItemClickListener(new OnLocationItemClickListener());

        return v;
    }

    /**
     *  location list item click handler
     */
    private class OnLocationItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LocationFragment locationFragment = new LocationFragment();
            Bundle args = new Bundle();
            args.putInt("id", (int) id);
            locationFragment.setArguments(args);
            activity.goToFragment(locationFragment);
        }
    }

    /**
     * Handle location list
     */
    private final class LocationsListviewContactAdapter extends BaseAdapter {
        private ArrayList<Location> locations;

        private LayoutInflater mInflater;

        public LocationsListviewContactAdapter(Context photosFragment, ArrayList<Location> results){
            locations = results;
            mInflater = LayoutInflater.from(photosFragment);
        }

        /**
         * Get current size of list
         * @return size of list
         */
        @Override
        public int getCount() {
            return locations.size();
        }

        /**
         * Get item at position in list
         * @param position of item
         * @return item at position
         */
        @Override
        public Object getItem(int position) {
            return locations.get(position);
        }

        /**
         * Get item id at position
         * @param position of item
         * @return id of item
         */
        @Override
        public long getItemId(int position) {
            return locations.get(position).getId();
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
            SettingsLocationsFragment.LocationsListviewContactAdapter.ViewHolder holder;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.location_item, null);

                holder = new SettingsLocationsFragment.LocationsListviewContactAdapter.ViewHolder();
                holder.locationName = (TextView) convertView.findViewById(R.id.location_name);
                holder.locationLat = (TextView) convertView.findViewById(R.id.location_lat);
                holder.locationLon = (TextView) convertView.findViewById(R.id.location_lon);
                convertView.setTag(holder);

            } else {
                holder = (SettingsLocationsFragment.LocationsListviewContactAdapter.ViewHolder) convertView.getTag();
            }

            Coordinate coordinate = locations.get(position).getPosition();
            holder.locationName.setText(locations.get(position).getName());
            String lat = "";
            String lon = "";
            if(coordinate != null){
                lat = String.valueOf(locations.get(position).getPosition().getLat());
                lon = String.valueOf(locations.get(position).getPosition().getLon());
            }
            holder.locationLat.setText(lat);
            holder.locationLon.setText(lon);
            return convertView;
        }

        private class ViewHolder{
            TextView locationName,locationLat,locationLon;
        }
    }

}
