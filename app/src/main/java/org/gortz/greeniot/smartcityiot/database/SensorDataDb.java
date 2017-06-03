package org.gortz.greeniot.smartcityiot.database;

/**
 * Database structure definition
 */

import android.provider.BaseColumns;

 final class SensorDataDb {
    private SensorDataDb() {}

     static final String ALL_SQL_DELETE_ENTRIES = MessageEntry.SQL_DELETE_ENTRIES + "; " + TypeAliasEntry.SQL_DELETE_ENTRIES + "; " + ConnectionEntry.SQL_DELETE_ENTRIES + "; " + DataStructureEntry.SQL_DELETE_ENTRIES + "; " + TopicStructureEntry.SQL_DELETE_ENTRIES + "; " + ConnectionGroupEntry.SQL_DELETE_ENTRIES + "; " + NodeEntry.SQL_DELETE_ENTRIES + "; " + TypeEntry.SQL_DELETE_ENTRIES + "; " + OrganizationEntry.SQL_DELETE_ENTRIES + "; " + LocationEntry.SQL_DELETE_ENTRIES  + ";";

     /**
      * Message database entry
      */
     static class MessageEntry implements BaseColumns {
         static final String TABLE_NAME = "messages";
         static final String COLUMN_NAME_ID = "id";
         static final String COLUMN_NAME_NODE_ID = "node_id";
         static final String COLUMN_NAME_TIMESTAMP = "lastRetrievalTimestamp";
         static final String COLUMN_NAME_VALUE = "value";
         static final String COLUMN_NAME_TYPE_ID = "type_id";

         static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + MessageEntry.TABLE_NAME + " (" + COLUMN_NAME_ID
                        + " integer primary key autoincrement, " + COLUMN_NAME_NODE_ID
                        + " integer not null, " + COLUMN_NAME_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                        + COLUMN_NAME_VALUE + " integer not null,"
                        + COLUMN_NAME_TYPE_ID + " integer not null,"
                        + " FOREIGN KEY ("+COLUMN_NAME_NODE_ID+") REFERENCES "+NodeEntry.TABLE_NAME+" ("+NodeEntry.COLUMN_NAME_ID+"),"
                        + " FOREIGN KEY ("+COLUMN_NAME_TYPE_ID+") REFERENCES "+TypeEntry.TABLE_NAME+" ("+TypeEntry.COLUMN_NAME_ID+") ON DELETE CASCADE);";

         static final String SQL_DELETE_ENTRIES =  "DROP TABLE IF EXISTS " + MessageEntry.TABLE_NAME;
    }

     /**
      * Node database entry
      */
     static class NodeEntry implements BaseColumns { //TODO name + organization should be unique and foreign key
         static final String TABLE_NAME = "node";
         static final String COLUMN_NAME_ID = "id";
         static final String COLUMN_NAME_NAME = "name";
         static final String COLUMN_NAME_LOCATION_ID = "location_id";
         static final String COLUMN_NAME_ORGANIZATION_ID = "organization_id";

         static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + NodeEntry.TABLE_NAME + " (" + COLUMN_NAME_ID
                        + " integer primary key autoincrement, "
                        + COLUMN_NAME_NAME + " text not null, " +
                        COLUMN_NAME_LOCATION_ID + " integer not null," +
                        COLUMN_NAME_ORGANIZATION_ID + " integer not null,"
                        + " FOREIGN KEY ("+ COLUMN_NAME_ORGANIZATION_ID +") REFERENCES "+ OrganizationEntry.TABLE_NAME+" ("+ OrganizationEntry.COLUMN_NAME_ID+"),"
                        + " FOREIGN KEY ("+COLUMN_NAME_LOCATION_ID+") REFERENCES "+LocationEntry.TABLE_NAME+" ("+LocationEntry.COLUMN_NAME_ID+"));";

         static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + NodeEntry.TABLE_NAME;

    }

     /**
      * Type database entry
      */
     static class TypeEntry implements BaseColumns {
         static final String TABLE_NAME = "type";
         static final String COLUMN_NAME_ID = "id";
         static final String COLUMN_NAME_NAME = "name"; //TODO should be unique
         static final String COLUMN_NAME_UNIT = "unit";
         static final String COLUMN_NAME_ACTIVE = "active";
         static final String COLUMN_NAME_SUPPORTED = "supported";
         static final String COLUMN_NAME_LOW = "low";
         static final String COLUMN_NAME_MED = "med";
         static final String COLUMN_NAME_HIGH = "high";

         static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TypeEntry.TABLE_NAME + " (" + COLUMN_NAME_ID
                        + " integer primary key autoincrement, " + COLUMN_NAME_NAME
                        + " text not null, " + COLUMN_NAME_UNIT + " text null, "
                        + COLUMN_NAME_ACTIVE + " integer not null, "
                        + COLUMN_NAME_SUPPORTED + " integer not null, "
                        + COLUMN_NAME_LOW + " double not null, "
                        + COLUMN_NAME_MED + " double not null, "
                        + COLUMN_NAME_HIGH + " double not null);";

         static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TypeEntry.TABLE_NAME;
    }

     /**
      * Type alia database entry
      */
     static class TypeAliasEntry implements BaseColumns {
         static final String TABLE_NAME = "type_alias";
         static final String COLUMN_NAME_ID = "id";
         static final String COLUMN_NAME_NAME = "name";
         static final String COLUMN_NAME_TYPE_ID = "type_id";

         static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TypeAliasEntry.TABLE_NAME + " ("
                        + COLUMN_NAME_ID + " integer primary key autoincrement,"
                        + COLUMN_NAME_NAME + " text not null,"
                        + COLUMN_NAME_TYPE_ID + " integer not null,"
                        + " FOREIGN KEY ("+COLUMN_NAME_TYPE_ID+") REFERENCES "+TypeEntry.TABLE_NAME+" ("+TypeEntry.COLUMN_NAME_ID+"));";

         static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TypeAliasEntry.TABLE_NAME;
    }

     /**
      * Organization database entry
      */
     static class OrganizationEntry implements BaseColumns {
         static final String TABLE_NAME = "organization";
         static final String COLUMN_NAME_ID = "id";
         static final String COLUMN_NAME_NAME = "name";

         static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + OrganizationEntry.TABLE_NAME + " (" + COLUMN_NAME_ID
                        + " integer primary key autoincrement, " + COLUMN_NAME_NAME
                        + " text not null);";

         static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + OrganizationEntry.TABLE_NAME;
    }


     /**
      * Location database entry
      */
     static class LocationEntry implements BaseColumns { //TODO a location should probably be unique to an organization and not let it be shared.
         static final String TABLE_NAME = "Location";
         static final String COLUMN_NAME_ID = "id";
         static final String COLUMN_NAME_NAME = "name";
         static final String COLUMN_NAME_LAT = "lat";
         static final String COLUMN_NAME_LON = "lon";

         static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" + COLUMN_NAME_ID
                        + " integer primary key autoincrement, "
                        + COLUMN_NAME_NAME + " text not null,"
                        + COLUMN_NAME_LAT + " double null,"
                        + COLUMN_NAME_LON + " double null);";

         static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME;
    }

     /**
      * Connection database entry
      */
     static class ConnectionEntry implements BaseColumns {
         static final String TABLE_NAME = "connection";
         static final String COLUMN_NAME_ID = "id";
         static final String COLUMN_NAME_CONNECTION_TYPE = "connection_type";
         static final String COLUMN_NAME_URL = "url";
         static final String COLUMN_NAME_PORT = "port";
         static final String COLUMN_NAME_ARG0 = "arg0";
         static final String COLUMN_NAME_USERNAME = "username";
         static final String COLUMN_NAME_PASSWORD = "password";
         static final String COLUMN_NAME_ACTIVE = "active";
         static final String COLUMN_NAME_TOPIC_STRUCTURE_ID = "topic_structure_id";
         static final String COLUMN_NAME_DATA_STRUCTURE_ID = "data_structure_id";

         static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + ConnectionEntry.TABLE_NAME + " ("
                        + COLUMN_NAME_ID + " integer primary key autoincrement,"
                        + COLUMN_NAME_CONNECTION_TYPE + " text not null,"
                        + COLUMN_NAME_URL + " text not null,"
                        + COLUMN_NAME_PORT + " integer not null,"
                        + COLUMN_NAME_ARG0 + " text null,"
                        + COLUMN_NAME_USERNAME + " text null,"
                        + COLUMN_NAME_PASSWORD + " text null,"
                        + COLUMN_NAME_ACTIVE + " integer not null,"
                        + COLUMN_NAME_TOPIC_STRUCTURE_ID + " integer null,"
                        + COLUMN_NAME_DATA_STRUCTURE_ID + " integer not null,"
                        + " FOREIGN KEY ("+COLUMN_NAME_TOPIC_STRUCTURE_ID+") REFERENCES "+TopicStructureEntry.TABLE_NAME+" ("+TopicStructureEntry.COLUMN_NAME_ID+"),"
                        + " FOREIGN KEY ("+COLUMN_NAME_DATA_STRUCTURE_ID+") REFERENCES "+DataStructureEntry.TABLE_NAME+" ("+DataStructureEntry.COLUMN_NAME_ID+"));";

         static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + ConnectionEntry.TABLE_NAME;
    }

     /**
      * Data structure database entry
      */
     static class DataStructureEntry implements BaseColumns{
         static final String TABLE_NAME = "data_structure";
         static final String COLUMN_NAME_ID = "id";
         static final String COLUMN_NAME_NAME = "name";
         static final String COLUMN_NAME_CONNECTION_GROUP_ID = "connection_group_id";

         static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DataStructureEntry.TABLE_NAME + " ("
                        + COLUMN_NAME_ID + " integer primary key autoincrement,"
                        + COLUMN_NAME_NAME + " text not null,"
                        + COLUMN_NAME_CONNECTION_GROUP_ID + " integer not null,"
                        + " FOREIGN KEY (" + COLUMN_NAME_CONNECTION_GROUP_ID + ") REFERENCES " + ConnectionGroupEntry.TABLE_NAME + " (" + ConnectionGroupEntry.COLUMN_NAME_ID + "));";

         static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + DataStructureEntry.TABLE_NAME;
    }

     /**
      * Topic structure database entry
      */
     static class TopicStructureEntry implements BaseColumns{
         static final String TABLE_NAME = "topic_structure";
         static final String COLUMN_NAME_ID = "id";
         static final String COLUMN_NAME_NAME = "name";
         static final String COLUMN_NAME_REGEX = "regex";
         static final String COLUMN_NAME_LOCATION_REGEX_ID = "location_regex_id";
         static final String COLUMN_NAME_ORGANIZATION_REGEX_ID = "organization_regex_id";
         static final String COLUMN_NAME_NODE_REGEX_ID = "node_regex_id";
         static final String COLUMN_NAME_CONNECTION_GROUP_ID = "connection_group_id";

         static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TopicStructureEntry.TABLE_NAME + " ("
                        + COLUMN_NAME_ID + " integer primary key autoincrement,"
                        + COLUMN_NAME_NAME + " text not null,"
                        + COLUMN_NAME_REGEX + " text not null,"
                        + COLUMN_NAME_LOCATION_REGEX_ID + " integer null,"
                        + COLUMN_NAME_ORGANIZATION_REGEX_ID + " integer null,"
                        + COLUMN_NAME_NODE_REGEX_ID + " integer null,"
                        + COLUMN_NAME_CONNECTION_GROUP_ID + " integer not null,"
                        + " FOREIGN KEY (" + COLUMN_NAME_CONNECTION_GROUP_ID + ") REFERENCES " + ConnectionGroupEntry.TABLE_NAME + " (" + ConnectionGroupEntry.COLUMN_NAME_ID + "));";

         static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TopicStructureEntry.TABLE_NAME;
    }

     /**
      * Connection group database entry
      */
     static class ConnectionGroupEntry implements BaseColumns {
         static final String TABLE_NAME = "connection_group";
         static final String COLUMN_NAME_ID = "id";
         static final String COLUMN_NAME_NAME = "name";

         static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + ConnectionGroupEntry.TABLE_NAME + " ("
                        + COLUMN_NAME_ID + " integer primary key autoincrement, "
                        + COLUMN_NAME_NAME + " text unique not null);";

         static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + ConnectionGroupEntry.TABLE_NAME;
    }

}