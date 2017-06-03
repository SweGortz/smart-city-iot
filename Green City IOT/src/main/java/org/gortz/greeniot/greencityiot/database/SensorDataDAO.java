package org.gortz.greeniot.greencityiot.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TimeZone;

import org.gortz.greeniot.greencityiot.database.entity.ConnectionGroup;
import org.gortz.greeniot.greencityiot.database.entity.DataStructure;
import org.gortz.greeniot.greencityiot.database.entity.TopicStructure;
import org.gortz.greeniot.greencityiot.database.entity.TypeAlias;
import org.gortz.greeniot.greencityiot.database.entity.Connection;
import org.gortz.greeniot.greencityiot.dto.listitems.SpinnerItemEntry;
import org.gortz.greeniot.greencityiot.dto.locations.LocationsDbDTO;
import org.gortz.greeniot.greencityiot.dto.sensors.SensorTypeDbDTO;
import org.gortz.greeniot.greencityiot.dto.sensors.SensorTypeNode;
import org.gortz.greeniot.greencityiot.database.entity.Coordinate;
import org.gortz.greeniot.greencityiot.database.entity.Location;
import org.gortz.greeniot.greencityiot.dto.sensors.SensorValue;
import org.gortz.greeniot.greencityiot.dto.sensors.SensorNodeBaseMessage;
import org.gortz.greeniot.greencityiot.database.entity.SensorLimits;
import org.gortz.greeniot.greencityiot.database.entity.SensorType;

/**
 * Sensor data, Data Access Object.
 * The current connection between the application and the database
 */
public class SensorDataDAO {
    private static final String TAG = SensorDataDAO.class.getSimpleName() ;
    private SensorDataDbHelper dbSensor;
    private SQLiteDatabase db;
    private static SensorDataDAO sInstance;
    private final String MAX_MESSAGE_TO_SAVE = "10";

    public static synchronized SensorDataDAO getInstance(Context context){
        if(sInstance == null){
            sInstance = new SensorDataDAO(context.getApplicationContext());
        }
        return sInstance;
    }

    private SensorDataDAO(Context context) {
        dbSensor = new SensorDataDbHelper(context);
        db = dbSensor.getWritableDatabase();
    }

    /**
     * retrieves all sensor types from database
     * @return sensorTypes in map with connected database id as key
     */
    public HashMap<Integer,SensorType> getAllSensorTypes(){
        HashMap<Integer,SensorType> sensorTypes = new HashMap<>();
        SQLiteDatabase db = dbSensor.getReadableDatabase();
        Cursor typeCursor = db.query("type", new String[] { "id, name, unit, supported, active, low, med, high" }, null, null, null, null, "name ASC");

        if(typeCursor != null){
            typeCursor.moveToFirst();
            for(int i = 0; i < typeCursor.getCount(); i++){
                boolean active = typeCursor.getInt(4) != 0;
                if(active) {
                    sensorTypes.put(typeCursor.getInt(0), new SensorType(typeCursor.getInt(0), typeCursor.getString(1), typeCursor.getString(2), typeCursor.getInt(3) != 0, true, new SensorLimits(typeCursor.getDouble(5), typeCursor.getDouble(6), typeCursor.getDouble(7))));
                }
                typeCursor.moveToNext();
            }
            typeCursor.close();
        }
        return sensorTypes;
    }

    /**
     * Get sensor node id out of SensorNodeBaseMessage from database. Either by retriving it if it exist or creating it and then returning it.
     * @param sensorNodeBaseMessage to be created or get id from
     * @return id of created/retrieved sensor node
     * @throws NoSuchElementException if then sensorNodeBaseMessage contains no identification
     */
    public long getNodeID(SensorNodeBaseMessage sensorNodeBaseMessage) throws NoSuchElementException {
        if(sensorNodeBaseMessage.getId() == null){
            Log.e(TAG,"No identifier on message");
            throw new NoSuchElementException("no identification on node");
        }
        long sensorNodeId = -2;
        String[] columns ={"id"};
        Cursor nodeCursor = db.query("node", columns, "name=?", new String[] { sensorNodeBaseMessage.getId() }, null, null, null);
        nodeCursor.moveToFirst();
        if(nodeCursor.getCount() == 0){
            if(sensorNodeBaseMessage.getLocation() != null) {
                if (sensorNodeBaseMessage.getLocation().getPosition() != null) {
                    sensorNodeId = createNode(sensorNodeBaseMessage.getId(), sensorNodeBaseMessage.getLocation().getName(), sensorNodeBaseMessage.getLocation().getPosition().getLat(), sensorNodeBaseMessage.getLocation().getPosition().getLon(), sensorNodeBaseMessage.getOrganization());
                } else {
                    sensorNodeId = createNode(sensorNodeBaseMessage.getId(),sensorNodeBaseMessage.getLocation().getName(),sensorNodeBaseMessage.getOrganization());
                }
            }
        }else {
            sensorNodeId = nodeCursor.getInt(0);
        }
        nodeCursor.close();
        return sensorNodeId;
    }

