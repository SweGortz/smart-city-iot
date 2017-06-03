package org.gortz.greeniot.smartcityiot.activity.base;

/**
 * Communication channel between fragment and activity
 */
public interface CommunicationChannel {
    /**
     * Option menu item click handler
     * @param listID of item that was clicked
     */
    void changeViewOption(int listID);
}
