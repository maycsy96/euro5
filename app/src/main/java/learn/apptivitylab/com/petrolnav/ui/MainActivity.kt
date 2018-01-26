package learn.apptivitylab.com.petrolnav.ui

import android.content.Context
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
import learn.apptivitylab.com.petrolnav.model.User

/**
 * Created by apptivitylab on 09/01/2018.
 */

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private val TAG = "Navigation View"
        const val EXTRA_USER_DETAIL = "user_detail"

        fun newLaunchIntent(context: Context, user: User): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(EXTRA_USER_DETAIL, user)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val drawerToggle = ActionBarDrawerToggle(this, this.drawer_layout, this.toolbar, R.string.openDrawer, R.string.closeDrawer)
        this.drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        this.navigationView.inflateMenu(R.menu.navigation_drawer_menu)
        this.navigationView.setNavigationItemSelectedListener(this)

        val user = intent.getParcelableExtra<User>(EXTRA_USER_DETAIL)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.mainViewgroupContainer, MapDisplayFragment.newInstance(user))
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
        val user = intent.getParcelableExtra<User>(EXTRA_USER_DETAIL)
        when (id) {
            R.id.nav_map -> {
                displayFragment = MapDisplayFragment.newInstance(user)
            }
            R.id.nav_search -> {
                displayFragment = SearchFragment.newInstance(user)
            }
            R.id.nav_petrol_price -> Log.d(TAG, "Show Petrol Price")
            R.id.nav_preference -> {
                displayFragment = PreferencesFragment.newInstance(user)
            }
            R.id.nav_log_out -> Log.d(TAG, "Show Log Out")
        }

        if (displayFragment != null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.mainViewgroupContainer, displayFragment)
                    .addToBackStack(null)
                    .commit()
        }
    }
}