    /**
     * Get sensor type id of sensorTypeAlias from database or create sensorType with alias if it doesn't exists and return the type id.
     * @param sensorTypeAlias to get connected sensor type from
     * @return id of connected sensor type
     */
    private long getTypeID(String sensorTypeAlias){
        Cursor sensorTypesCursor = db.query("type,type_alias", new String[] { "type.id, type.supported" }, "type_alias.name=? AND type.id=type_alias.type_id", new String[] { sensorTypeAlias }, null, null, null);
        if(sensorTypesCursor.moveToFirst()){
            if(sensorTypesCursor.getInt(1) != 0) {
                int typeId = sensorTypesCursor.getInt(0);
                sensorTypesCursor.close();
                return typeId;
            }else {
                Log.i(TAG,"Message arrived with the unsupported type alias " + sensorTypeAlias);
                sensorTypesCursor.close();
                return -1L;
            }
        }else {
            long sensorTypeID = createSensorTypeAndAlias(new SensorType(sensorTypeAlias,"",false,false,new SensorLimits(0,0,0)));
            sensorTypesCursor.close();
            return sensorTypeID;
        }

    }

    /**
     * Get location id of location from database, create location and return id if it doesn't exists
     * @param location to get id from
     * @param coordinateLat to set if the location doesn't exist
     * @param coordinateLong to set if the location doesn't exist
     * @return location id
     */
    private long getLocationID(String location, double coordinateLat, double coordinateLong){
        Cursor locationCursor = db.query("Location", new String[]{"id"}, "name=?", new String[]{location}, null, null, null);
        if (locationCursor.moveToFirst()) {
            int locationID = locationCursor.getInt(0);
            locationCursor.close();
            return  locationID;
        } else {
            ContentValues valuesForLocation = new ContentValues();
            if(coordinateLat != 0.0 && coordinateLong != 0.0) {
                valuesForLocation.put("name", location);
                valuesForLocation.put("lat", coordinateLat);
                valuesForLocation.put("lon", coordinateLong);
                return db.insert(SensorDataDb.LocationEntry.TABLE_NAME, null, valuesForLocation);
            }else{
                valuesForLocation.put("name", location);
                locationCursor.close();
                return db.insert(SensorDataDb.LocationEntry.TABLE_NAME, null, valuesForLocation);
            }
        }
    }

    /**
     * Get id of organization by name from database, create it and retrun the id if it doesn't exist.
     * @param organization name
     * @return id of organization
     */
    private long getOrganizationID(String organization){
        Cursor organizationCursor = db.query("organization", new String[] { "id" }, "name=?", new String[] { organization }, null, null, null);
        if(organizationCursor.moveToFirst()){
            return organizationCursor.getInt(0);
        }else {
            ContentValues valuesForOrganization = new ContentValues();
            valuesForOrganization.put("name", organization);
            organizationCursor.close();
            return db.insert(SensorDataDb.OrganizationEntry.TABLE_NAME, null, valuesForOrganization);
        }
    }

    /**
     * Retrieve all active sensor types from database
     * @return list of sensor type that are active
     */
    private List<Integer> getActiveSensorTypeIDs(){
        ArrayList<Integer> sensorTypeIDs = new ArrayList<>();
        Cursor typeCursor = db.query("type", new String[] { "id"}, "active != 0", null, null, null, null);
        if(typeCursor != null){
            typeCursor.moveToFirst();
            for(int i = 0; i < typeCursor.getCount(); i++){
                sensorTypeIDs.add(typeCursor.getInt(0));
                typeCursor.moveToNext();
            }
            typeCursor.close();
        }
        return sensorTypeIDs;
    }

    /**
     * Create a sensor value in database
     * @param node that the sensor value is coming from
     * @param sensorTypeAlias the sensor type alias the message had
     * @param sensorValue to save
     * @return id of the created sensor value
     */
    public long createSensorValueMessage(long node, String sensorTypeAlias, SensorValue sensorValue) {
        ContentValues values = new ContentValues();
        values.put("node_id",node);
        values.put("value", sensorValue.getValue());
        if(sensorValue.getTimestamp() != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                String date = sdf.format(sensorValue.getTimestamp().toString());
                values.put("lastRetrievalTimestamp", date);
            }catch (Exception e){
                Log.i(TAG,"Couldn't convert messages lastRetrievalTimestamp to date format ");
            }
        }

        Cursor sensorTypesCursor = db.query("type,type_alias", new String[] { "type.id, type.supported" }, "type_alias.name=? AND type.id=type_alias.type_id", new String[] { sensorTypeAlias }, null, null, null);
        if(sensorTypesCursor.moveToFirst()){
            if(sensorTypesCursor.getInt(1) != 0) { //Don't save sensor data on sensor types not yet supported
                values.put("type_id", sensorTypesCursor.getInt(0));
            }else {
                Log.i(TAG,"Message arrived with the unsupported type alias " + sensorTypeAlias);
                return -1L;
            }
        }else {
            createSensorTypeAndAlias(new SensorType(sensorTypeAlias,"",false,false,new SensorLimits(0,0,0)));
            return -1L;
        }
        sensorTypesCursor.close();

