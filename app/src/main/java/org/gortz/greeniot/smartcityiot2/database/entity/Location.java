package org.gortz.greeniot.smartcityiot2.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@AllArgsConstructor(suppressConstructorProperties = true)
public class Location {
    /**
     * Id
     *
     * @param id Set id of location.
     * @return id of location.
     */
    private int id;

    /**
     * name of location
     *
     * @param name Set name of location.
     * @return name of location.
     */
    private String name;

    @Setter private Coordinate position;

    public Location(String name, Coordinate position) {
        this.name = name;
        this.position = position;
    }

    public Location(String name) {
        this.name = name;
        this.position = new Coordinate(0.0,0.0);
    }

}
