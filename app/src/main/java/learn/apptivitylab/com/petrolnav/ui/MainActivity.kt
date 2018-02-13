package learn.apptivitylab.com.petrolnav.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_drawer_menu_header.view.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.controller.PetrolStationLoader
import learn.apptivitylab.com.petrolnav.model.PetrolStation
import learn.apptivitylab.com.petrolnav.model.User

/**
 * Created by apptivitylab on 09/01/2018.
 */

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, UserListener {

    companion object {
        const val EXTRA_USER_DETAIL = "user_detail"

        fun newLaunchIntent(context: Context, user: User): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(EXTRA_USER_DETAIL, user)
            return intent
        }
    }

    private var user = User()
    private var petrolStationList = ArrayList<PetrolStation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        this.setSupportActionBar(this.toolbar)

        val drawerToggle = ActionBarDrawerToggle(this, this.drawer_layout, this.toolbar, R.string.openDrawer, R.string.closeDrawer)
        this.drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        this.navigationView.inflateMenu(R.menu.navigation_drawer_menu)
        this.navigationView.setNavigationItemSelectedListener(this)

        this.user = intent.getParcelableExtra<User>(EXTRA_USER_DETAIL)
        this.petrolStationList = PetrolStationLoader.loadJSONStations(this)

        val navigationViewHeader = this.navigationView.getHeaderView(0)
        navigationViewHeader.navigationHeaderNameTextView.text = this.user.userName
        navigationViewHeader.navigationHeaderEmailTextView.text = this.user.userEmail

        this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.mainViewgroupContainer, MapDisplayFragment.newInstance(this.user, this.petrolStationList))
                .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        this.navigateTo(item.itemId)
        this.drawer_layout?.closeDrawers()
        return true
    }

    override fun onBackPressed() {
        var currentFragment: Fragment? = this.supportFragmentManager.findFragmentById(R.id.mainViewgroupContainer)

        if (currentFragment is MapDisplayFragment) {
            this.showLogOutDialog()
        } else {
            this.supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
        }
    }

    private fun showLogOutDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_log_out))
                .setMessage(getString(R.string.message_confirm_log_out))
                .setPositiveButton(getString(R.string.button_yes), { dialog, which ->
                    val launchIntent = Intent(this, LoginActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    this.startActivity(launchIntent)
                    this.finish()
                })
                .setNegativeButton(getString(R.string.button_no), null)
                .show()
    }

    private fun navigateTo(id: Int) {
        var displayFragment: Fragment? = null
        var currentFragment: Fragment? = this.supportFragmentManager.findFragmentById(R.id.mainViewgroupContainer)
        when (id) {
            R.id.nav_map -> {
                this.locationSearchView.visibility = View.VISIBLE
                if (currentFragment is MapDisplayFragment) {
                    return
                }
                displayFragment = MapDisplayFragment.newInstance(this.user, this.petrolStationList)
            }
            R.id.nav_search -> {
                displayFragment = SearchFragment.newInstance(this.user, this.petrolStationList)
            }
            R.id.nav_petrol_price -> {
                val launchIntent = PetrolPriceActivity.newLaunchIntent(this)
                this.startActivity(launchIntent)
            }
            R.id.nav_preference -> {
                displayFragment = PreferencesFragment.newInstance(this.user)
            }
            R.id.nav_log_out -> {
                this.showLogOutDialog()
            }
        }

        this.locationSearchView.visibility = View.INVISIBLE
        if (displayFragment != null) {
            this.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.mainViewgroupContainer, displayFragment)
                    .addToBackStack(null)
                    .commit()
        }
    }

    override fun onUpdateUser(user: User) {
        this.user = user
    }
}

