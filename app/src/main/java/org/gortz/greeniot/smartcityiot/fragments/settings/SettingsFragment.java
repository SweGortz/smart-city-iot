package org.gortz.greeniot.smartcityiot.fragments.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import java.util.ArrayList;

import org.gortz.greeniot.smartcityiot.R;
import org.gortz.greeniot.smartcityiot.activity.SettingsActivity;
import org.gortz.greeniot.smartcityiot.database.entity.Location;
import org.gortz.greeniot.smartcityiot.database.entity.SensorType;
import org.gortz.greeniot.smartcityiot.database.entity.Connection;
import org.gortz.greeniot.smartcityiot.database.entity.TypeAlias;

/**
 * Handles main settings menu.
 */
public class SettingsFragment extends Fragment {
    private SettingsActivity activity;
    private Switch advancedFeatures;
    private boolean showAdvancedFeatures = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_view, container, false);
        advancedFeatures = (Switch) v.findViewById(R.id.advanced_features_switch);
        this.activity = (SettingsActivity) getActivity();
        activity.showDrawer();

        advancedFeatures.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showAdvancedFeatures = isChecked;
                if(showAdvancedFeatures){
                    getView().findViewById(R.id.advanced_features).setVisibility(View.VISIBLE);
                }
                else{
                    getView().findViewById(R.id.advanced_features).setVisibility(View.GONE);
                }
            }
        });
        return v;
    }

    /**
     * Communication channel from fragment to activity.
     */
    public interface SettingsActivityCommunicationChannel{
        /**
         * Get all sensor node locations containing GPS coordinates.
         * @return ArrayList of Locations.
         */
        ArrayList<Location> getAllLocationsWithCoords();

        /**
         * Get all sensor node locations without GPS coordinates.
         * @return ArrayList of Locations.
         */
        ArrayList<Location> getAllLocationsWithoutCoords();

        /**
         * Get all connections of specific connection type.
         * @param type Connection type.
         * @return ArrayList of Connections of specified type.
         */
        ArrayList<Connection> getAllConnectionsByType(String type);

        /**
         * Get all sensor types.
         * @return ArrayList of SensorTypes.
         */
        ArrayList<SensorType> getAllSensorTypes();

        /**
         * Get all sensor types that are supported in the application.
         * @return ArrayList of SensorTypes that are supported.
         */
        ArrayList<SensorType> getAllSupportedSensorTypes();

        /**
         * Get all Topic Structures of a specific connection group.
         * @param connectionGroup Name of connection group.
         * @return ArrayList of TopicStructures belonging to specified connection group.
         */
        ArrayList getAllTopicStructuresByType(String connectionGroup);

        /**
         * Get all Data Structures of a specific connection group.
         * @param connectionGroup Name of connection group
         * @return ArrayList of DataStructures belonging to specified connection group.
         */
        ArrayList getAllDataStructuresByType(String connectionGroup);

        /**
         * Get all sensor type names.
         * @return ArrayList of sensor type names.
         */
        ArrayList getAllSensorTypeNames();

        /**
         * Get all type aliases.
         * @return ArrayList of type aliases.
         */
        ArrayList getAllTypeAliases();

        /**
         * Get Spinner item list ID of Topic Structure.
         * @param topicStructureID ID of Topic Structure.
         * @return Spinner item list ID of specified Topic Structure.
         */
        int getTopicStructureListID(int topicStructureID);

        /**
         * Get Spinner item list ID of Data Structure.
         * @param dataStructureID ID of Data Structure.
         * @return Spinner item list ID of specified Data Structure.
         */
        int getDataStructureListID(int dataStructureID);

        /**
         * Get Spinner item list ID of Sensor Type Name.
         * @param sensorTypeID ID of Sensor Type Name.
         * @return Spinner item list ID of specified Sensor Type Name.
         */
        int getSensorTypeNameListID(int sensorTypeID);

        /**
         * Get Type Alias by ID.
         * @param typeAliasID ID of Type Alias.
         * @return TypeAlias with specified ID.
         */
        TypeAlias getTypeAliasById(int typeAliasID);

        /**
         * Add a new connection.
         * @param connection Connection to be added.
         */
        void addConnection(Connection connection);

        /**
         * Update existing connection.
         * @param connection Updated connection.
         */
        void updateConnection(Connection connection);

        /**
         * Update location.
         * @param location Updated location.
         */
        void updateLocation(Location location);

        /**
         * Update sensor type.
         * @param sensorType Updated SensorType.
         */
        void updateSensorType(SensorType sensorType);

        /**
         * Associate type alias with a new sensor type.
         * @param typeAliasID ID of TypeAlias to be re-associated.
         * @param sensorTypeID ID of SensorType the TypeAlias should be associated with.
         */
        void updateTypeAlias(int typeAliasID, int sensorTypeID);

        /**
         * Recreate original SensorType of specific TypeAlias and re-associate.
         * @param typeAliasID ID of Type alias.
         */
        void releaseAsAliasByID(int typeAliasID);

        /**
         * Remove specified type alias.
         * @param typeAliasID ID of TypeAlias to be removed.
         */
        void deleteTypeAlias(int typeAliasID);

        /**
         * Go to specific fragment.
         * @param fragment New fragment to go to.
         */
        void goToFragment(Fragment fragment);

        /**
         * Go to specific fragment wihout adding current fragment to back stack.
         * @param fragment New fragment to go to.
         */
        void goToFragmentWithoutBack(Fragment fragment);

        /**
         * Get connection by connection type and ID.
         * @param type Connection type.
         * @param id Connection id.
         * @return
         */
        Connection getConnectionByTypeAndID(String type, int id);

        /**
         * Get Location by ID.
         * @param id Location ID.
         * @return Location with specified ID.
         */
        Location getLocationByID(int id);

        /**
         * Get sensor type by ID.
         * @param id Sensor type id.
         * @return SensorType of specified ID.
         */
        SensorType getSensorTypeByID(int id);

        /**
         * Set specific connection as active/inactive.
         * @param type Connection type.
         * @param id Connection ID.
         * @param active Active/Inactive.
         */
        void setConnectionsActive(String type, int id, boolean active);

        /**
         * Set sensor type as active/inactive.
         * @param id Sensor type ID.
         * @param active Active/Inactive.
         */
        void setSensorTypeActive(int id, boolean active);
    }

}
