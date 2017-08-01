package org.gortz.greeniot.smartcityiot2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Container for Minimum and Maximum values.
 */
@Getter
@AllArgsConstructor(suppressConstructorProperties = true)
public class MinMaxDTO {
    /**
     * Minimum value.
     *
     * @return Min value.
     */
    double min;

    /**
     * Maximum value.
     *
     * @return Max value.
     */
    double max;
}
