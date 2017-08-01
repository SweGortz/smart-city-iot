package org.gortz.greeniot.smartcityiot2.fragments.sensors;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.gortz.greeniot.smartcityiot2.dto.MinMaxDTO;
import org.gortz.greeniot.smartcityiot2.dto.sensors.SensorValue;

import static org.junit.Assert.*;

public class SensorItemFragmentTest {
SensorItemFragment fragment;
    @Before
    public void setup(){
        fragment = new SensorItemFragment();
    }

    @Test
    public void calcDistanceSpecificDay(){
        Calendar mCalendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Calendar mCalendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        mCalendar2.setTimeInMillis(mCalendar1.getTimeInMillis());
        mCalendar2.add(Calendar.DAY_OF_MONTH, -1);
        assertEquals("1 day ", fragment.calcDistanceSpecific(TimeUnit.MILLISECONDS.toSeconds(mCalendar1.getTimeInMillis() - mCalendar2.getTimeInMillis()), 1));
    }

    @Test
    public void calcDistanceSpecificHour(){
        Calendar mCalendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Calendar mCalendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        mCalendar2.setTimeInMillis(mCalendar1.getTimeInMillis());
        mCalendar2.add(Calendar.HOUR, -1);
        assertEquals("1 hour ", fragment.calcDistanceSpecific(TimeUnit.MILLISECONDS.toSeconds(mCalendar1.getTimeInMillis() - mCalendar2.getTimeInMillis()), 1));
    }

    @Test
    public void calcDistanceSpecificMinute(){
        Calendar mCalendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Calendar mCalendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        mCalendar2.setTimeInMillis(mCalendar1.getTimeInMillis());
        mCalendar2.add(Calendar.MINUTE, -1);
        assertEquals("1 min ", fragment.calcDistanceSpecific(TimeUnit.MILLISECONDS.toSeconds(mCalendar1.getTimeInMillis() - mCalendar2.getTimeInMillis()), 1));
    }

    @Test
    public void calcDistanceSpecificDays(){
        Calendar mCalendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Calendar mCalendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        mCalendar2.setTimeInMillis(mCalendar1.getTimeInMillis());
        mCalendar2.add(Calendar.DAY_OF_MONTH, -2);
        assertEquals("2 days ", fragment.calcDistanceSpecific(TimeUnit.MILLISECONDS.toSeconds(mCalendar1.getTimeInMillis() - mCalendar2.getTimeInMillis()), 1));
    }

    @Test
    public void calcDistanceSpecificHours(){
        Calendar mCalendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Calendar mCalendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        mCalendar2.setTimeInMillis(mCalendar1.getTimeInMillis());
        mCalendar2.add(Calendar.HOUR, -2);
        assertEquals("2 hours ", fragment.calcDistanceSpecific(TimeUnit.MILLISECONDS.toSeconds(mCalendar1.getTimeInMillis() - mCalendar2.getTimeInMillis()), 1));
    }

    @Test
    public void calcDistanceSpecificSecond(){
        Calendar mCalendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Calendar mCalendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        mCalendar2.setTimeInMillis(mCalendar1.getTimeInMillis());
        mCalendar2.add(Calendar.SECOND, -1);
        assertEquals("1 sec ", fragment.calcDistanceSpecific(TimeUnit.MILLISECONDS.toSeconds(mCalendar1.getTimeInMillis() - mCalendar2.getTimeInMillis()), 1));
    }

    @Test
    public void calcDistanceSpecificDayAndHour(){
        Calendar mCalendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Calendar mCalendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        mCalendar2.setTimeInMillis(mCalendar1.getTimeInMillis());
        mCalendar2.add(Calendar.HOUR, -25);
        assertEquals("1 day 1 hour ", fragment.calcDistanceSpecific(TimeUnit.MILLISECONDS.toSeconds(mCalendar1.getTimeInMillis() - mCalendar2.getTimeInMillis()), 2));
    }

    @Test
    public void calcDistanceSpecificHourAndMin(){
        Calendar mCalendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Calendar mCalendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        mCalendar2.setTimeInMillis(mCalendar1.getTimeInMillis());
        mCalendar2.add(Calendar.MINUTE, -61);
        assertEquals("1 hour 1 min ", fragment.calcDistanceSpecific(TimeUnit.MILLISECONDS.toSeconds(mCalendar1.getTimeInMillis() - mCalendar2.getTimeInMillis()), 2));
    }

    @Test
    public void calcDistanceSpecificMinAndSec(){
        Calendar mCalendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Calendar mCalendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        mCalendar2.setTimeInMillis(mCalendar1.getTimeInMillis());
        mCalendar2.add(Calendar.SECOND, -61);
        assertEquals("1 min 1 sec ", fragment.calcDistanceSpecific(TimeUnit.MILLISECONDS.toSeconds(mCalendar1.getTimeInMillis() - mCalendar2.getTimeInMillis()), 2));
    }

    @Test
    public void findMinMaxY(){
        ArrayList<SensorValue> list = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            list.add(new SensorValue(createCalendarFromTimestamp(i), (double)i));
        }
        MinMaxDTO minMax = fragment.findMinMaxY(list);
        assertEquals(9, minMax.getMax(), 0);
        assertEquals(0, minMax.getMin(), 0);
    }



    private Calendar createCalendarFromTimestamp(long timestamp){
        Calendar mCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        mCalendar.setTime(new Date(timestamp));
        return mCalendar;
    }
}