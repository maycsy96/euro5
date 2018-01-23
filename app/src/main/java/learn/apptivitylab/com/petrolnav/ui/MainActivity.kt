package learn.apptivitylab.com.petrolnav.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import learn.apptivitylab.com.petrolnav.R

/**
 * Created by apptivitylab on 09/01/2018.
 */

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val drawerToggle = ActionBarDrawerToggle(this, this.drawer_layout, this.toolbar, R.string.openDrawer, R.string.closeDrawer)
        this.drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        this.navigationView.inflateMenu(R.menu.navigation_drawer_menu)
        this.navigationView.setNavigationItemSelectedListener(this)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.mainViewgroupContainer, MapDisplayFragment())
                .commit()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        navigateTo(id)
        this.drawer_layout?.closeDrawers()
        return true
    }

    private fun navigateTo(id: Int) {
        var displayFragment: Fragment? = null
        when (id) {
            R.id.nav_map -> {
                Log.d(TAG, "Show Map")
                displayFragment = MapDisplayFragment()
            }
            R.id.nav_search -> {
                Log.d(TAG, "Show Search")
                displayFragment = SearchFragment()
            }
            R.id.nav_petrol_price -> Log.d(TAG, "Show Petrol Price")
            R.id.nav_preference -> {
                Log.d(TAG, "Show Preference")
                displayFragment = PreferencesFragment()
            }
            R.id.nav_log_out -> Log.d(TAG, "Show Log Out")
        }

        if(displayFragment != null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.mainViewgroupContainer, displayFragment)
                    .addToBackStack(null)
                    .commit()
        }
    }

    companion object {
        private val TAG = "Navigation View"
    }
}
