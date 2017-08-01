package org.gortz.greeniot.smartcityiot2.activity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.gortz.greeniot.smartcityiot2.activity.base.BaseActivity;
import org.gortz.greeniot.smartcityiot2.database.SensorDataDAO;
import org.gortz.greeniot.smartcityiot2.database.entity.ConnectionGroup;
import org.gortz.greeniot.smartcityiot2.database.entity.SensorLimits;
import org.gortz.greeniot.smartcityiot2.database.entity.SensorType;
import org.gortz.greeniot.smartcityiot2.dto.listitems.SpinnerItemEntry;
import org.gortz.greeniot.smartcityiot2.database.entity.Location;
import org.gortz.greeniot.smartcityiot2.database.entity.Connection;
import org.gortz.greeniot.smartcityiot2.database.entity.TypeAlias;
import org.gortz.greeniot.smartcityiot2.dto.locations.LocationsDbDTO;
import org.gortz.greeniot.smartcityiot2.dto.sensors.SensorTypeDbDTO;
import org.gortz.greeniot.smartcityiot2.fragments.settings.AboutFragment;
import org.gortz.greeniot.smartcityiot2.fragments.settings.MQTTFragment;
import org.gortz.greeniot.smartcityiot2.fragments.settings.RestAPIFragment;
import org.gortz.greeniot.smartcityiot2.fragments.settings.SettingsActiveSensorTypesFragment;
import org.gortz.greeniot.smartcityiot2.fragments.settings.SettingsConnectionsFragment;
import org.gortz.greeniot.smartcityiot2.fragments.settings.SettingsFragment;
import org.gortz.greeniot.smartcityiot2.fragments.settings.SettingsLocationsFragment;
import org.gortz.greeniot.smartcityiot2.fragments.settings.SettingsSensorTypesFragment;

/**
 * Activity that handles the settings feature
 */
