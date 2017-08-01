package org.gortz.greeniot.smartcityiot2.activity.base;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import org.gortz.greeniot.smartcityiot2.R;
import org.gortz.greeniot.smartcityiot2.activity.SensorActivity;
import org.gortz.greeniot.smartcityiot2.activity.SettingsActivity;

/**
 * Base activity to handle all basic features features of an activity
 */
public class BaseActivity  extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActionBarDrawerToggle toggle;
    private Fragment currentFragment;
    private boolean fromSensorCluster;

    /**
     * Set the current fragment
     * @param currentFragment to be set
     */
    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
    }

    /**
     * Set if the call was made from a sensor cluster
     * @param fromSensorCluster true if the call came from sensor cluester
     */
    public void setFromSensorCluster(boolean fromSensorCluster){
        this.fromSensorCluster = fromSensorCluster;
    }

    /**
     * Check if the call was bade from sensor cluster
     * @return
     */
    public boolean getFromSensorCluster(){
        return fromSensorCluster;
    }

    /**
     * Get the current fragment
     * @return the current fragment
     */
    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    /**
     * Start the the view and create toolbar
     */
    protected void startActivityFrame(){
        setContentView(R.layout.activity_main);
        createToolbar();
        initMenuNavListener();
    }

    /**
     * Create the toolbar
     */
    protected void createToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.setToolbarNavigationClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) { //TODO kanske att man ska kontrollera att det är back arrow som man tryckt på ifall något annat också kan leda hit.
                onBackPressed();
            }
        });

        toggle.syncState();
    }

    /** todo jimmy, stämmer det?
     * Hide the drawer
     * @return true if it succeeded
     */
    public Boolean hideDrawer(){
        Boolean hidden = false;
        if(toggle != null){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            toggle.setDrawerIndicatorEnabled(false);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            hidden = true;
        }
        return hidden;
    }

    /**
     * Show the drawer
     * @return true if it succeeded
     */
    public Boolean showDrawer(){
        Boolean visible = false;
        if(toggle != null){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            toggle.setDrawerIndicatorEnabled(true);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            visible = true;
        }
        return visible;
    }

    /**
     * Start the menu navigation listener
     */
    protected void initMenuNavListener(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    /**
     * Change fragment without  history it can go back to
     * @param fragment to be set
     */
    protected void setFragmentWithoutBackStack(Fragment fragment){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        String fragmentTag = fragment.getClass().getSimpleName();
        fragmentTransaction.replace(R.id.fragment_place, fragment, fragmentTag);
        fragmentTransaction.commit();
    }

    /**
     * Change fragment with history it can go back to without name
     * @param fragment to be set
     */
    protected void setFragmentOnBackStack(Fragment fragment){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        String fragmentTag = fragment.getClass().getSimpleName();
        fragmentTransaction.replace(R.id.fragment_place, fragment, fragmentTag);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     *  Change fragment with history it can go back to with name
     * @param fragment to be set
     * @param currentFragmentName of old fragment
     */
    protected void setFragmentOnBackStack(Fragment fragment, String currentFragmentName){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        String fragmentTag = fragment.getClass().getSimpleName();
        fragmentTransaction.replace(R.id.fragment_place, fragment, fragmentTag);
        fragmentTransaction.addToBackStack(currentFragmentName);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0){
            this.finish();
        }
        else{
            getFragmentManager().popBackStack();
        }
    }
    /**
     * Change activity
     * @param newActivity to change to
     */
    protected void changeActivity(Class newActivity, String message){
        Intent intent = new Intent(this, newActivity);
        intent.putExtra("start",message);
        startActivity(intent);
    }

    /**
     * Change activity
     * @param newActivity to change to
     */
    protected void changeActivity(Class newActivity){
        Intent intent = new Intent(this, newActivity);
        startActivity(intent);
    }

    /**
     * Menu event handler
     * @param item that was selected
     * @return true if it could change page
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_map) {
            changeActivity(SensorActivity.class,"map");
        } else if (id == R.id.nav_sensors) {
            changeActivity(SensorActivity.class,"list");
        } else if (id == R.id.nav_settings) {
            changeActivity(SettingsActivity.class);
        }
        closeMenuNav();
        return true;
    }

    /**
     * Close the navigation menu
     */
    protected void closeMenuNav(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

}
