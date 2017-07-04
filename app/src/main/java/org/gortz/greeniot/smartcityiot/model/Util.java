package org.gortz.greeniot.smartcityiot.model;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.PermissionChecker;

public final class Util {

    private Util(){}

    public static boolean selfPermissionGranted(Activity activity, String permission){
        int targetSdkVersion = activity.getApplicationInfo().targetSdkVersion;
        // For Android versions before Marshmallow, self permissions are always granted.
        boolean result = true;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(targetSdkVersion >= Build.VERSION_CODES.M){
                // targetSDKVersion >= Android Marshmallow, we can use Context#checkSelfPermission
                result = activity.getApplicationContext().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            }
            else{
                // targetSdkVersion < Android Marshmallow, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(activity.getApplicationContext(), permission) == PermissionChecker.PERMISSION_GRANTED;
            }
        }
        return result;
    }
}