public class SettingsActivity extends BaseActivity implements SettingsFragment.SettingsActivityCommunicationChannel {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    HashMap<String, HashMap<Integer,Connection>> connections = new HashMap<>();
    HashMap<Integer,Location> locationsWithCoords = new HashMap<>();
    HashMap<Integer,Location> locationsWithoutCoords = new HashMap<>();
    HashMap<Integer,SensorType> sensorTypes;
    HashMap<Integer,TypeAlias> typeAliases;
    HashMap<String, ConnectionGroup> connectionGroups = new HashMap<>();
    private HashMap<String, ArrayList<SpinnerItemEntry<Integer, String>>> topicStructures = new HashMap<>();
    private HashMap<String, ArrayList<SpinnerItemEntry<Integer, String>>> dataStructures = new HashMap<>();
    private ArrayList<SpinnerItemEntry<Integer, String>> sensorTypeNames;
    private HashMap<Integer, Integer> topicMap = new HashMap<>();
    private HashMap<Integer, Integer> dataMap = new HashMap<>();
    private HashMap<Integer, Integer> sensorTypeMap;
    SensorDataDAO sensorDataDAO;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.startActivityFrame();
        context = getApplicationContext();
        sensorDataDAO = SensorDataDAO.getInstance(context);
        getSettingsFromDB();
        setCurrentFragment(new SettingsFragment());
        setFragmentWithoutBackStack(getCurrentFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "About");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == 0){
            setCurrentFragment(new AboutFragment());
            goToFragment(getCurrentFragment());
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Change fragment to add new MQTT connection view
     * @param currentView of the application
     */
    public void addMQTTBrokerView(View currentView){
        setCurrentFragment(new MQTTFragment());
        setFragmentOnBackStack(getCurrentFragment());
    }

    /**
     * Change fragment to add api connection view
     * @param currentView of the application
     */
    public void addRestAPIView(View currentView){
        setCurrentFragment(new RestAPIFragment());
        setFragmentOnBackStack(getCurrentFragment());
    }

    /**
     * Change fragment to connection view
     * @param currentView of the application
     */
    public void goToConnectionsView(View currentView){
        setCurrentFragment(new SettingsConnectionsFragment());
        setFragmentOnBackStack(getCurrentFragment());
    }

    /**
     * Change fragment to location view
     * @param currentView of the application
     */
    public void goToLocationsView(View currentView){
        setCurrentFragment(new SettingsLocationsFragment());
        setFragmentOnBackStack(getCurrentFragment());
    }

    /**
     * Change fragment to sensor type view
     * @param currentView of the application
     */
    public void goToSensorTypesView(View currentView){
        setCurrentFragment(new SettingsSensorTypesFragment());
        setFragmentOnBackStack(getCurrentFragment());
    }

    /**
     * Change fragment to active sensor type view
     * @param currentView of the application
     */
    public void goToActiveSensorTypesView(View currentView){
        setCurrentFragment(new SettingsActiveSensorTypesFragment());
        setFragmentOnBackStack(getCurrentFragment());
    }

    // Todo not implemented yet
    public void goToChangeMapStyleView(View currentView){
        CharSequence text = "Will be implemented soon!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        /*setCurrentFragment(new SettingsChangeMapStyleFragment());
         setFragmentOnBackStack(getCurrentFragment());*/
    }

    @Override
    public ArrayList<Location> getAllLocationsWithCoords() {
        return new ArrayList<>(locationsWithCoords.values());
    }

    @Override
    public ArrayList<Location> getAllLocationsWithoutCoords() {
        return new ArrayList<>(locationsWithoutCoords.values());
    }

    @Override
    public ArrayList<Connection> getAllConnectionsByType(String type) {
        return new ArrayList<>(connections.get(type).values());
    }

    @Override
    public ArrayList<SensorType> getAllSensorTypes() {
        return new ArrayList<>(sensorTypes.values());
    }

    @Override
    public ArrayList<SensorType> getAllSupportedSensorTypes() {
        ArrayList<SensorType> supportedSensorTypes = new ArrayList();
        for(SensorType item : sensorTypes.values()){
            if(item.isSupported()){
                supportedSensorTypes.add(item);
            }
        }
        return supportedSensorTypes;
    }

    @Override
    public ArrayList getAllTopicStructuresByType(String type) {
        return topicStructures.get(type);
    }

    @Override
    public ArrayList getAllDataStructuresByType(String type) {
        return dataStructures.get(type);
    }

    @Override
    public ArrayList getAllSensorTypeNames() {
        return sensorTypeNames;
    }

    @Override
    public ArrayList getAllTypeAliases() {
        return new ArrayList<>(typeAliases.values());
    }

    @Override
    public int getTopicStructureListID(int topicStructureID) {
        return topicMap.get(topicStructureID);
    }

    @Override
    public int getDataStructureListID(int dataStructureID) {
        return dataMap.get(dataStructureID);
    }

    @Override
    public int getSensorTypeNameListID(int sensorTypeAliasID) {
        return sensorTypeMap.get(sensorTypeAliasID);
    }

    @Override
    public TypeAlias getTypeAliasById(int typeAliasID) {
        return typeAliases.get(typeAliasID);
    }

    @Override
    public void addConnection(Connection connection) {
        try {
            SensorDataDAO.getInstance(context).createConnection(connection);
            connections.get(connection.getConnectionType()).put(connection.getId(), connection);
            Toast.makeText(context, "New connection have been added", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Requires restart to work", Toast.LENGTH_SHORT).show(); //todo fix without restart;
        }catch (Exception e) {
            Toast.makeText(context, "Error: A connection couldn't be added to the application", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateConnection(Connection connection) {
        try {
            SensorDataDAO.getInstance(context).updateConnection(connection);
            connections.get(connection.getConnectionType()).put(connection.getId(), connection);
            Toast.makeText(context, "Updated a connection", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Requires restart to work", Toast.LENGTH_SHORT).show(); //todo fix without restart
        }catch (Exception e) {
            Toast.makeText(context,  "Error: Couldn't update a connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateLocation(Location location) {
        CharSequence text;
        try {
            SensorDataDAO.getInstance(context).updateLocation(location);
            text = "Updated a Location";
            if(locationsWithCoords.containsKey(location.getId())){
                locationsWithCoords.put(location.getId(), location);
            }
            else{
                locationsWithoutCoords.remove(location.getId());
                locationsWithCoords.put(location.getId(), location);
            }
        }catch (Exception e) {
            text = "Error: Couldn't update a Location";
        }
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateSensorType(SensorType sensorType) {
        CharSequence toastMessage;
        try{
            SensorDataDAO.getInstance(context).updateSensorType(sensorType);
            toastMessage = "Updated sensor type " + sensorType.getName();
        }
        catch(Exception e){
            toastMessage = "Error: Couldn't update a sensor type";
        }
        getSensorTypesFromDB();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, toastMessage, duration);
        toast.show();
    }

    @Override
    public void updateTypeAlias(int typeAliasID, int sensorTypeID) {
        CharSequence toastMessage;
        try{
            int prevTypeID = getTypeAliasById(typeAliasID).getTypeID();
            SensorDataDAO.getInstance(context).updateTypeAlias(prevTypeID, typeAliasID, sensorTypeID);
            getSensorTypesFromDB();
            getTypeAliasFromDB();
            toastMessage = "Updated type alias";
        }
        catch(Exception e){
            e.printStackTrace();
            toastMessage = "Error: Couldn't update type alias";
        }
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, toastMessage, duration);
        toast.show();
    }

    @Override
    public void releaseAsAliasByID(int typeAliasID) {
        CharSequence toastMessage;
        try{
            SensorType sensorType = new SensorType(typeAliases.get(typeAliasID).getName(), "", true, false, new SensorLimits(0,0,0));
            SensorDataDAO.getInstance(context).releaseAsAliasByID(typeAliasID, sensorType);
            toastMessage = "Updated type alias";
        }
        catch(Exception e){
            toastMessage = "Error: Couldn't update type alias";
        }
        getSensorTypesFromDB();
        getTypeAliasFromDB();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, toastMessage, duration);
        toast.show();
    }

    @Override
    public void deleteTypeAlias(int typeAliasID) {
        CharSequence toastMessage;

        try{
            SensorDataDAO.getInstance(context).deleteTypeAlias(typeAliasID);
            deleteSensorTypeByID(typeAliases.get(typeAliasID).getTypeID());
            toastMessage = "Deleted type alias";
        }
        catch(Exception e){
            e.printStackTrace();
            toastMessage = "Error: Couldn't delete type alias";
        }
        getSensorTypesFromDB();
        getTypeAliasFromDB();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, toastMessage, duration);
        toast.show();
    }

    @Override
    public void goToFragment(Fragment fragment) {
        String currentFragmentName = getCurrentFragment().getClass().getSimpleName();
        setFragmentOnBackStack(fragment, currentFragmentName);
    }

    @Override
    public Connection getConnectionByTypeAndID(String type, int id) {
        return connections.get(type).get(id);
    }

    @Override
    public Location getLocationByID(int id) {
        if(locationsWithCoords.containsKey(id)){
            return locationsWithCoords.get(id);
        }
        else {
            return locationsWithoutCoords.get(id);
        }
    }

    @Override
    public SensorType getSensorTypeByID(int id) {
        return sensorTypes.get(id);
    }

    @Override
    public void setConnectionsActive(String type, int id, boolean active) {
        try{
            SensorDataDAO.getInstance(getApplicationContext()).setConnectionsActive(id, active);
            connections.get(type).get(id).setActive(active);
            Toast.makeText(this, "Requires restart to work", Toast.LENGTH_SHORT).show(); //todo fix without restart
        }
        catch (SQLException e){
            Toast.makeText(this, "Could not change connection state", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setSensorTypeActive(int id, boolean active) {
        try{
            SensorDataDAO.getInstance(getApplicationContext()).setSensorTypeActive(id, active);
            sensorTypes.get(id).setActive(active);
        }
        catch (SQLException e){
            Toast.makeText(this, "Could not change sensor type state", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void goToFragmentWithoutBack(Fragment fragment) { // todo make everybody use this instead of there own calls to change fragment
        setFragmentWithoutBackStack(fragment);
    }

    /**
     * Get all sensor types from the database
     */
    public void getSensorTypesFromDB(){
        try{
            SensorTypeDbDTO sensorTypeDbDTO = sensorDataDAO.getSensorTypesdbDto();
            sensorTypeNames = sensorTypeDbDTO.getSensorTypeNames();
            sensorTypeMap = sensorTypeDbDTO.getSensorTypeMap();
            sensorTypes =  sensorTypeDbDTO.getSensorTypes();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Get all sensor type aliases from the database
     */
    public void getTypeAliasFromDB(){
        try{
            typeAliases = sensorDataDAO.getTypeAlias();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Delete sensor type by id
     * @param typeID of sensor type to be deleted
     */
    public void deleteSensorTypeByID(int typeID){
        CharSequence toastMessage;
        try{
            SensorDataDAO.getInstance(context).deleteSensorTypeByID(typeID);
            toastMessage = "Successfully deleted sensor type from database";
        }
        catch(SQLException e){
            toastMessage = "Failed to delete sensor type from database";
        }
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, toastMessage, duration);
        toast.show();
    }

    /**
     * Get all settings from database
     */
    private void getSettingsFromDB() {
            connections.put("mqtt", sensorDataDAO.getAllConnectionsOfType("mqtt"));
            connections.put("restapi",sensorDataDAO.getAllConnectionsOfType("restapi"));
            LocationsDbDTO locationsDbDTO = sensorDataDAO.getLocations();
            locationsWithCoords = locationsDbDTO.getLocationsWithCoords();
            locationsWithoutCoords = locationsDbDTO.getLocationsWithoutCoords();
            connectionGroups = sensorDataDAO.getConnectionGroups();
            getTopicAndDataStructureFromDB();
            getSensorTypesFromDB();
            getTypeAliasFromDB();
    }

    /**
     * Get all topic structure and data structures from database
     */
    private void getTopicAndDataStructureFromDB() {
        for(String type : sensorDataDAO.getConnectionGroupNames()) {
            ArrayList<SpinnerItemEntry<Integer, String>> topicStructure = sensorDataDAO.getTopicStructuresOfType(type);
            topicStructures.put(type, topicStructure);
            int i = 0;
            for (Iterator<SpinnerItemEntry<Integer, String>> iter = topicStructure.iterator(); iter.hasNext();) {
                topicMap.put(iter.next().getKey(), i++);
            }

            ArrayList<SpinnerItemEntry<Integer, String>> dataStructure = sensorDataDAO.getDataStructureOfType(type);
            dataStructures.put(type, dataStructure);
            i = 0;
            for (Iterator<SpinnerItemEntry<Integer, String>> iter = dataStructure.iterator(); iter.hasNext();) {
                dataMap.put(iter.next().getKey(), i++);
            }
        }
    }
}