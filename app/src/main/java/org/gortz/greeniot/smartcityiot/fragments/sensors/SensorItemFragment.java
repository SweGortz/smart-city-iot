package org.gortz.greeniot.smartcityiot.fragments.sensors;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.gortz.greeniot.smartcityiot.R;
import org.gortz.greeniot.smartcityiot.activity.SensorActivity;
import org.gortz.greeniot.smartcityiot.dto.MinMaxDTO;
import org.gortz.greeniot.smartcityiot.dto.ValueUnitRest;
import org.gortz.greeniot.smartcityiot.dto.sensors.SensorNodeKey;
import org.gortz.greeniot.smartcityiot.database.entity.SensorType;
import org.gortz.greeniot.smartcityiot.dto.sensors.SensorTypeNode;
import org.gortz.greeniot.smartcityiot.dto.sensors.SensorValue;
import org.gortz.greeniot.smartcityiot.fragments.sensors.base.SensorOptionFragment;

/**
 * Detailed view of a sensor node.
 */
public class SensorItemFragment extends SensorOptionFragment {

    private final String TAG = SensorItemFragment.class.getSimpleName();
    private SensorActivity activity;
    private TextView sensorTopic;
    private TextView sensorid;
    private TextView sensorTypeName;
    private TextView latestValue;
    private TextView unit;
    private TextView organization;
    private PointsGraphSeries<DataPoint> pointSeries;
    private LineGraphSeries<DataPoint> lineSeries;
    private List<SensorValue> dataPoints;
    private SensorTypeNode node;
    private View v;
    private GraphView graph;
    private String nodeName;
    private String organizationName;
    int sensorTypeID;

