package learn.apptivitylab.com.petrolnav;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;

/**
 * Created by apptivitylab on 09/01/2018.
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG ="Navigation View";
    private DrawerLayout mDrawerLayout;
    private ViewGroup mContentViewGroup;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        mContentViewGroup = (ViewGroup) findViewById(R.id.activity_main_vg_container);
        mNavigationView = (NavigationView) findViewById(R.id.activity_main_navigation_view);


        //Prepare ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        //Create ActionBarDrawer Toggle
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, mToolbar, R.string.openDrawer, R.string.closeDrawer);
        drawerToggle.syncState();

        //prepare menu for navigationView
        mNavigationView.inflateMenu(R.menu.navigation_drawer_menu);
        mNavigationView.setNavigationItemSelectedListener(this);

        //Set a fragment as the default content fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_main_vg_container, new MapDisplayFragment())
                .commit();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //handle navigation view item click
        int id = item.getItemId();
        //switch to red,green,blue fragment
        changeFragment(id);
        mDrawerLayout.closeDrawers();
        return false;
    }

    private void changeFragment(int id){
        Fragment displayFragment = null;
        switch(id){
            case R.id.nav_recent:
                Log.d(TAG, "Show Recent History");
                //displayFragment = new RedFragment();
                break;
            case R.id.nav_favourite:
                Log.d(TAG, "Show Favourites");
                //displayFragment = new RedFragment();
                break;
            case R.id.nav_preference:
                Log.d(TAG, "Show Preference");
                //displayFragment = new RedFragment();
                break;
            case R.id.nav_search:
                Log.d(TAG, "Show Search");
                //displayFragment = new RedFragment();
                break;
            case R.id.nav_log_out:
                Log.d(TAG, "Show Log Out");
                //displayFragment = new RedFragment();
                break;
        }
        //.add(R.id.activity_main_vg_container, helloFragment)
        //mean add the view onto the main container view.

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_vg_container, displayFragment)
                .addToBackStack(null)
                .commit();


    }
}
