package org.gortz.greeniot.greencityiot.database.entity;


import com.google.android.gms.maps.model.LatLng;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(suppressConstructorProperties = true)
public class Coordinate{
    /**
     * Latitude value to coordinate
     * @param latitude Set latitude of coordinate.
     * @return latitude of coordinate.
     */
    private double lat;

    /**
     * Longitude value to coordinate
     * @param longitude Set longitude of coordinate.
     * @return longitude of coordinate
     */
    private double lon;

    /**
     * Return LatLng version of the current coordinate
     * @return LatLng object
     */
    public LatLng getLatLng(){
        return new LatLng(lat, lon);
    }
}