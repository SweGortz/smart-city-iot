package org.gortz.greeniot.smartcityiot.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Type alias entity
 */
@Getter
@Setter
@AllArgsConstructor(suppressConstructorProperties = true)
public class TypeAlias {

    /**
     * Id of type alias
     *
     * @param id Set id of type alias
     * @return id of type alias
     */
    private int id;

    /**
     * name of type alias
     *
     * @return name of type alias
     */
    private String name;

    /**
     * id of type that the alias is pointing at
     *
     * @return sensor type id connected to the alias
     */
    private int typeID;
}
