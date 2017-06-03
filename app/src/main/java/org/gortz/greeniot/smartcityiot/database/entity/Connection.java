package org.gortz.greeniot.smartcityiot.database.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * For containing all parameters needed for a connection.
 */

@Getter
@Setter
@AllArgsConstructor(suppressConstructorProperties = true)
public class Connection {

    /**
     * Id
     *
     * @param id Set id of connection.
     * @return id of connection.
     */
    private int id;

    /**
     * ConnectionType
     *
     * @param connectionType Set connectionType.
     * @return the ConnectionType.
     */
    private String connectionType;
    /**
     * Connection URL
     *
     * @param url Set connection URL.
     * @return connection URL.
     */
    private String url;

    /**
     * Connection port
     *
     * @param port Set connection port number.
     * @return connection port number.
     */
    private int port;

    /**
     * Connection argument
     *
     * @param arg0 Set connection argument.
     * @return connection argument.
     */
    private String arg0;

    /**
     * Authentication username
     *
     * @param username Set username for authentication.
     * @return authentication username.
     */
    private String username;

    /**
     * Authentication password
     *
     * @param password Set password for authentication.
     * @return authentication password.
     */
    private String password;

    /**
     * Active
     *
     * @param active Set connection as active or inactive.
     * @return true if connection is activated and false if not.
     */
    private boolean active;

    /**
     * TopicStructure
     *
     * @param topicStructure Set TopicStructure of connection.
     * @return the TopicStructure of the connection.
     */
    private TopicStructure topicStructure;

    /**
     * DataStructure
     *
     * @param dataStructure Set DataStructure of connection.
     * @return the DataStructure of the connection.
     */
    private DataStructure dataStructure;


    public Connection(String connectionType, String url, int port, String arg0, String username, String password, boolean active, TopicStructure topicStructure, DataStructure dataStructure) {
        this.connectionType = connectionType;
        this.url = url;
        this.port = port;
        this.arg0 = arg0;
        this.username = username;
        this.password = password;
        this.active = active;
        this.topicStructure = topicStructure;
        this.dataStructure = dataStructure;
    }

    public Connection(String connectionType, String url, int port, String arg0, boolean active, TopicStructure topicStructure, DataStructure dataStructure) {
        this.connectionType = connectionType;
        this.url = url;
        this.port = port;
        this.arg0 = arg0;
        this.active = active;
        this.topicStructure = topicStructure;
        this.dataStructure = dataStructure;
    }
}
