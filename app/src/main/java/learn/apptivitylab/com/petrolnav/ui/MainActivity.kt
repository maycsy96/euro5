package learn.apptivitylab.com.petrolnav.ui

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import learn.apptivitylab.com.petrolnav.R

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import android.location.Location;
/**
 * Created by apptivitylab on 09/01/2018.
 */

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var mDrawerLayout: DrawerLayout? = null
    private var mContentViewGroup: ViewGroup? = null
    private var mNavigationView: NavigationView? = null
    private var mToolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDrawerLayout = findViewById<View>(R.id.activity_main_drawer_layout) as DrawerLayout
        mContentViewGroup = findViewById<View>(R.id.activity_main_vg_container) as ViewGroup
        mNavigationView = findViewById<View>(R.id.activity_main_navigation_view) as NavigationView
        mToolbar = findViewById<View>(R.id.activity_main_toolbar) as Toolbar

        //Prepare ActionBar
        setSupportActionBar(mToolbar)

        //Create ActionBarDrawer Toggle
        val drawerToggle = ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.openDrawer, R.string.closeDrawer)
        mDrawerLayout!!.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        //prepare menu for navigationView
        mNavigationView!!.inflateMenu(R.menu.navigation_drawer_menu)
        mNavigationView!!.setNavigationItemSelectedListener(this)

        //Set a fragment as the default content fragment
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.activity_main_vg_container, MapDisplayFragment())
                .commit()

        /*val testObject = TestClass(15)
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("String Key")
        startActivity(intent)*/

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //handle navigation view item click
        val id = item.itemId
        //switch to red,green,blue fragment
        changeFragment(id)
        mDrawerLayout!!.closeDrawers()
        return false
    }

    private fun changeFragment(id: Int) {
        var displayFragment: Fragment? = null
        when (id) {
            R.id.nav_recent -> Log.d(TAG, "Show Recent History")
            R.id.nav_petrol_price -> Log.d(TAG, "Show Petrol Price")
            R.id.nav_preference -> {
                Log.d(TAG, "Show Preference")
                displayFragment = PreferencesFragment();
            }
            R.id.nav_log_out -> Log.d(TAG, "Show Log Out")
        }//displayFragment = new RedFragment();
        //displayFragment = new RedFragment();
        //displayFragment = new RedFragment();
        //displayFragment = new RedFragment();
        //displayFragment = new RedFragment();
        //.add(R.id.activity_main_vg_container, helloFragment)
        //mean add the view onto the main container view.

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.activity_main_vg_container, displayFragment)
                .addToBackStack(null)
                .commit()


    }

    companion object {
        private val TAG = "Navigation View"
    }
}
