package org.gortz.greeniot.greencityiot.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;

import org.gortz.greeniot.greencityiot.R;
import org.gortz.greeniot.greencityiot.activity.base.BaseActivity;
import org.gortz.greeniot.greencityiot.service.SubscriberService;

/**
 * First activity to start. Handles the loading of background services
 */
public class StartActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_view);

        startListenDaemon(this);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                changeActivity(SensorActivity.class,"map");
                finish();
            }
        }, 1000);
    }

    /**
     * Start the background service
     * @param context of application
     */
    private static void startListenDaemon(Context context) {
        Intent intent = new Intent(context, SubscriberService.class);
        intent.setAction("se.kth.greeniot.greencityiot.service.action.start.listen.daemon");
        context.startService(intent);
    }
}
