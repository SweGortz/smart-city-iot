package org.gortz.greeniot.smartcityiot2.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data structure entity
 */
@Getter
@AllArgsConstructor(suppressConstructorProperties = true)
public class DataStructure {
    /**
     * Id
     *
     * @param id Set id of data structure.
     * @return id of data structure.
     */
    private int id;

    /**
     * name of data structure
     *
     * @param name Set name of data structure.
     * @return name of data structure.
     */
    private String name;

    /**
     * name of connection group connected to the data structure
     *
     * @param name of connection group connected to data structure.
     * @return name of connection group connected to data structure
     */
    private String groupName;

    public DataStructure(String name, String groupName) {
        this.name = name;
        this.groupName = groupName;
    }

    public DataStructure(String name) {
        this.name=name;
    }

    public DataStructure(int id) {
        this.id = id;
    }
}
