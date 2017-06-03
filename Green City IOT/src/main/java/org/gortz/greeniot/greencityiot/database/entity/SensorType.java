package org.gortz.greeniot.greencityiot.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Sensor type entity
 */
@Getter
@AllArgsConstructor(suppressConstructorProperties = true)
public class SensorType {
    /**
     * Id
     *
     * @param id Set id of sensor type.
     * @return id of sensor type.
     */
    @Setter private int id;

    /**
     * name
     *
     * @return name of sensor type
     */
    private String name;

    /**
     * Unit
     *
     * @return Unit of sensor type
     */
    private String unit;

    /**
     * active
     *
     * @param active Set active of sensor type
     * @return true if the sensor type is currently active
     */
    @Setter private boolean active;

    /**
     * supported
     *
     * @param supported Set supported of sensor type
     * @return true if the sensor type is currently supported
     */
    @Setter private boolean supported;

    /**
     * Limits connected to sensor type
     *
     * @return limits connected to the sensor type
     */
    private SensorLimits limits;

    public SensorType(String name, String unit,boolean supported, boolean active, SensorLimits limits) {
        this.name = name;
        this.unit = unit;
        this.supported = supported;
        this.active = active;
        this.limits = limits;
    }

    public SensorType(String name, String unit) {
        this.name = name;
        this.unit = unit;
    }
}
