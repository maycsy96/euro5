package learn.apptivitylab.com.petrolnav.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_drawer_menu_header.view.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.User


/**
 * Created by apptivitylab on 09/01/2018.
 */

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, UserListener, PlaceSelectionListener {

    companion object {
        const val EXTRA_USER_DETAIL = "user_detail"
        const val REQUEST_SELECT_PLACE = 1000
        fun newLaunchIntent(context: Context, user: User): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(EXTRA_USER_DETAIL, user)
            return intent
        }
    }

    interface SearchLocationListener {
        fun onLocationSelected(place: Place)
    }

    private var user = User()
    private lateinit var searchLocationListener: SearchLocationListener

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        try {
            this.searchLocationListener = fragment as SearchLocationListener
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getLocationData(place: Place) {
        this.searchLocationListener.onLocationSelected(place)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        this.toolbar.title = ""
        this.setSupportActionBar(this.toolbar)
        val drawerToggle = ActionBarDrawerToggle(this, this.drawer_layout, this.toolbar, R.string.openDrawer, R.string.closeDrawer)
        this.drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        with(this.navigationView) {
            inflateMenu(R.menu.navigation_drawer_menu)
            setNavigationItemSelectedListener(this@MainActivity)
        }

        this.user = intent.getParcelableExtra<User>(EXTRA_USER_DETAIL)
        val navigationViewHeader = this.navigationView.getHeaderView(0)
        with(navigationViewHeader) {
            navigationHeaderNameTextView.text = this@MainActivity.user.userName
            navigationHeaderEmailTextView.text = this@MainActivity.user.userEmail
        }

        (this.mainViewgroupContainer.layoutParams as CoordinatorLayout.LayoutParams).behavior = null
        this.mainViewgroupContainer.requestLayout()

        val autoCompleteFilter: AutocompleteFilter = AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("MY")
                .build()

        this.locationSearchTextView.setOnClickListener {
            try {
                val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                        .setFilter(autoCompleteFilter)
                        .build(this@MainActivity)
                this.startActivityForResult(intent, REQUEST_SELECT_PLACE)
            } catch (e: Exception) {
                when (e) {
                    is GooglePlayServicesRepairableException, is GooglePlayServicesNotAvailableException -> e.printStackTrace()
                    else -> throw e
                }
            }
        }

        this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.mainViewgroupContainer, MapDisplayFragment.newInstance(this.user))
                .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MainActivity.REQUEST_SELECT_PLACE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlaceAutocomplete.getPlace(this, data)
                this.onPlaceSelected(place)
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                val status = PlaceAutocomplete.getStatus(this, data)
                this.onError(status)
            }
        }
    }

    override fun onPlaceSelected(place: Place) {
        this.getLocationData(place)
    }

    override fun onError(status: Status?) {
        Toast.makeText(this, "Place selection failed: " + status.toString(), Toast.LENGTH_LONG)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        this.navigateTo(item.itemId)
        this.drawer_layout?.closeDrawers()
        return true
    }

    override fun onBackPressed() {
        this.toolbar.title = ""
        var currentFragment: Fragment? = this.supportFragmentManager.findFragmentById(R.id.mainViewgroupContainer)
        (this.mainViewgroupContainer.layoutParams as CoordinatorLayout.LayoutParams).behavior = null
        if (currentFragment is MapDisplayFragment) {
            this.showLogOutDialog()
        } else {
            this.locationSearchAutoComplete.visibility = View.VISIBLE
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
        val currentFragment: Fragment? = this.supportFragmentManager.findFragmentById(R.id.mainViewgroupContainer)

        this.locationSearchAutoComplete.visibility = View.GONE
        (this.mainViewgroupContainer.layoutParams as CoordinatorLayout.LayoutParams).behavior = AppBarLayout.ScrollingViewBehavior()
        this.mainViewgroupContainer.requestLayout()
        when (id) {
            R.id.nav_map -> {
                this.toolbar.title = ""
                this.locationSearchAutoComplete.visibility = View.VISIBLE
                (this.mainViewgroupContainer.layoutParams as CoordinatorLayout.LayoutParams).behavior = null
                this.mainViewgroupContainer.requestLayout()

                if (currentFragment is MapDisplayFragment) {
                    return
                }
                displayFragment = MapDisplayFragment.newInstance(this.user)
            }
            R.id.nav_search -> {
                this.toolbar.title = getString(R.string.title_station_list)
                this.locationSearchAutoComplete.text = null
                displayFragment = SearchFragment.newInstance(this.user)
            }
            R.id.nav_petrol_price -> {
                if (currentFragment is MapDisplayFragment) {
                    this.locationSearchAutoComplete.visibility = View.VISIBLE
                    this.locationSearchAutoComplete.text = null
                    (this.mainViewgroupContainer.layoutParams as CoordinatorLayout.LayoutParams).behavior = null
                    this.mainViewgroupContainer.requestLayout()
                }
                val launchIntent = PetrolPriceActivity.newLaunchIntent(this)
                this.startActivity(launchIntent)
            }
            R.id.nav_preference -> {
                this.toolbar.title = getString(R.string.title_preference)
                this.locationSearchAutoComplete.text = null
                displayFragment = PreferencesFragment.newInstance(this.user)
            }
            R.id.nav_log_out -> {
                this.showLogOutDialog()
            }
        }

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

