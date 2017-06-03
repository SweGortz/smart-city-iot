package org.gortz.greeniot.smartcityiot.dto.locations;


import java.util.HashMap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gortz.greeniot.smartcityiot.database.entity.Location;

/**
 * For containing locations with and without GPS coordinates
 */

@Getter
@NoArgsConstructor
public class LocationsDbDTO {
    /**
     * All locations with GPS coordinates
     *
     * @return HashMap with Location objects containing GPS coordinates
     */
    HashMap<Integer,Location> locationsWithCoords = new HashMap<>();

    /**
     * All locations without GPS coordinates
     *
     * @return HashMap with Location objects without GPS coordinates
     */
    HashMap<Integer,Location> locationsWithoutCoords = new HashMap<>();
}
