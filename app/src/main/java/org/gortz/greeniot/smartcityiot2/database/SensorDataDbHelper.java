package org.gortz.greeniot.smartcityiot2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import org.gortz.greeniot.smartcityiot2.database.entity.DataStructure;
import org.gortz.greeniot.smartcityiot2.database.entity.TopicStructure;
import org.gortz.greeniot.smartcityiot2.database.entity.Connection;
import org.gortz.greeniot.smartcityiot2.database.entity.Coordinate;
import org.gortz.greeniot.smartcityiot2.database.entity.Location;
import org.gortz.greeniot.smartcityiot2.database.entity.SensorLimits;
import org.gortz.greeniot.smartcityiot2.database.entity.SensorType;

import static org.gortz.greeniot.smartcityiot2.database.SensorDataDb.ALL_SQL_DELETE_ENTRIES;

/**
 * Database helper class
 */
public class SensorDataDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "GreenIot.db";

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public SensorDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("dbHelper","create table");
        db.execSQL(SensorDataDb.OrganizationEntry.SQL_CREATE_ENTRIES);
        db.execSQL(SensorDataDb.LocationEntry.SQL_CREATE_ENTRIES);
        db.execSQL(SensorDataDb.ConnectionGroupEntry.SQL_CREATE_ENTRIES);
        db.execSQL(SensorDataDb.TopicStructureEntry.SQL_CREATE_ENTRIES);
        db.execSQL(SensorDataDb.DataStructureEntry.SQL_CREATE_ENTRIES);
        db.execSQL(SensorDataDb.TypeEntry.SQL_CREATE_ENTRIES);
        db.execSQL(SensorDataDb.NodeEntry.SQL_CREATE_ENTRIES);
        db.execSQL(SensorDataDb.TypeAliasEntry.SQL_CREATE_ENTRIES);
        db.execSQL(SensorDataDb.MessageEntry.SQL_CREATE_ENTRIES);
        db.execSQL(SensorDataDb.ConnectionEntry.SQL_CREATE_ENTRIES);
        insertStaticConnectionGroups(db);
        insertStaticTopicStructures(db);
        insertStaticDataStructures(db);
        insertStaticSensorType(db);
        insertStaticConnections(db);
        insertStaticLocations(db);
    }

    /**
     * Takes care about database updates
     * @param db connection
     * @param oldVersion of the database
     * @param newVersion of the database
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(ALL_SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    /**
     * Takes care about database downgrade
     * @param db connection
     * @param oldVersion of the database
     * @param newVersion of the database
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Insert static sensor type that should be available from start
     * @param db to add data into
     */
    void insertStaticSensorType(SQLiteDatabase db){
        ArrayList<SensorType> typeList = new ArrayList<>();
        typeList.add(new SensorType("temperature", "°C", true, true, new SensorLimits(-15.0, 10.0, 30.0)));
        typeList.add(new SensorType("humidity", "%RH", true, true, new SensorLimits(20, 40, 65)));
        typeList.add(new SensorType("pressure", "hPa", true, true, new SensorLimits(700, 850, 1050)));
        typeList.add(new SensorType("co", "mg/m3", true, false, new SensorLimits(0, 1, 2))); //TODO find actual limits
        typeList.add(new SensorType("no2", "ug/m3", true, false, new SensorLimits(0, 1, 2))); //TODO find actual limits
        typeList.add(new SensorType("pm1", "ug/m3", true, true, new SensorLimits(0, 1, 2))); //TODO find actual limits
        typeList.add(new SensorType("pm2_5", "ug/m3", true,true, new SensorLimits(0, 1, 2))); //TODO find actual limits
        typeList.add(new SensorType("pm10", "ug/m3", true, true, new SensorLimits(0, 1, 2))); //TODO find actual limits

        ContentValues values;
        for(SensorType type : typeList){
            values = new ContentValues();
            values.put(SensorDataDb.TypeEntry.COLUMN_NAME_NAME, type.getName());
            values.put(SensorDataDb.TypeEntry.COLUMN_NAME_UNIT, type.getUnit());
            values.put(SensorDataDb.TypeEntry.COLUMN_NAME_ACTIVE, (type.isActive()? 1:0));
            values.put(SensorDataDb.TypeEntry.COLUMN_NAME_SUPPORTED, (type.isSupported()? 1:0));
            values.put(SensorDataDb.TypeEntry.COLUMN_NAME_LOW, type.getLimits().getLow());
            values.put(SensorDataDb.TypeEntry.COLUMN_NAME_MED, type.getLimits().getMedium());
            values.put(SensorDataDb.TypeEntry.COLUMN_NAME_HIGH, type.getLimits().getHigh());
            long id = db.insert(SensorDataDb.TypeEntry.TABLE_NAME, null, values);

            values = new ContentValues();
            values.put(SensorDataDb.TypeAliasEntry.COLUMN_NAME_NAME, type.getName());
            values.put(SensorDataDb.TypeAliasEntry.COLUMN_NAME_TYPE_ID, id);
            db.insert(SensorDataDb.TypeAliasEntry.TABLE_NAME, null, values);

        }
    }

    /**
     * Insert static connections that should be available from start
     * @param db to add data into
     */
    void insertStaticConnections(SQLiteDatabase db) {
        ArrayList<Connection> connectionList = new ArrayList<>();
        connectionList.add(new Connection("mqtt","mqtt.greeniot.it.uu.se",1883,"#", true, new TopicStructure("GreenIoT"),new DataStructure("senML")));

        for(Connection c : connectionList) {
            System.out.println(c.toString());
            ContentValues values = new ContentValues();
            values.put(SensorDataDb.ConnectionEntry.COLUMN_NAME_CONNECTION_TYPE, c.getConnectionType());
            values.put(SensorDataDb.ConnectionEntry.COLUMN_NAME_URL, c.getUrl());
            values.put(SensorDataDb.ConnectionEntry.COLUMN_NAME_PORT, c.getPort());
            values.put(SensorDataDb.ConnectionEntry.COLUMN_NAME_ARG0, c.getArg0());
            values.put(SensorDataDb.ConnectionEntry.COLUMN_NAME_USERNAME, c.getUsername());
            values.put(SensorDataDb.ConnectionEntry.COLUMN_NAME_PASSWORD, c.getPassword());
            values.put(SensorDataDb.ConnectionEntry.COLUMN_NAME_ACTIVE, (c.isActive())? 1:0);
            Cursor topicStructureCursor = db.query(SensorDataDb.TopicStructureEntry.TABLE_NAME, new String[] {"id"}, "name=?", new String[] {c.getTopicStructure().getName()},null, null, null);
            if(topicStructureCursor == null)break;
            topicStructureCursor.moveToFirst();
            values.put(SensorDataDb.ConnectionEntry.COLUMN_NAME_TOPIC_STRUCTURE_ID, topicStructureCursor.getInt(0));
            Cursor dataStructureCursor = db.query(SensorDataDb.DataStructureEntry.TABLE_NAME, new String[] {"id"}, "name=?", new String[] {c.getDataStructure().getName()},null, null, null);
            if(dataStructureCursor == null)break;
            dataStructureCursor.moveToFirst();
            values.put(SensorDataDb.ConnectionEntry.COLUMN_NAME_DATA_STRUCTURE_ID, dataStructureCursor.getInt(0));
            db.insert(SensorDataDb.ConnectionEntry.TABLE_NAME, null, values);
        }
    }

    /**
     * Insert static location that should be available from start
     * @param db to add data into
     */
    void insertStaticLocations(SQLiteDatabase db){
        ArrayList<Location> locationList = new ArrayList<>();
        locationList.add(new Location("kthlab", new Coordinate(59.405107, 17.949597)));
        locationList.add(new Location("kthlab2", new Coordinate(59.405390, 17.949519)));
        locationList.add(new Location("upwistests", new Coordinate(59.863065, 17.644057)));
        locationList.add(new Location("kungsgatan48", new Coordinate(59.856658, 17.647552)));
        locationList.add(new Location("kungsgatan67slb", new Coordinate(59.857183, 17.645970)));
        locationList.add(new Location("kungsgatan-center", new Coordinate(59.858150, 17.644628)));


        for (Location l : locationList){
            ContentValues values = new ContentValues();
            values.put(SensorDataDb.LocationEntry.COLUMN_NAME_NAME,l.getName());
            values.put(SensorDataDb.LocationEntry.COLUMN_NAME_LAT,l.getPosition().getLat());
            values.put(SensorDataDb.LocationEntry.COLUMN_NAME_LON,l.getPosition().getLon());
            db.insert(SensorDataDb.LocationEntry.TABLE_NAME, null,values);
        }
    }

    /**
     * Insert static topic structure that should be available from start
     * @param db to add data into
     */
    void insertStaticTopicStructures(SQLiteDatabase db){
        ArrayList<TopicStructure> topicStructures = new ArrayList<>();
        topicStructures.add(new TopicStructure("GreenIoT","\\/([a-zA-Z0-9-_]{1,30})\\/([a-zA-Z0-9-_]{1,30})\\/([a-zA-Z0-9-_]{1,30})\\/([a-zA-Z0-9-_]{1,30})",1,4,2,"broker"));
        topicStructures.add(new TopicStructure("Gortz","",0,0,0,"api"));


        for(TopicStructure  ts: topicStructures) {
            try {
                int connectionGroupID = -1;
                Cursor connectionGroupCursor = db.query(SensorDataDb.ConnectionGroupEntry.TABLE_NAME, new String[]{"id"}, "name=?", new String[]{ts.getGroupName()}, null, null, null);
                if (connectionGroupCursor != null) {
                    connectionGroupCursor.moveToFirst();
                    connectionGroupID = connectionGroupCursor.getInt(0);
                    if (connectionGroupID != -1) {
                        ContentValues values = new ContentValues();
                        values.put(SensorDataDb.TopicStructureEntry.COLUMN_NAME_NAME, ts.getName());
                        values.put(SensorDataDb.TopicStructureEntry.COLUMN_NAME_REGEX, ts.getRegex());
                        values.put(SensorDataDb.TopicStructureEntry.COLUMN_NAME_LOCATION_REGEX_ID, ts.getLocationRegexID());
                        values.put(SensorDataDb.TopicStructureEntry.COLUMN_NAME_ORGANIZATION_REGEX_ID, ts.getOrganizationRegexID());
                        values.put(SensorDataDb.TopicStructureEntry.COLUMN_NAME_NODE_REGEX_ID, ts.getNodeNameRegexID());
                        values.put(SensorDataDb.TopicStructureEntry.COLUMN_NAME_CONNECTION_GROUP_ID, connectionGroupID);
                        db.insert(SensorDataDb.TopicStructureEntry.TABLE_NAME, null, values);
                    }
                }
            } catch (Exception e) {
                System.out.println("Could not find ConnectionGroup");
            }
        }
    }

    /**
     * Insert static data structure that should be available from start
     * @param db to add data into
     */
    void insertStaticDataStructures(SQLiteDatabase db){
        ArrayList<DataStructure> dataStructures = new ArrayList<>();
        dataStructures.add(new DataStructure("senML","broker"));
        dataStructures.add(new DataStructure("Gortz","api"));

        for(DataStructure ds: dataStructures) {
            try {
                int connectionGroupID = -1;
                Cursor connectionGroupCursor = db.query(SensorDataDb.ConnectionGroupEntry.TABLE_NAME, new String[]{"id"}, "name=?", new String[]{ds.getGroupName()}, null, null, null);
                if (connectionGroupCursor != null) {
                    connectionGroupCursor.moveToFirst();
                    connectionGroupID = connectionGroupCursor.getInt(0);
                    if (connectionGroupID != -1) {
                        ContentValues values = new ContentValues();
                        values.put(SensorDataDb.DataStructureEntry.COLUMN_NAME_NAME, ds.getName());
                        values.put(SensorDataDb.DataStructureEntry.COLUMN_NAME_CONNECTION_GROUP_ID, connectionGroupID);
                        db.insert(SensorDataDb.DataStructureEntry.TABLE_NAME, null, values);
                    }
                }
            } catch (Exception e) {
                System.out.println("Could not find ConnectionGroup");
            }
        }
   }

    /**
     * Insert static connection groups that should be available from start
     * @param db to add data into
     */
   void insertStaticConnectionGroups(SQLiteDatabase db){
       ArrayList<String> connectionGroupNames = new ArrayList<>();
       connectionGroupNames.add("api");
       connectionGroupNames.add("broker");

       ContentValues connectionGroupValues;
       for(String gn: connectionGroupNames) {
           connectionGroupValues = new ContentValues();
           connectionGroupValues.put(SensorDataDb.ConnectionGroupEntry.COLUMN_NAME_NAME, gn);
           db.insert(SensorDataDb.ConnectionGroupEntry.TABLE_NAME, null, connectionGroupValues);

       }
   }
}
