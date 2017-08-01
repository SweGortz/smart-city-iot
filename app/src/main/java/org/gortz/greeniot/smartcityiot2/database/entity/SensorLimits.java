package org.gortz.greeniot.smartcityiot2.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Sensor limits for the map view
 */
@Getter
@AllArgsConstructor(suppressConstructorProperties = true)
public class SensorLimits {

    /**
     * Lower limit
     *
     * @return lower limit.
     */
    private final double low;

    /**
     * medium limit
     *
     * @return medium limit.
     */
    private final double medium;

    /**
     * high limit
     *
     * @return high limit.
     */
    private final double high;
}