    private final Handler mHandler = new Handler();
    private Runnable mTimer1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.sensor_item_view, container, false);
        activity = (SensorActivity) getActivity();
        activity.invalidateOptionsMenu();
        activity.hideDrawer();

        sensorTopic = (TextView)v.findViewById(R.id.sensortopic);
        sensorid = (TextView) v.findViewById(R.id.sensorid);
        sensorTypeName = (TextView) v.findViewById(R.id.sensortypename);
        organization = (TextView) v.findViewById(R.id.organization);
        unit = (TextView) v.findViewById(R.id.unit);
        latestValue = (TextView) v.findViewById(R.id.latest_value);

        Bundle args = getArguments();
        nodeName = args.getString("id", "-1");
        organizationName = args.getString("organization", "org");
        organization.setText(organizationName);
        sensorTopic.setText(args.getString("location","location"));
        sensorTypeID = args.getInt("typeID", -1);
        sensorid.setText(nodeName);
        SensorType sensorType = activity.getSensorTypeByID(sensorTypeID);
        String tempSensorTypeName = sensorType.getName();
        String capitalizedSensorTypeName = tempSensorTypeName.substring(0, 1).toUpperCase() + tempSensorTypeName.substring(1);
        sensorTypeName.setText(capitalizedSensorTypeName);
        unit.setText(sensorType.getUnit());

        initGraph();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer1 = new RefreshGraph();
        mHandler.postDelayed(mTimer1, 300);
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer1);
        super.onPause();
    }

    /**
     * Find minimum and maximum values in a list of SensorValues.
     * @param sensorValues List of SensorValues.
     * @return MinMaxDTO containing min and max values.
     */
    public MinMaxDTO findMinMaxY(List<SensorValue> sensorValues){ //TODO should perhaps be in a util class?
        double min = -1;
        double max = -1;
        boolean firstRound = true;
        for(SensorValue sv : sensorValues){
            if(firstRound){
                min = sv.getValue();
                max = sv.getValue();
                firstRound = false;
            }
            else{
                if(min > sv.getValue()) min = sv.getValue();
                if(max < sv.getValue()) max = sv.getValue();
            }
        }
        return new MinMaxDTO(min, max);
    }

    //TODO Add more representation of a data type e.g. not only a graph.

    private void initGraph(){
        graph = (GraphView) v.findViewById(R.id.graph);
        graph.getGridLabelRenderer().setPadding(40);

        if( ! nodeName.equals("-1")) {
            node = activity.getSensorNodeOfTypeByID(new SensorNodeKey(nodeName,organizationName), sensorTypeID);
            latestValue.setText(String.valueOf(node.getLatestValue().getValue()));
            dataPoints = node.getValueList();
            Log.i(TAG,"Showing graph with "+dataPoints.size()+ " values");
            pointSeries = new PointsGraphSeries<>(dataPoints.toArray(new DataPoint[dataPoints.size()]));
            lineSeries = new LineGraphSeries<>(dataPoints.toArray(new DataPoint[dataPoints.size()]));
            lineSeries.setThickness(5);

            MinMaxDTO minMaxY = findMinMaxY(dataPoints);

            graph.getViewport().setMinY(minMaxY.getMin() * 0.95);
            graph.getViewport().setMaxY(minMaxY.getMax() * 1.05);
            graph.getViewport().setMinX(node.getEarliestValue().getTimestamp().getTimeInMillis());
            graph.getViewport().setMaxX(node.getLatestValue().getTimestamp().getTimeInMillis());
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setXAxisBoundsManual(true);

            Log.i(TAG, "min: " + node.getEarliestValue().getTimestamp().getTimeInMillis() + " max: " + node.getLatestValue().getTimestamp().getTimeInMillis());

            graph.getGridLabelRenderer().setLabelFormatter(new TimeAgoLabelFormatter());
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);
            graph.getGridLabelRenderer().setHumanRounding(false);
            graph.addSeries(pointSeries);
            graph.addSeries(lineSeries);
        }
    }

    private class RefreshGraph implements Runnable{
        @Override
        public void run() {
            Log.i(TAG, "Refreshing graph!");
            node = activity.getSensorNodeOfTypeByID(new SensorNodeKey(nodeName,organizationName), sensorTypeID);
            latestValue.setText(String.valueOf(node.getLatestValue().getValue()));
            /* Future work getting realtime updates on graph
            dataPoints = node.getValueList();

            for(SensorValue item : dataPoints){
                long timeNow = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
                long diff = TimeUnit.MILLISECONDS.toSeconds(timeNow - item.getLastRetrievalTimestamp().getTimeInMillis());
                Log.i(TAG, "Value: " + item.getValue() + " from: " + calcDistanceSpecific(diff, 4) + " ago");
            }
            Log.i(TAG, "Value: ----------------");

            MinMaxDTO minMaxY = findMinMaxY(dataPoints);

            graph.getViewport().setMinY(minMaxY.getMin() * 0.95);
            graph.getViewport().setMaxY(minMaxY.getMax() * 1.05);
            graph.getViewport().setMinX(node.getEarliestValue().getLastRetrievalTimestamp().getTimeInMillis());
            graph.getViewport().setMaxX(node.getLatestValue().getLastRetrievalTimestamp().getTimeInMillis());

            pointSeries.resetData(dataPoints.toArray(new DataPoint[dataPoints.size()]));
            lineSeries.resetData(dataPoints.toArray(new DataPoint[dataPoints.size()]));
            Log.i(TAG,"Showing graph with "+dataPoints.size()+ " values");
            */

            mHandler.postDelayed(this, 10000);
        }
    }

    /**
     * Calculate the distance between a Unix timestamp and the current date.
     * @param distance Unix timestamp.
     * @param significantNumbers Max number of units to include in result.
     * @return String containing amount of time passed since supplied timestamp.
     */
    public String calcDistanceSpecific(long distance, int significantNumbers){
        String result = "";
        for(int i = 0; i < significantNumbers; i++){
            ValueUnitRest res = calcDistance(distance);
            if(res.getValue() == 0 && !result.equals("")) {
                result = "\n" + result;
            }
            else{
                result += res.getValue() + " " + res.getUnit() + "\n";
                distance = res.getRest();
            }
        }
        return result + "ago";
    }

    private ValueUnitRest calcDistance(long distance){
        String unit;
        long rest;
        if(distance>=60){
            rest = distance % 60;
            distance = distance/60;
            if(distance>=60){
                rest += (distance%60)*60;
                distance = distance/60;
                if(distance>=24){
                    rest += (distance%24)*60*60;
                    distance = distance/24;
                    if(distance > 1) unit = "days";
                    else unit = "day";
                }
                else{
                    if(distance > 1) unit = "hours";
                    else unit = "hour";
                }
            }else{
                unit="min";
            }
        }else {
            unit = "sec";
            rest = 0;
        }
        return new ValueUnitRest(distance, rest, unit);
    }

    private class TimeAgoLabelFormatter extends DefaultLabelFormatter {
        @Override
        public String formatLabel(double value, boolean isValueX) {
            if (isValueX) {
                Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                Calendar mCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                mCalendar.setTimeInMillis((long) value);
                long distance = (TimeUnit.MILLISECONDS.toSeconds(now.getTimeInMillis() - mCalendar.getTimeInMillis()));
                return calcDistanceSpecific(distance, 2);
            } else {
                DecimalFormat df = new DecimalFormat("#.0");
                return df.format(value);
            }
        }
    }
}
