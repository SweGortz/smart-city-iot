package org.gortz.greeniot.smartcityiot2.fragments.settings;

import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.gortz.greeniot.smartcityiot2.R;
import org.gortz.greeniot.smartcityiot2.activity.base.BaseActivity;


/**
 * "About" screen
 */
public class AboutFragment extends Fragment {
    private BaseActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.about_view, container, false);
        this.activity = (BaseActivity) getActivity();
        activity.invalidateOptionsMenu();
        activity.hideDrawer();

        Button licenseButton = (Button) v.findViewById(R.id.licenses);
        TextView versionNumber = (TextView) v.findViewById(R.id.version_number);


        try{
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            versionNumber.setText(pInfo.versionName);
        }
        catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        licenseButton.setOnClickListener(new LicenseButtonListener());

        return v;
    }

    private class LicenseButtonListener implements  View.OnClickListener{

        @Override
        public void onClick(View v) {
            activity.goToFragment(new LicensesFragment());
        }
    }

}
