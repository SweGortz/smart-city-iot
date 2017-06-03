package org.gortz.greeniot.greencityiot.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Connection group entity
 */
@Getter
@Setter
@AllArgsConstructor(suppressConstructorProperties = true)
public class ConnectionGroup {
    /**
     * Id
     *
     * @param id Set id of connection group.
     * @return id of connection group.
     */
    private int id;

    /**
     * name of connection group
     *
     * @param name Set name of connection group.
     * @return id of connection group.
     */
    private String name;
}
