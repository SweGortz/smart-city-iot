package org.gortz.greeniot.smartcityiot.model.queue;

import org.junit.Test;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.gortz.greeniot.smartcityiot.dto.sensors.SensorValue;
import static junit.framework.Assert.assertEquals;


public class SensorValueQueueTest {
    @Test
    public void isEmpty() throws Exception {
    assertEquals(true,new SensorValueQueue(5).isEmpty());
    }

    @Test
    public void peek() throws Exception {
    SensorValueQueue sensorValueQueue = new SensorValueQueue(5);
        sensorValueQueue.enqueue(new SensorValue(Calendar.getInstance(),5.0));
        assertEquals(5.0 , sensorValueQueue.peekEnd().getValue());
    }

    @Test
    public void enqueue() throws Exception {
        SensorValueQueue sensorValueQueue = new SensorValueQueue(5);
        sensorValueQueue.enqueue(new SensorValue(Calendar.getInstance(),5.0));
    }

    @Test
    public void getSize() throws Exception {
        SensorValueQueue sensorValueQueue = new SensorValueQueue(5);
        sensorValueQueue.enqueue(new SensorValue(Calendar.getInstance(),5.0));
        assertEquals(1,sensorValueQueue.getSize());
        Thread.sleep(10);
        sensorValueQueue.enqueue(new SensorValue(Calendar.getInstance(),5.0));
        assertEquals(2,sensorValueQueue.getSize());
    }


    @Test
    public void getValues() throws Exception {
        SensorValueQueue sensorValueQueue = new SensorValueQueue(5);
        sensorValueQueue.enqueue(new SensorValue(Calendar.getInstance(),1.0));
        Thread.sleep(10);
        sensorValueQueue.enqueue(new SensorValue(Calendar.getInstance(),2.0));
        Thread.sleep(10);
        sensorValueQueue.enqueue(new SensorValue(Calendar.getInstance(),3.0));
        Thread.sleep(10);
        sensorValueQueue.enqueue(new SensorValue(Calendar.getInstance(),4.0));
        Thread.sleep(10);
        sensorValueQueue.enqueue(new SensorValue(Calendar.getInstance(),5.0));
        double value = 1.0;
        for(SensorValue sv : sensorValueQueue.getValues()){
            assertEquals(sv.getValue(),value++);
        }
    }

    @Test
    public void valuesAtTheSameTime() throws Exception {
        SensorValueQueue sensorValueQueue = new SensorValueQueue(5);
        Calendar mCalendar = createCalendarFromTimestamp(10);
        sensorValueQueue.enqueue(new SensorValue(mCalendar,1.0));
        sensorValueQueue.enqueue(new SensorValue(mCalendar,2.0));
        sensorValueQueue.enqueue(new SensorValue(mCalendar,3.0));
        sensorValueQueue.enqueue(new SensorValue(mCalendar,4.0));
        sensorValueQueue.enqueue(new SensorValue(mCalendar,5.0));
        assertEquals(1,sensorValueQueue.getSize());
    }

    @Test
    public void valuesAtTheSameTimeButInDifferentOrder() throws Exception {
        Calendar mCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        SensorValueQueue sensorValueQueue = new SensorValueQueue(5);
        sensorValueQueue.enqueue(new SensorValue(createCalendarFromTimestamp(1496332554000L),1.0));
        sensorValueQueue.enqueue(new SensorValue(createCalendarFromTimestamp(1496332654000L),2.0));
        sensorValueQueue.enqueue(new SensorValue(createCalendarFromTimestamp(1496332454000L),3.0));
        sensorValueQueue.enqueue(new SensorValue(createCalendarFromTimestamp(1496332554000L),4.0));
        sensorValueQueue.enqueue(new SensorValue(createCalendarFromTimestamp(1496333454000L),5.0));
        assertEquals(4,sensorValueQueue.getSize());
    }

    private Calendar createCalendarFromTimestamp(long timestamp){
        Calendar mCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        mCalendar.setTime(new Date(timestamp));
        return mCalendar;
    }

}