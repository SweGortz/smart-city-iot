package org.gortz.greeniot.smartcityiot.fragments.sensors.base;


import android.app.Fragment;

import org.gortz.greeniot.smartcityiot.activity.base.CommunicationChannel;

/**
 * Base class for visualization fragments.
 */
public class SensorOptionFragment extends Fragment implements CommunicationChannel {
    /**
     * Change view option in the options menu.
     * @param viewOption ID of selected item in options menu.
     */
    @Override public void changeViewOption(int viewOption) {}

}
