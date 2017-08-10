package org.gortz.greeniot.smartcityiot2.activity;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.gortz.greeniot.smartcityiot2.fragments.settings.AboutFragment;
import org.gortz.greeniot.smartcityiot2.model.Util;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.gortz.greeniot.smartcityiot2.R;
import org.gortz.greeniot.smartcityiot2.activity.base.BaseActivity;
import org.gortz.greeniot.smartcityiot2.database.SensorDataDAO;
import org.gortz.greeniot.smartcityiot2.database.entity.Connection;
import org.gortz.greeniot.smartcityiot2.dto.sensors.SensorNodeBaseMessage;
import org.gortz.greeniot.smartcityiot2.fragments.sensors.MapFragment;
import org.gortz.greeniot.smartcityiot2.fragments.sensors.base.SensorOptionFragment;
import org.gortz.greeniot.smartcityiot2.model.SensorDataHandler;
import org.gortz.greeniot.smartcityiot2.dto.sensors.SensorNodeKey;
import org.gortz.greeniot.smartcityiot2.dto.sensors.SensorValue;
import org.gortz.greeniot.smartcityiot2.dto.sensors.UpdateSensorDTO;
import org.gortz.greeniot.smartcityiot2.database.entity.SensorType;
import org.gortz.greeniot.smartcityiot2.dto.sensors.SensorTypeNode;
import org.gortz.greeniot.smartcityiot2.fragments.CommunicationChannelToActivity;
import org.gortz.greeniot.smartcityiot2.fragments.sensors.SensorItemFragment;
import org.gortz.greeniot.smartcityiot2.fragments.sensors.SensorsFragment;
import org.gortz.greeniot.smartcityiot2.model.formats.api.BasicRestApi;
import org.gortz.greeniot.smartcityiot2.model.formats.datatype.DataStructureConverter;

/**
 * Activity for the sensor map and list visualisation
 */
public class SensorActivity extends BaseActivity implements CommunicationChannelToActivity {
    private final String TAG = SensorActivity.class.getSimpleName();
    private final Integer SECONDS_BETWEEN_SENSOR_DATA_UPDATE = 7;
    private Map<Integer,SensorType> sensorTypes = new HashMap<>();
    private Integer currentSensorTypeID = -1;
    private SensorDataDAO sensorDataDAO;
    private Calendar lastSensorDataUpdate = Calendar.getInstance();
    private SensorDataHandler sensorList = SensorDataHandler.getInstance();
    private Collection<Connection> apiConnections = new ArrayList<>();
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private int sensorTypeSelected = -1;
    private HashMap<Integer, Integer> optionsMenuItemsMap = new HashMap<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        sensorDataDAO = SensorDataDAO.getInstance(this);
        super.onCreate(savedInstanceState);
        super.startActivityFrame();
        sensorTypes = sensorDataDAO.getAllSensorTypes();
        lastSensorDataUpdate.setTime(new Date(0));

        if(!sensorTypes.isEmpty()){
            currentSensorTypeID = sensorTypes.keySet().iterator().next().intValue();
            startRetrievalThread();
        }
        switch (getIntent().getExtras().getString("start").toString()){
            case "map":
                if(!Util.selfPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION) && !Util.selfPermissionGranted(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                    } else {
                        Context context = getApplicationContext();
                        CharSequence text = "Missing permission for " + Manifest.permission.ACCESS_FINE_LOCATION + " or " + Manifest.permission.ACCESS_COARSE_LOCATION;
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
                else{
                    startSensorMapFragment();
                }
                break;
            case "list":
                startSensorListFragment();
                break;
        }
    }

    /**
     * Menu click listen handler
     * @param item that was selected
     * @return true if it could change page
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_map) {
            String currentFragmentName = getCurrentFragment().getClass().getSimpleName();
            setCurrentFragment(new MapFragment());
            setFragmentOnBackStack(getCurrentFragment(), currentFragmentName);
        } else if (id == R.id.nav_sensors) {
            String currentFragmentName = getCurrentFragment().getClass().getSimpleName();
            setCurrentFragment(new SensorsFragment());
            setFragmentOnBackStack(getCurrentFragment(), currentFragmentName);
        } else if (id == R.id.nav_settings) {
            changeActivity(SettingsActivity.class);
        }
        else if(id == R.id.nav_about){
            String currentFragmentName = getCurrentFragment().getClass().getSimpleName();
            setCurrentFragment(new AboutFragment());
            setFragmentOnBackStack(getCurrentFragment(), currentFragmentName);
        }

        closeMenuNav();
        return true;
    }

    /**
     * Handler for request permission result
     * @param requestCode to request
     * @param permissions to handle
     * @param grantResults of request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startSensorMapFragment();
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    startSensorMapFragment();
                }
                return;
            }
        }
    }

    /**
     * Start the sensor list visualisation
     */
    private void startSensorListFragment(){
        setCurrentFragment(new SensorsFragment());
        setFragmentWithoutBackStack(getCurrentFragment());
    }

