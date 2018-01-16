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

/**
 * Created by apptivitylab on 09/01/2018.
 */

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var drawerLayout: DrawerLayout? = null
    private var contentViewGroup: ViewGroup? = null
    private var navigationView: NavigationView? = null
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.drawerLayout = findViewById<View>(R.id.activity_main_drawer_layout) as DrawerLayout
        this.contentViewGroup = findViewById<View>(R.id.activity_main_vg_container) as ViewGroup
        this.navigationView = findViewById<View>(R.id.activity_main_navigation_view) as NavigationView
        this.toolbar = findViewById<View>(R.id.activity_main_toolbar) as Toolbar

        //Prepare ActionBar
        setSupportActionBar(toolbar)

        //Create ActionBarDrawer Toggle
        val drawerToggle = ActionBarDrawerToggle(this, this.drawerLayout, this.toolbar, R.string.openDrawer, R.string.closeDrawer)
        this.drawerLayout!!.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        //prepare menu for navigationView
        this.navigationView!!.inflateMenu(R.menu.navigation_drawer_menu)
        this.navigationView!!.setNavigationItemSelectedListener(this)

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
        this.drawerLayout!!.closeDrawers()
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
