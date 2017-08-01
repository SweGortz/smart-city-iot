package org.gortz.greeniot.smartcityiot2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Container for value and unit and rest from division.
 */
@Getter
@AllArgsConstructor(suppressConstructorProperties = true)
public class ValueUnitRest{
    /**
     * Result from division.
     *
     * @return value
     */
    private long value;

    /**
     * Rest after division.
     *
     * @return Rest
     */
    private long rest;

    /**
     * Unit
     *
     * @return Unit
     */
    private String unit;
}