        long typeId = getTypeID(sensorTypeAlias);
        if(typeId == -1L)return -1L;
        values.put("type_id",typeId);
        long newRowId = db.insert(SensorDataDb.MessageEntry.TABLE_NAME, null,values);
        Log.i(TAG, "Added new row: " + newRowId + " " + sensorTypeAlias + " with the value " + sensorValue.getValue());
        return newRowId;
    }

    /**
     * Create a sensor node in database
     * @param nodeId of node
     * @param location the node is located at
     * @param organization owning the node
     * @return id of the created sensor node
     */
    private long createNode(String nodeId, String location, String organization){
        return createNode(nodeId,location,0.0,0.0,organization);
    }

    /**
     * Create a sensor node in database
     * @param nodeId of node
     * @param location the node is located at
     * @param coordinateLat to the location
     * @param coordinateLong to the location
     * @param organization owning the node
     * @return id of the created sensor node
     */
    private long createNode(String nodeId, String location, double coordinateLat, double coordinateLong, String organization){
        ContentValues valuesForNode = new ContentValues();
        valuesForNode.put("name", nodeId);
        valuesForNode.put("location_id",getLocationID(location,coordinateLat,coordinateLong));
        valuesForNode.put("organization_id", getOrganizationID(organization));
        return db.insert(SensorDataDb.NodeEntry.TABLE_NAME,null,valuesForNode);
    }

    /**
     * Create sensor type and connected sensor type alias in database
     * @param type to create
     * @return id of the sensor type
     */
    public long createSensorTypeAndAlias(SensorType type){
        Cursor sensorTypeCursor = db.query("type_alias", new String[] { "id" }, "name=?", new String[]{type.getName()}, null, null, null);
        if(sensorTypeCursor == null){
            Log.i(TAG,"Sensor type exists [" + type.getName() +"]");
            return sensorTypeCursor.getInt(0);
        }
        type.setActive(false);
        type.setSupported(false);
        long sensorTypeId = getSensorTypeIDBySensorType(type);
        createSensorTypeAlisa(type,sensorTypeId);
        return sensorTypeId;
    }

    /**
     * Create sensor type in databse
     * @param type to create
     * @return id of sensor type
     */
    private long createSensorType(SensorType type){
        ContentValues values = new ContentValues();
        values.put(SensorDataDb.TypeEntry.COLUMN_NAME_NAME, type.getName());
        values.put(SensorDataDb.TypeEntry.COLUMN_NAME_UNIT, type.getUnit());
        values.put(SensorDataDb.TypeEntry.COLUMN_NAME_ACTIVE, (type.isActive()? 1:0));
        values.put(SensorDataDb.TypeEntry.COLUMN_NAME_SUPPORTED, (type.isSupported()? 1:0));
        if(type.getLimits() != null) {
            values.put(SensorDataDb.TypeEntry.COLUMN_NAME_LOW, type.getLimits().getLow());
            values.put(SensorDataDb.TypeEntry.COLUMN_NAME_MED, type.getLimits().getMedium());
            values.put(SensorDataDb.TypeEntry.COLUMN_NAME_HIGH, type.getLimits().getHigh());
        }else{
            values.put(SensorDataDb.TypeEntry.COLUMN_NAME_LOW, 0);
            values.put(SensorDataDb.TypeEntry.COLUMN_NAME_MED, 0);
            values.put(SensorDataDb.TypeEntry.COLUMN_NAME_HIGH, 0);
        }
        return db.insert(SensorDataDb.TypeEntry.TABLE_NAME, null, values);
    }

    /**
     * Create sensor type alias in database
     * @param sensorTypeAlias to create
     * @param sensorTypeID it should be connected to
     * @return id of created sensor type alias
     */
    private long createSensorTypeAlisa(SensorType sensorTypeAlias, long sensorTypeID){
        ContentValues values = new ContentValues();
        values.put("name", sensorTypeAlias.getName());
        values.put("type_id", sensorTypeID);
        Log.i(TAG,"Created a new sensor type and alias with the name " +sensorTypeAlias.getName());
        return db.insert(SensorDataDb.TypeAliasEntry.TABLE_NAME, null, values);
    }

    /**
     * Create connection in database
     * @param connection to create
     * @return id of connection
     */
    public long createConnection(Connection connection){
        ContentValues values = new ContentValues();
        values.put("connection_type", connection.getConnectionType());
        values.put("url", connection.getUrl());
        values.put("port", connection.getPort());
        values.put("arg0", connection.getArg0());
        values.put("username", connection.getUsername());
        values.put("password", connection.getPassword());
        values.put("active", 1);
        values.put("topic_structure_id", connection.getTopicStructure().getId());
        values.put("data_structure_id", connection.getDataStructure().getId());
        int id = (int) db.insert(SensorDataDb.ConnectionEntry.TABLE_NAME, null, values);
        connection.setId(id);
        return id;
    }

    /**
     * Get sensor Data of type by the type id from database
     * @param typeID to get data from
     * @return List of sensorTypeNodes
     */
    private ArrayList<SensorTypeNode> getSensorDataOfTypeByTypeID(int typeID){
        ArrayList<SensorTypeNode> sensorDataList = new ArrayList<>();
        Cursor sensorTypeValueCursor = db.query(true, "Location, organization, node, messages", new String[] { "node.name, organization.name, lat, lon, Location.name" },
                "node.location_id = Location.id AND node.organization_id = organization.id AND node.id = messages.node_id AND messages.type_id = ?",
                new String[] { String.valueOf(typeID)}, null, null, null, null);

        sensorTypeValueCursor.moveToFirst();
        while(!sensorTypeValueCursor.isAfterLast()){
            SensorTypeNode node = new SensorTypeNode(sensorTypeValueCursor.getString(0), sensorTypeValueCursor.getString(1), new Location(sensorTypeValueCursor.getString(4), new Coordinate(sensorTypeValueCursor.getDouble(2), sensorTypeValueCursor.getDouble(3))));
            SensorTypeNode sensorTypeNode = getAllSensorDataOfTypeFromNode(node,typeID);
            if(!sensorTypeNode.getValueList().isEmpty()){
                sensorDataList.add(sensorTypeNode);
            }

            sensorTypeValueCursor.moveToNext();
        }
        sensorTypeValueCursor.close();
        return  sensorDataList;
    }

    /**
     * Get all sensor data of type in a node from database
     * @param node to get data from
     * @param typeID to type to be retrieved
     * @return sensorTypeNode with sensor values
     */
    private SensorTypeNode getAllSensorDataOfTypeFromNode(SensorTypeNode node, int typeID){
        Cursor sensorValuesForTypeCursor = db.query("node, messages", new String[] { "messages.value, messages.lastRetrievalTimestamp" }, "node.id = messages.node_id AND messages.type_id = ? AND node.name = ?", new String[] {String.valueOf(typeID), node.getName()}, null, null, "messages.id DESC", MAX_MESSAGE_TO_SAVE);
        if(sensorValuesForTypeCursor != null) {
            sensorValuesForTypeCursor.moveToFirst();
            do {
                Calendar mCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                try {
                    mCalendar.setTime(dateFormat.parse(sensorValuesForTypeCursor.getString(1)));
                    mCalendar.add(Calendar.HOUR, 2); //TODO should not have to do this. Something is wrong in conversion
                    node.addValue(mCalendar, sensorValuesForTypeCursor.getDouble(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } while (sensorValuesForTypeCursor.moveToNext());
            sensorValuesForTypeCursor.close();
        }
        return node;
    }

    /**
     * Get all sensor data from database
     * @param lastValue that has already been retrieved
     * @return map of SensorTypeNodes with the sensorType as key
     */
    public HashMap<String, ArrayList<SensorTypeNode>> getAllSensorData(Calendar lastValue){
        HashMap<String, ArrayList<SensorTypeNode>> newSensorData = new HashMap<>();

        List<Integer> activeSensorType = getActiveSensorTypeIDs();
        if(activeSensorType.size()> 0){
            for(Integer sensorTypeId : activeSensorType){
                newSensorData.put(getSensorNameByID(sensorTypeId), getSensorDataOfTypeByTypeID(sensorTypeId));
            }
        }
        Log.i(TAG, "retrieved sensor values from db");
        return newSensorData;
    }

    /**
     * Get sensor name by its id
     * @param sensorID of sensor
     * @return sensor name
     */
    private String getSensorNameByID(int sensorID){
        String name = "No Sensor found";
        Cursor typeCursor = db.query("type", new String[] { "name"}, "id=?", new String[]{String.valueOf(sensorID)}, null, null, null);
        if(typeCursor != null) {
            if (typeCursor.moveToFirst()) {
                name = typeCursor.getString(0);
            }
            typeCursor.close();
        }
        return name;
    }

    /**
     * Get all active connection of type
     * @param type to get connection from
     * @return collection of connections
     */
    public Collection<Connection> getAllActiveConnectionOfType(String type) {
        ArrayList<Connection> connections = new ArrayList<>();
        Cursor connectionCursor = db.query("connection, topic_structure, data_structure", new String[] { "url, port, username, password, topic_structure.name, data_structure.name" }, "connection_type=? AND active=1 AND data_structure_id=data_structure.id AND connection.topic_structure_id = topic_structure.id", new String[] {type}, null, null, null);
        if(connectionCursor != null){
            connectionCursor.moveToFirst();
            for(int i = 0; i < connectionCursor.getCount(); i++){
                connections.add(new Connection(type,connectionCursor.getString(0),connectionCursor.getInt(1),null,connectionCursor.getString(2), connectionCursor.getString(3),true,new TopicStructure(connectionCursor.getString(4)),new DataStructure(connectionCursor.getString(5))));
                connectionCursor.moveToNext();
            }
            connectionCursor.close();
        }
        return connections;
    }

    /**
     * Get all connections of type
     * @param type to get connection from
     * @return map of connection with connection id as key
     */
    public HashMap<Integer,Connection> getAllConnectionsOfType(String type){
        HashMap<Integer,Connection> connections = new HashMap<>();
        Cursor connectionCursor = db.query("connection", new String[] { "id, connection_type, url, port, arg0, username, password, active, topic_structure_id, data_structure_id" }, "connection_type=?", new String[] {type}, null, null, "id ASC");

        if(connectionCursor != null){
            connectionCursor.moveToFirst();
            for(int i = 0; i < connectionCursor.getCount(); i++){
                connections.put(connectionCursor.getInt(0),new Connection(connectionCursor.getInt(0), connectionCursor.getString(1), connectionCursor.getString(2), connectionCursor.getInt(3), connectionCursor.getString(4), connectionCursor.getString(5), connectionCursor.getString(6), (connectionCursor.getInt(7) > 0),  new TopicStructure(connectionCursor.getInt(8)) , new DataStructure(connectionCursor.getInt(9)))); //TODO might need to check for null since topic_structure_id is allowed to be null
                connectionCursor.moveToNext();
            }
            connectionCursor.close();
        }
        return connections;
    }

    /**
     * Get all data structures from database
     * @return map of data structure names with data structure id as key
     */
    public HashMap<Integer,String> getAllDataStructure() {
        HashMap<Integer, String> dataStructures = new HashMap<>();
        Cursor dataStructure = db.query("data_structure", new String[] { "id, name" }, null, null, null, null, null);
        dataStructure.moveToFirst();
        while(dataStructure.moveToNext()) {
            dataStructures.put(dataStructure.getInt(0), dataStructure.getString(1));
        }
        dataStructure.close();
        return dataStructures;
    }

    /**
     * Get all topic names from database
     * @return map of topic structure names with topic structure id as key
     */
    public HashMap<Integer,String> getAllTopicName() {
        HashMap<Integer, String> dataStructures = new HashMap<>();
        Cursor topicStructure = db.query("topic_structure", new String[] { "id, name" }, null, null, null, null, null);
        topicStructure.moveToFirst();
        while(topicStructure.moveToNext()) {
            dataStructures.put(topicStructure.getInt(0), topicStructure.getString(1));
        }
        topicStructure.close();
        return dataStructures;
    }

    /**todo Jimmy explain  the need for this call
     * Get sensor type data from database
     * @return SensorTypeDbDTO
     */
    public SensorTypeDbDTO getSensorTypesdbDto() throws SQLException {
        SensorTypeDbDTO sensorTypedbDTO = new SensorTypeDbDTO();
        Cursor sensorTypeCursor = db.query(SensorDataDb.TypeEntry.TABLE_NAME, new String[] {SensorDataDb.TypeEntry.COLUMN_NAME_ID, SensorDataDb.TypeEntry.COLUMN_NAME_NAME, SensorDataDb.TypeEntry.COLUMN_NAME_UNIT, SensorDataDb.TypeEntry.COLUMN_NAME_ACTIVE, SensorDataDb.TypeEntry.COLUMN_NAME_SUPPORTED, SensorDataDb.TypeEntry.COLUMN_NAME_LOW, SensorDataDb.TypeEntry.COLUMN_NAME_MED, SensorDataDb.TypeEntry.COLUMN_NAME_HIGH}, null, null, null, null, "id ASC");
        if(sensorTypeCursor != null){
            sensorTypeCursor.moveToFirst();
            for(int i = 0; i < sensorTypeCursor.getCount(); i++){
                sensorTypedbDTO.getSensorTypes().put(sensorTypeCursor.getInt(0),new SensorType(sensorTypeCursor.getInt(0), sensorTypeCursor.getString(1), sensorTypeCursor.getString(2), (sensorTypeCursor.getInt(3) != 0), (sensorTypeCursor.getInt(4) != 0), new SensorLimits(sensorTypeCursor.getDouble(5), sensorTypeCursor.getDouble(6), sensorTypeCursor.getDouble(7))));
                sensorTypedbDTO.getSensorTypeNames().add(i, new SpinnerItemEntry<Integer, String>(sensorTypeCursor.getInt(0), sensorTypeCursor.getString(1)));
                sensorTypedbDTO.getSensorTypeMap().put(sensorTypeCursor.getInt(0), i);
                sensorTypeCursor.moveToNext();
            }
            sensorTypeCursor.close();
        }
        return sensorTypedbDTO;
    }

    /**
     * Get all type alias
     * @return map of type aliases with the type alias id as key
     * @throws SQLException
     */
    public HashMap<Integer,TypeAlias> getTypeAlias() throws SQLException{
        HashMap<Integer,TypeAlias> typeAliases = new HashMap<>();
        Cursor typeAliasCursor = db.query(SensorDataDb.TypeAliasEntry.TABLE_NAME, new String[] {SensorDataDb.TypeAliasEntry.COLUMN_NAME_ID, SensorDataDb.TypeAliasEntry.COLUMN_NAME_NAME, SensorDataDb.TypeAliasEntry.COLUMN_NAME_TYPE_ID}, null, null, null, null, "id ASC");
        if(typeAliasCursor != null){
            typeAliasCursor.moveToFirst();
            for(int i = 0; i < typeAliasCursor.getCount(); i++){
                typeAliases.put(typeAliasCursor.getInt(0),new TypeAlias(typeAliasCursor.getInt(0), typeAliasCursor.getString(1), typeAliasCursor.getInt(2)));
                typeAliasCursor.moveToNext();
            }
            typeAliasCursor.close();
        }
        return typeAliases;
    }

    /**
     *Get all active connection
     * @return list of active connection
     */
    public List<Connection> getActiveConnections(){
        ArrayList<Connection> connections = new ArrayList<>();
        Cursor connectionCursor = db.query("connection LEFT OUTER JOIN topic_structure ON topic_structure_id=topic_structure.id, data_structure ", new String[] { "connection.id, connection_type, url, port, arg0, username, password, topic_structure.regex, location_regex_id, organization_regex_id, node_regex_id, data_structure.name" }, "data_structure_id=data_structure.id AND active != 0", null, null, null, null);
        if(connectionCursor != null){
            connectionCursor.moveToFirst();

            for(int i = 0; i < connectionCursor.getCount(); i++) {
                connections.add(new Connection(connectionCursor.getString(1),connectionCursor.getString(2),connectionCursor.getInt(3),connectionCursor.getString(4),connectionCursor.getString(5), connectionCursor.getString(6),true,new TopicStructure("",connectionCursor.getString(7),connectionCursor.getInt(8),connectionCursor.getInt(10),connectionCursor.getInt(9),""),new DataStructure(connectionCursor.getString(11))));
                connectionCursor.moveToNext();
            }
        }
        return connections;
    }

    /**
     * Get all locations
     * @return  LocationsDbDTO with two list, one for location with connection and another with  connection without
     */
    public LocationsDbDTO getLocations(){
        LocationsDbDTO locationsDbDTO = new LocationsDbDTO();
        Cursor locationCursor = db.query("Location", new String[] { "id, name, lat, lon" }, null, null, null, null, "id ASC");
        if(locationCursor != null){
            locationCursor.moveToFirst();
            for(int i = 0; i < locationCursor.getCount(); i++){
                if(locationCursor.getString(2) != null){
                    locationsDbDTO.getLocationsWithCoords().put(locationCursor.getInt(0),new Location(locationCursor.getInt(0), locationCursor.getString(1), new Coordinate(locationCursor.getDouble(2), locationCursor.getDouble(3))));
                }
                else{
                    locationsDbDTO.getLocationsWithoutCoords().put(locationCursor.getInt(0),new Location(locationCursor.getInt(0), locationCursor.getString(1), null));
                }
                locationCursor.moveToNext();
            }
            locationCursor.close();
        }
        return locationsDbDTO;
    }

    /**
     * Get all connection groups
     * @return map of connection groups with the connection group id as key
     */
    public HashMap<String, ConnectionGroup> getConnectionGroups(){
        HashMap<String,ConnectionGroup> connectionGroups = new HashMap<>();
        Cursor connectionGroupCursor = db.query(SensorDataDb.ConnectionGroupEntry.TABLE_NAME, new String[]{"id, name"}, null, null, null, null, null);
        if(connectionGroupCursor != null){
            connectionGroupCursor.moveToFirst();
            for(int i = 0; i < connectionGroupCursor.getCount(); i++){
                connectionGroups.put(connectionGroupCursor.getString(1), new ConnectionGroup(connectionGroupCursor.getInt(0), connectionGroupCursor.getString(1)));
                connectionGroupCursor.moveToNext();
            }
            connectionGroupCursor.close();
        }
        return connectionGroups;
    }

    /**
     * Get connection group names
     * @return list of connection group names
     */
    public ArrayList<String> getConnectionGroupNames(){ //todo!!!!
        ArrayList<String> dataStructureTypes = new ArrayList<>();
        dataStructureTypes.add("api");
        dataStructureTypes.add("broker");
        return dataStructureTypes;
    }

    /**
     * Get all data structures of type
     * @param type  to get data structure from
     * @return List of data structure name and id
     */
    public ArrayList<SpinnerItemEntry<Integer, String>> getDataStructureOfType(String type){
        ArrayList<SpinnerItemEntry<Integer, String>> dataStructures = new ArrayList<SpinnerItemEntry<Integer, String>>();
        Cursor dataStructureCursor = db.query("data_structure", new String[] {"id, name"}, "connection_group_id=?", new String[]{String.valueOf(getConnectionGroupsID(type))}, null, null, "id ASC");
        if(dataStructureCursor != null){
            dataStructureCursor.moveToFirst();
            for(int i = 0; i < dataStructureCursor.getCount(); i++){
                dataStructures.add(i, new SpinnerItemEntry<Integer, String>(dataStructureCursor.getInt(0), dataStructureCursor.getString(1)));
                dataStructureCursor.moveToNext();
            }
            dataStructureCursor.close();
        }
        return dataStructures;
    }

    /**
     * Get topic structures of type
     * @param type to get data structure from
     * @return list of topic strucure name and id
     */
    public ArrayList<SpinnerItemEntry<Integer, String>> getTopicStructuresOfType(String type){
        ArrayList<SpinnerItemEntry<Integer, String>> topicStructures = new ArrayList<SpinnerItemEntry<Integer, String>>();
        Cursor topicStructureCursor = db.query("topic_structure", new String[] {"id, name"}, "connection_group_id=?", new String[]{String.valueOf(getConnectionGroupsID(type))}, null, null, "id ASC");
        if(topicStructureCursor != null){
            topicStructureCursor.moveToFirst();
            for(int i = 0; i < topicStructureCursor.getCount(); i++){
                topicStructures.add(i, new SpinnerItemEntry<Integer, String>(topicStructureCursor.getInt(0), topicStructureCursor.getString(1)));
                topicStructureCursor.moveToNext();
            }
            topicStructureCursor.close();
        }
        return topicStructures;
    }

    /**
     * Get connection group id of name
     * @param connectionGroupName to get id to
     * @return id of connection group
     */
    private int getConnectionGroupsID(String connectionGroupName) {
        int groupID = -1;
        Cursor connectionGroupCursor = db.query(SensorDataDb.ConnectionGroupEntry.TABLE_NAME, new String[]{"id"}, "name=?", new String[]{connectionGroupName}, null, null, null);
        if(connectionGroupCursor != null){
            connectionGroupCursor.moveToFirst();
            groupID = connectionGroupCursor.getInt(0);
            connectionGroupCursor.close();
        }
        return groupID;
    }


    /**
     * Update connection in database
     * @param connection to update
     */
    public void updateConnection(Connection connection) {
        ContentValues values = new ContentValues();
        values.put("url", connection.getUrl());
        values.put("port", connection.getPort());
        values.put("arg0", connection.getArg0());
        values.put("username", connection.getUsername());
        values.put("password", connection.getPassword());
        values.put("topic_structure_id", connection.getTopicStructure().getId());
        values.put("data_structure_id", connection.getDataStructure().getId());
        db.update(SensorDataDb.ConnectionEntry.TABLE_NAME, values, "id=?", new String[]{String.valueOf(connection.getId())});
    }

    /**
     * Update location in database
     * @param location to update
     */
    public void updateLocation(Location location){
        ContentValues locationValues = new ContentValues();
        locationValues.put("lat", location.getPosition().getLat());
        locationValues.put("lon", location.getPosition().getLon());
        db.update("Location", locationValues, "id = ?", new String[] {String.valueOf(location.getId())});
    }

    /**
     * Update sensor type
     * @param sensorType to update
     */
    public void updateSensorType(SensorType sensorType){
        ContentValues sensorTypeValues = new ContentValues();
        sensorTypeValues.put(SensorDataDb.TypeEntry.COLUMN_NAME_NAME, sensorType.getName());
        sensorTypeValues.put(SensorDataDb.TypeEntry.COLUMN_NAME_UNIT, sensorType.getUnit());
        sensorTypeValues.put(SensorDataDb.TypeEntry.COLUMN_NAME_LOW, sensorType.getLimits().getLow());
        sensorTypeValues.put(SensorDataDb.TypeEntry.COLUMN_NAME_MED, sensorType.getLimits().getMedium());
        sensorTypeValues.put(SensorDataDb.TypeEntry.COLUMN_NAME_HIGH, sensorType.getLimits().getHigh());
        sensorTypeValues.put(SensorDataDb.TypeEntry.COLUMN_NAME_SUPPORTED, sensorType.isSupported());
        sensorTypeValues.put(SensorDataDb.TypeEntry.COLUMN_NAME_ACTIVE, sensorType.isActive());
        db.update(SensorDataDb.TypeEntry.TABLE_NAME, sensorTypeValues, "id=" + sensorType.getId(), null);
    }

    /**
     * Update type alias
     * @param previousTypeID connected to the alias
     * @param typeAliasID to update connection to
     * @param newSensorTypeID to be set
     * @throws SQLException
     */
    public void updateTypeAlias(int previousTypeID, int typeAliasID, int newSensorTypeID) throws SQLException{
        ContentValues typeAliasValues = new ContentValues();
        typeAliasValues.put(SensorDataDb.TypeAliasEntry.COLUMN_NAME_TYPE_ID, newSensorTypeID);

        db.update(SensorDataDb.TypeAliasEntry.TABLE_NAME, typeAliasValues, "id=" + typeAliasID, null);

        Cursor aliasCursor = db.query(SensorDataDb.TypeAliasEntry.TABLE_NAME, new String[]{"count(id)"}, "type_id=?", new String[]{String.valueOf(previousTypeID)}, null, null, null );
        if(aliasCursor != null){
            if(aliasCursor.moveToFirst()){
                if(aliasCursor.getInt(0) == 0){
                    int rows_deleted = db.delete(SensorDataDb.MessageEntry.TABLE_NAME, "type_id="+previousTypeID,null);
                    int rows_deleted2 = db.delete(SensorDataDb.TypeEntry.TABLE_NAME, "id=" + previousTypeID, null);
                    if(rows_deleted2 == 1){
                        Log.i(TAG, "removed sensor type with id: " + previousTypeID + " and " + rows_deleted + " values");
                    }
                    else{
                        Log.e(TAG, "Could not remove sensor type with id: " + previousTypeID);
                    }
                }
            }
        }
        aliasCursor.close();
    }


    /**
     * Recreate sensor type to alias
     * @param typeAliasID to alias
     * @param sensorType to be connected to alias
     */
    public void releaseAsAliasByID(int typeAliasID, SensorType sensorType){
        int typeID;
        Cursor sensorTypeCursor = db.query(SensorDataDb.TypeEntry.TABLE_NAME, new String[] {"id"}, "name=?", new String[] {sensorType.getName()}, null, null, null);
        if((sensorTypeCursor != null) && sensorTypeCursor.moveToFirst()){
            typeID = sensorTypeCursor.getInt(0);
        }
        else{
            ContentValues sensorTypeValues = new ContentValues();
            sensorTypeValues.put(SensorDataDb.TypeEntry.COLUMN_NAME_NAME, sensorType.getName());
            sensorTypeValues.put(SensorDataDb.TypeEntry.COLUMN_NAME_UNIT, sensorType.getUnit());
            sensorTypeValues.put(SensorDataDb.TypeEntry.COLUMN_NAME_ACTIVE, sensorType.isActive());
            sensorTypeValues.put(SensorDataDb.TypeEntry.COLUMN_NAME_SUPPORTED, sensorType.isSupported());
            sensorTypeValues.put(SensorDataDb.TypeEntry.COLUMN_NAME_LOW, sensorType.getLimits().getLow());
            sensorTypeValues.put(SensorDataDb.TypeEntry.COLUMN_NAME_MED, sensorType.getLimits().getMedium());
            sensorTypeValues.put(SensorDataDb.TypeEntry.COLUMN_NAME_HIGH, sensorType.getLimits().getHigh());
            typeID = (int) db.insert(SensorDataDb.TypeEntry.TABLE_NAME, null, sensorTypeValues);
        }
        sensorTypeCursor.close();

        int oldTypeID = getSensorTypeIDByTypeAliasID(typeAliasID);
        if(oldTypeID != -1){
            try {
                deleteSensorTypeByID(oldTypeID);
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }

        ContentValues typeAliasValues = new ContentValues();
        typeAliasValues.put(SensorDataDb.TypeAliasEntry.COLUMN_NAME_TYPE_ID, typeID);
        db.update(SensorDataDb.TypeAliasEntry.TABLE_NAME, typeAliasValues, "id=" + typeAliasID, null);

    }

    /**
     * Delete sensor type alias
     * @param typeAliasID to alias
     */
    public void deleteTypeAlias(int typeAliasID){
        db.delete(SensorDataDb.TypeAliasEntry.TABLE_NAME, "id=?", new String[]{String.valueOf(typeAliasID)});
    }

    /**
     * Delete sensor type by sensor type id
     * @param typeID to type
     */
    public void deleteSensorTypeByID(int typeID) throws SQLException{
        Cursor aliasCursor = db.query(SensorDataDb.TypeAliasEntry.TABLE_NAME, new String[]{"count(id)"}, "type_id=?", new String[]{String.valueOf(typeID)}, null, null, null );
        if(aliasCursor != null){
            if(aliasCursor.moveToFirst()){
                if(aliasCursor.getInt(0) == 0){
                    db.delete(SensorDataDb.MessageEntry.TABLE_NAME, "type_id=?",new String[]{String.valueOf(typeID)});
                    db.delete(SensorDataDb.TypeEntry.TABLE_NAME, "id=?", new String[]{String.valueOf(typeID)});
                }
            }
            aliasCursor.close();
        }
    }

    /**
     * Get sensor type id by Type alias id.
     * @param typeAliasID ID of alias.
     * @return ID of sensor type with specified alias.
     */
    public int getSensorTypeIDByTypeAliasID(int typeAliasID){
        int sensorTypeID = -1;
        Cursor typeAliasCursor = db.query(SensorDataDb.TypeAliasEntry.TABLE_NAME, new String[]{"type_id"}, "id=?", new String[]{String.valueOf(typeAliasID)}, null, null, null);
        if(typeAliasCursor != null){
            if(typeAliasCursor.moveToFirst()){
                sensorTypeID = typeAliasCursor.getInt(0);
            }
        }
        typeAliasCursor.close();
        return sensorTypeID;
    }

    /**
     * Get sensor type id by SensorType.
     * @param sensorType Sensor type.
     * @return ID of sensor type.
     */
    public long getSensorTypeIDBySensorType(SensorType sensorType){
        long sensorTypeID = -1L;
        Cursor sensorTypeCursor = db.query(SensorDataDb.TypeEntry.TABLE_NAME, new String[]{"id"}, "name=?", new String[]{sensorType.getName()}, null, null, null);
        if(sensorTypeCursor != null){
            if(sensorTypeCursor.moveToFirst()){
                sensorTypeID =  sensorTypeCursor.getInt(0);
            }
        }
        sensorTypeCursor.close();
        if(sensorTypeID == -1L){
            ContentValues typeEntryValues = new ContentValues();
            typeEntryValues.put("name", sensorType.getName());
            typeEntryValues.put("unit", "");
            typeEntryValues.put("active", sensorType.isActive());
            typeEntryValues.put("supported", sensorType.isSupported());
            typeEntryValues.put("low", sensorType.getLimits().getLow());
            typeEntryValues.put("med", sensorType.getLimits().getMedium());
            typeEntryValues.put("high", sensorType.getLimits().getHigh());
            sensorTypeID = db.insert(SensorDataDb.TypeEntry.TABLE_NAME, null, typeEntryValues);
        }
        return sensorTypeID;
    }

    /**
     * Set connection to active/inactive
     * @param id of connection
     * @param active to be set
     */
    public void setConnectionsActive(int id, boolean active) throws SQLException{
        ContentValues values = new ContentValues();
        values.put("active",  active ? 1 : 0);
        db.update(SensorDataDb.ConnectionEntry.TABLE_NAME, values, "id=?", new String[]{String.valueOf(id)});
    }

    /**
     * Set sensor type to active
     * @param id of sensor type
     * @param active to be set
     */
    public void setSensorTypeActive(int id, boolean active) throws SQLException{
        ContentValues values = new ContentValues();
        values.put("active",  active ? 1 : 0);
        db.update(SensorDataDb.TypeEntry.TABLE_NAME, values, "id=?", new String[]{String.valueOf(id)});
    }

    /**
     * Removes old values form the database
     */
    public void sensorDataCleanUp(){
        Cursor nodeIdCursor = db.query("node, organization,location", new String[]{"node.id, organization.name, location.name"}, "organization_id=organization.id AND location_id=location.id", null, null, null, null);
        if (nodeIdCursor.getCount() > 0) {
            nodeIdCursor.moveToFirst();
            String nodeName = nodeIdCursor.getString(0);
            for(int i = 0; i < nodeIdCursor.getCount(); i++) {
                Cursor nodeTypesCursor = db.query(true,"node, messages", new String[]{"messages.type_id"}, "node.id=node_id AND node.id=?", new String[]{nodeIdCursor.getString(0)}, null, null, null,null);
                nodeTypesCursor.moveToFirst();
                int rowsRemoved=0;
                for(int p = 0; p < nodeTypesCursor.getCount(); p++) {
                    Cursor theOldestMessageAllowedCursor = db.query("(SELECT lastRetrievalTimestamp from messages WHERE node_id=? AND type_id =? ORDER BY lastRetrievalTimestamp desc LIMIT "+MAX_MESSAGE_TO_SAVE+")"
                            , new String[]{"MIN(lastRetrievalTimestamp)"}, null,new String[]{nodeIdCursor.getString(0),nodeTypesCursor.getString(0)},null,null,null);
                    theOldestMessageAllowedCursor.moveToFirst();
                    rowsRemoved += db.delete("messages", "node_id=? AND type_id=? AND lastRetrievalTimestamp < ?",new String[]{nodeIdCursor.getString(0),nodeTypesCursor.getString(0),theOldestMessageAllowedCursor.getString(0)});
                    nodeTypesCursor.moveToNext();
                }
                Log.i(TAG, "Removed " + rowsRemoved + " old sensor values from organization " + nodeIdCursor.getString(1) + " with the location " + nodeIdCursor.getString(2) + " in database");
                nodeIdCursor.moveToNext();
            }
        }
    }
}