    /**
     * Start the map visualisation
     */
    private void startSensorMapFragment(){
        setCurrentFragment(new MapFragment());
        setFragmentWithoutBackStack(getCurrentFragment());
    }

    /**
     * Start the retrieval thread that gathers new sensor values from api and database
     */
    private void startRetrievalThread(){
        new Thread() {
            @Override
            public void run() {
                try {
                    apiConnections = sensorDataDAO.getAllActiveConnectionOfType("restapi");
                    while(true) {
                        new DataRetrievalTask().execute(new UpdateSensorDTO(sensorList,getSensorTypeByID(currentSensorTypeID).getName(),lastSensorDataUpdate) );
                        lastSensorDataUpdate = Calendar.getInstance();
                        sleep(SECONDS_BETWEEN_SENSOR_DATA_UPDATE*1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MapFragment mapfragment = (MapFragment) getFragmentManager().findFragmentByTag(MapFragment.class.getSimpleName());
        SensorsFragment listFragment = (SensorsFragment) getFragmentManager().findFragmentByTag(SensorsFragment.class.getSimpleName());
        if(mapfragment != null && mapfragment.isVisible()){
            createMenu(menu);
        }else if(listFragment != null && listFragment.isVisible() && !getFromSensorCluster()){
            createMenu(menu);
        }
        else{
            hideDrawer();
        }
        return true;
    }


    /**
     * Create the menu with options
     * @param menu to add options into
     * @return the new menu
     */
    private Menu createMenu(Menu menu){
        int i = 0;
        for (SensorType s : sensorTypes.values()) {
            if(sensorTypeSelected == -1) sensorTypeSelected = s.getId();
            menu.add(0, (int) s.getId(), 0, s.getName());
            optionsMenuItemsMap.put(s.getId(), i);
            i++;
        }
        menu.setGroupCheckable(0,true,true);
        menu.getItem(optionsMenuItemsMap.get(sensorTypeSelected)).setChecked(true);
        return menu;
    }

    /**
     * Go to sensor detail of singe sensor node
     * @param sensor to show details about
     * @param sensorTypeID of sensor type to show
     * @return fragment that is being created and shown
     */
    @Override
    public SensorItemFragment goToSensorDetails(SensorTypeNode sensor, int sensorTypeID) {
        SensorItemFragment sensorDetailsFragment = new SensorItemFragment();
        Bundle args = new Bundle();
        args.putString("id", sensor.getName());
        args.putString("location", sensor.getLocation().getName());
        args.putString("organization", sensor.getOrganization());
        args.putInt("typeID", sensorTypeID);
        sensorDetailsFragment.setArguments(args);
        String currentFragmentName = getCurrentFragment().getClass().getSimpleName();
        setCurrentFragment(sensorDetailsFragment);
        setFragmentOnBackStack(sensorDetailsFragment, currentFragmentName);
        return sensorDetailsFragment;
    }

    /**
     * Go to sensor list
     * @param sensorTypeNodes to list
     * @param sensorTypeID of type to be shown
     * @return fragment that is being created and shown
     */
    @Override
    public SensorsFragment goToSensorsList(Collection<SensorTypeNode> sensorTypeNodes, int sensorTypeID) {
        SensorsFragment sensorsFragment  = new SensorsFragment();
        String[] nodeIDs = new String[sensorTypeNodes.size()];
        String[] organizationIDs = new String[sensorTypeNodes.size()];
        Bundle args = new Bundle();
        int pointer = 0;
        for(SensorTypeNode s : sensorTypeNodes){
            nodeIDs[pointer] = s.getName();
            organizationIDs[pointer] = s.getOrganization();
            pointer++;
        }

        args.putStringArray("sensorNames", nodeIDs);
        args.putStringArray("organizationNames", organizationIDs);
        args.putInt("typeID", sensorTypeID);
        sensorsFragment.setArguments(args);
        String currentFragmentName = getCurrentFragment().getClass().getSimpleName();
        setCurrentFragment(sensorsFragment);
        setFragmentOnBackStack(sensorsFragment, currentFragmentName);
        setFromSensorCluster(true);
        return sensorsFragment;
    }

    /**
     * Get sensor data of type
     * @param sensorTypeID of type to get data from
     * @return collection with sensorTypeNodes containing sensor data of type
     */
    @Override
    public Collection<SensorTypeNode> getSensorDataOfType(int sensorTypeID) {
        currentSensorTypeID = sensorTypeID;
        if(sensorTypes.get(sensorTypeID) == null){
            return new ArrayList<>();
        }

        return sensorList.getDataByType(sensorTypes.get(sensorTypeID).getName());
    }

    /**
     * Get sensor data of type without coordinates
     * @param sensorTypeID to type
     * @return collection of sensorTypeNodes with sensor data of type
     */
    @Override
    public Collection<SensorTypeNode> getSensorDataOfTypeWithCoordinate(int sensorTypeID) {
        currentSensorTypeID = sensorTypeID;
        if(sensorTypes.get(sensorTypeID) == null) return new ArrayList<>();
        ArrayList<SensorTypeNode> sensorNodesWithCoordinate = new ArrayList<>();
        for(SensorTypeNode sensorNode:sensorList.getDataByType(sensorTypes.get(sensorTypeID).getName())){
            if(!(sensorNode.getPosition().latitude == 0.0 && sensorNode.getPosition().longitude == 0.0)) {
                sensorNodesWithCoordinate.add(sensorNode);
            }
        }
        return sensorNodesWithCoordinate;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        sensorTypeSelected = id;
        item.setChecked(true);
        ((SensorOptionFragment) getCurrentFragment()).changeViewOption(id);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public SensorType getSensorTypeByID(int typeID) {
        return sensorTypes.get(typeID);
    }

    @Override
    public SensorTypeNode getSensorNodeOfTypeByID(SensorNodeKey nodeKey, int typeID) {
        Log.i(TAG, "Tried to retrieve sensor: " + nodeKey.getSensorName() + " with type id: " + typeID);
        return sensorList.getSensorTypeNodeByTypeAndName(sensorTypes.get(typeID).getName(),nodeKey);
    }

    /**
     * Get the current sensor type id of type
     * @return id of current type
     */
    public int getCurrentSensorTypeId() {
        return currentSensorTypeID;
    }

    /**
     *  set the current sensor type
     * @param id to be set
     */
    public void setCurrentSensorTypeId(int id){
        currentSensorTypeID = id;
    }

    @Override
    public void onBackPressed() {
        int index = getFragmentManager().getBackStackEntryCount() - 1;
        if(index != -1){
            String tag = getFragmentManager().getBackStackEntryAt(index).getName();
            Fragment fragment = getFragmentManager().findFragmentByTag(tag);
            setCurrentFragment(fragment);
        }
        super.onBackPressed();
    }

    /**
     * Task to retrieve  new sensor data from db and api
     */
    private class DataRetrievalTask extends AsyncTask<UpdateSensorDTO, Integer, UpdateSensorDTO> {

        @Override
        protected UpdateSensorDTO doInBackground(UpdateSensorDTO... params) {
            addTheNewSensorData(sensorDataDAO.getAllSensorData(params[0].getLastRetrievalTimestamp()),params[0].getSensorDataHandler());
            addTheNewSensorData(getSensorDataFromApi(params[0].getCurrentSensorTypeName(),params[0].getLastRetrievalTimestamp()),params[0].getSensorDataHandler());
            Log.i(TAG,"Updated sensor values");
            return params[0];
        }

        public HashMap<String, ArrayList<SensorTypeNode>> getSensorDataFromApi(String sensorTypeName, Calendar timestamp) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            HashMap<String, ArrayList<SensorTypeNode>> sensorData = new HashMap<>();

            for(Connection c : apiConnections) {
                try {
                   BasicRestApi restApi = (BasicRestApi) Class.forName("se.kth.greeniot.greencityiot.model.formats.api."+c.getTopicStructure().getName()).newInstance();
                    String restRequest = "http://"+c.getUrl() +":"+c.getPort()+ restApi.getLatestSensorValuesOfSensorTypeTopic(sensorTypeName,timestamp).getTopic();
                    System.out.println(restRequest);
                    String result = restTemplate.getForObject(restRequest, String.class);
                    DataStructureConverter sensorDataHandler = new DataStructureConverter(getApplicationContext());
                    SensorNodeBaseMessage s = sensorDataHandler.convertData(result,c.getDataStructure().getName(),null,c.getUrl(),null,sensorTypes.get(currentSensorTypeID).getName());
                    if(s.getData().isEmpty()){
                        continue;
                    }
                    //todo check if it's needed
                    if(!sensorData.containsKey(sensorTypes.get(currentSensorTypeID).getName()))sensorData.put(sensorTypes.get(currentSensorTypeID).getName(),new ArrayList<SensorTypeNode>());
                    sensorData.get(sensorTypes.get(currentSensorTypeID).getName()).add(new SensorTypeNode(s.getId(),c.getUrl(),s.getLocation()));

                    for(String sensorType : s.getData().keySet()){
                        if(!sensorData.containsKey(sensorTypes.get(currentSensorTypeID).getName()))sensorData.put(sensorTypes.get(currentSensorTypeID).getName(),new ArrayList<SensorTypeNode>());
                        for(SensorValue sv : s.getData().get(sensorType)){
                           SensorTypeNode stn = new SensorTypeNode("t","t2",null);
                           stn.addValue(sv.getTimestamp(),sv.getValue());
                           sensorData.get(sensorTypes.get(currentSensorTypeID).getName()).add(stn);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return sensorData;
        }

        /**
         * Add new sensor data to old sensor data
         * @param newSensorData to add
         * @param oldSensorData to extend with new sensor data
         */
        private void addTheNewSensorData(HashMap<String, ArrayList<SensorTypeNode>> newSensorData, SensorDataHandler oldSensorData){
            for(String sensorType : newSensorData.keySet()){
                for(SensorTypeNode nsd : newSensorData.get(sensorType)){
                        oldSensorData.update(sensorType,nsd);
                }
            }
        }
    }

}
