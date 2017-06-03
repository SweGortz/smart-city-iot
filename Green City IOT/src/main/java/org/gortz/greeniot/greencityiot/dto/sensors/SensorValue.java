package org.gortz.greeniot.greencityiot.dto.sensors;

import com.jjoe64.graphview.series.DataPoint;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Container for a single sensor value.
 */
public class SensorValue extends DataPoint {
    private Calendar timestamp = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    private double value;

    /**
     * Create a new container for a sensor value.
     * @param timestamp Set timestamp of sensor reading.
     * @param value Set value of sensor reading.
     */
    public SensorValue(Calendar timestamp, Double value) {
        super(timestamp.getTime(), value);
        this.timestamp.setTimeInMillis(timestamp.getTimeInMillis());
        this.value = value;
    }

    /**
     * Create a new container for sensor value using current time as timestamp.
     * @param value Set value of sensor reading.
     */
    public SensorValue(double value) {
        super(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime(), value);
        this.timestamp = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        this.value = value;
    }

    /**
     * Get timestamp of sensor reading.
     * @return Timestamp of sensor reading.
     */
    public Calendar getTimestamp() {
        return timestamp;
    }

    /**
     * Get value from sensor reading.
     * @return value
     */
    public Double getValue() {
        return value;
    }


}
