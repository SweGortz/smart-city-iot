package org.gortz.greeniot.smartcityiot.fragments.sensors;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.Collection;

import org.gortz.greeniot.smartcityiot.R;
import org.gortz.greeniot.smartcityiot.activity.SensorActivity;
import org.gortz.greeniot.smartcityiot.database.entity.SensorLimits;
import org.gortz.greeniot.smartcityiot.database.entity.SensorType;
import org.gortz.greeniot.smartcityiot.dto.sensors.SensorTypeNode;
import org.gortz.greeniot.smartcityiot.fragments.sensors.base.SensorOptionFragment;

/**
 * Visualization of sensor data on a map.
 */
public class MapFragment extends SensorOptionFragment {
    private static final String LOG = MapFragment.class.getSimpleName();
    private final String TAG = MapFragment.class.getSimpleName();
    private MapView mMapView;
    private GoogleMap googleMap;
    private ClusterManager<SensorTypeNode> mClusterManager;
    private MyClusterRenderer renderer;
    private Location defaultStartPosition = new Location("");
    private float zoom = 15;
    private SensorActivity activity;
    private CameraPosition cp;
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_view, container, false);
        activity = (SensorActivity) getActivity();
        activity.invalidateOptionsMenu();
        activity.showDrawer();
        activity.setFromSensorCluster(false);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        defaultStartPosition.setLatitude(59.857828);
        defaultStartPosition.setLongitude(17.646929);


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                if (cp != null) {
                    getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cp));
                } else {
                    if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        getMap().setMyLocationEnabled(true);
                    }

                    Location bestCurrentLocation = getBestCurrentLocation();
                    if(bestCurrentLocation == null){
                        bestCurrentLocation = defaultStartPosition;
                    }else{
                        setCurrentLocationButtonStyle();
                    }
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(bestCurrentLocation.getLatitude(), bestCurrentLocation.getLongitude()), zoom));
                }
                setUpCluster();
            }
        });

        return v;
    }


    private Location getBestCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            long GPSLocationTime = 0;
            if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

            long NetLocationTime = 0;

            if (null != locationNet) {
                NetLocationTime = locationNet.getTime();
            }

            if ( 0 < GPSLocationTime - NetLocationTime ) {
                return locationGPS;
            }
            else {
                return locationNet;
            }
        }else {
            return defaultStartPosition;
        }
    }

    private void setCurrentLocationButtonStyle(){
        View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        relativeLayoutParams.setMargins(0, 0, 30, 30);
    }


    @Override
    public void onResume() {
        super.onResume();
        mTimer1 = new RefreshMap();
        mHandler.postDelayed(mTimer1, 300);
    }


    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer1);
        super.onPause();
        if (googleMap != null) {
            cp = googleMap.getCameraPosition();
        }
    }

    private void setUpCluster() {
        while (getContext() == null) {
        }

        mClusterManager = new ClusterManager<>(getContext(), getMap());
        renderer = new MyClusterRenderer(getContext(), getMap(), mClusterManager);
        mClusterManager.setRenderer(renderer);


        getMap().setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterItemClickListener(getNewclusterItemClickListener());
        mClusterManager.setOnClusterClickListener(getNewclusterClickListener());

        getSensorDataOfTypeToClusterManager(activity.getCurrentSensorTypeId(), mClusterManager);
    }

    private ClusterManager.OnClusterItemClickListener<SensorTypeNode> getNewclusterItemClickListener(){
        return new ClusterManager.OnClusterItemClickListener<SensorTypeNode>() {
            @Override
            public boolean onClusterItemClick(SensorTypeNode sensor) {
                System.out.println("Pressure sensor clicked ");
                activity.goToSensorDetails(sensor, sensorType.getId());
                return false;
            }
        };
    }

    private ClusterManager.OnClusterClickListener<SensorTypeNode> getNewclusterClickListener(){
        return new ClusterManager.OnClusterClickListener<SensorTypeNode>(){
            @Override
            public boolean onClusterClick(Cluster<SensorTypeNode> cluster) {
                System.out.println("sensor cluster clicked ");
                activity.goToSensorsList(cluster.getItems(), sensorType.getId());
                return false;
            }
        };
    }

    private SensorType sensorType;

   TileOverlay mOverlay;
    private void getSensorDataOfTypeToClusterManager(int sensorTypeID, ClusterManager cm){
        Collection<SensorTypeNode> st =activity.getSensorDataOfTypeWithCoordinate(sensorTypeID);
        cm.addItems(st);
        ArrayList<WeightedLatLng> weightedLatLngsNodes = new ArrayList<>();
        for(SensorTypeNode stn : st){
            weightedLatLngsNodes.add(stn.getWeightedLatLng());
        }
        /*if (mOverlay != null) mOverlay.remove(); //todo heatmap
        if(weightedLatLngsNodes.size()> 0) {
            HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                    .weightedData(weightedLatLngsNodes).build();
            //.gradient(gradient)

            mOverlay = getMap().addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }*/
        sensorType = activity.getSensorTypeByID(sensorTypeID);
        cm.cluster();
    }

    /**
     * Get the map.
     * @return The map as a GoogleMap.
     */
    public GoogleMap getMap() {
        return googleMap;
    }

    private void clusterManagerClearAll(){
        mClusterManager.clearItems();
        mClusterManager.cluster();
    }

    private void setCameraIdleListenerAndMarkerClickListener(ClusterManager cm){
        getMap().setOnCameraIdleListener(cm);
        getMap().setOnMarkerClickListener(cm);
    }


    @Override
    public void changeViewOption(int sensorTypeId) {
        //Log.i(TAG,String.valueOf(sensorTypeId));
        clusterManagerClearAll();
        activity.setCurrentSensorTypeId(sensorTypeId);
        getSensorDataOfTypeToClusterManager(sensorTypeId, mClusterManager);
        setCameraIdleListenerAndMarkerClickListener(mClusterManager);

    }

    private class MyClusterRenderer extends DefaultClusterRenderer<SensorTypeNode> {
        private Context context;
        public MyClusterRenderer(Context context, GoogleMap map, ClusterManager<SensorTypeNode> clusterManager) {
            super(context, map, clusterManager);
            this.context = context;
        }

        @Override
        protected void onBeforeClusterItemRendered(SensorTypeNode item, MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<SensorTypeNode> cluster, MarkerOptions markerOptions){
            System.out.println("I want to render stuff soon.");
            double totOfAllSensor=0;
            String unit=sensorType.getUnit();
            SensorLimits limit = sensorType.getLimits();
            for(SensorTypeNode i : cluster.getItems()){
                System.out.println("I want to render stuff now!");
                    totOfAllSensor+= i.getLatestValue().getValue();
            }
            double averageSensorValue = totOfAllSensor/cluster.getItems().size();
            averageSensorValue = (double)( ((int)(averageSensorValue * 10)) / 10 );//todo FIXA!!?!?!?

            Bitmap iconBitmap = getRelativeIconColor(averageSensorValue, limit).makeIcon(String.valueOf(averageSensorValue)+" "+unit);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconBitmap));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() > 1;
        }



        @Override
        protected void onClusterItemRendered(SensorTypeNode clusterItem, Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);
            try{
                Bitmap iconBitmap = getRelativeIconColor(clusterItem.getLatestValue().getValue(),sensorType.getLimits()).makeIcon(String.valueOf(clusterItem.getLatestValue().getValue())+ " " +sensorType.getUnit());
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconBitmap));
                marker.setAnchor(0.5f, 0.6f);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        private IconGenerator getRelativeIconColor(Double measurement, SensorLimits limits){
            IconGenerator icnGenerator = new IconGenerator(this.context);
            if(measurement<=limits.getLow()){
                icnGenerator.setStyle(IconGenerator.STYLE_PURPLE);
            }else if(measurement > limits.getLow() && measurement <= limits.getMedium()){
                icnGenerator.setStyle(IconGenerator.STYLE_BLUE);
            }else if(measurement>limits.getMedium() && measurement<=limits.getHigh()){
                icnGenerator.setStyle(IconGenerator.STYLE_GREEN);
            }else if(measurement>limits.getHigh() ){
                icnGenerator.setStyle(IconGenerator.STYLE_ORANGE);
            }
            return icnGenerator;
        }
    }
    private class RefreshMap implements Runnable{
        @Override
        public void run() {
            Log.i(TAG, "Refreshing map view");
            clusterManagerClearAll();
            getSensorDataOfTypeToClusterManager(activity.getCurrentSensorTypeId(), mClusterManager);
            setCameraIdleListenerAndMarkerClickListener(mClusterManager);
            mHandler.postDelayed(this, 10000);
        }
    }

}