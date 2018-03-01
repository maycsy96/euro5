package learn.apptivitylab.com.petrolnav.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_search.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.controller.PetrolStationLoader
import learn.apptivitylab.com.petrolnav.model.PetrolStation
import learn.apptivitylab.com.petrolnav.model.User

/**
 * Created by apptivitylab on 09/01/2018.
 */

class SearchFragment : Fragment(), SearchAdapter.StationViewHolder.SelectStationListener, SwipeRefreshLayout.OnRefreshListener {

    companion object {
        private const val ARG_USER_DETAIL = "user_detail"
        const val PREFERRED_PETROL_STATION = 1
        const val NON_PREFERRED_PETROL_STATION = 0
        fun newInstance(user: User): SearchFragment {
            val fragment = SearchFragment()
            val args: Bundle = Bundle()
            args.putParcelable(ARG_USER_DETAIL, user)
            fragment.arguments = args
            return fragment
        }
    }

    private var userLatLng: LatLng? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallBack: LocationCallback? = null

    private var petrolStationList = ArrayList<PetrolStation>()
    private var filteredListByPreferredPetrol = ArrayList<PetrolStation>()
    private val petrolStationListAdapter = SearchAdapter()
    private var user = User()
    private var currentMode = PREFERRED_PETROL_STATION

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            this.user = it.getParcelable(ARG_USER_DETAIL)
        }

        this.petrolStationList = PetrolStationLoader.petrolStationList
        this.filteredListByPreferredPetrol = this.filterByPreferredPetrol(this.petrolStationList, this.user)
        this.petrolStationListSwipeRefresh.setOnRefreshListener(this)
        val layoutManager = LinearLayoutManager(this.activity, LinearLayoutManager.VERTICAL, false)
        petrolStationListRecyclerView.layoutManager = layoutManager
        this.petrolStationListAdapter.setStationListener(this)
        petrolStationListRecyclerView.adapter = this.petrolStationListAdapter

        this.preferredStationButton.setOnClickListener {
            if (currentMode == PREFERRED_PETROL_STATION) {
                this.petrolStationListRecyclerView.smoothScrollToPosition(0)
            } else {
                this.currentMode = PREFERRED_PETROL_STATION
                this.updateButtonAppearance(this.currentMode)

                this.updateStationsAdapterDataSet(this.userLatLng, filteredListByPreferredPetrol, this.petrolStationListAdapter, this.currentMode)
            }
        }

        this.nonPreferredStationButton.setOnClickListener {
            if (currentMode == NON_PREFERRED_PETROL_STATION) {
                this.petrolStationListRecyclerView.smoothScrollToPosition(0)
            } else {
                this.currentMode = NON_PREFERRED_PETROL_STATION
                this.updateButtonAppearance(this.currentMode)

                this.updateStationsAdapterDataSet(this.userLatLng, this.filteredListByPreferredPetrol, this.petrolStationListAdapter, this.currentMode)
            }
        }
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context!!)
        this.currentMode = PREFERRED_PETROL_STATION
        this.updateButtonAppearance(this.currentMode)
        this.updateStationsAdapterDataSet(this.userLatLng, this.filteredListByPreferredPetrol, this.petrolStationListAdapter, this.currentMode)
    }

    private fun updateButtonAppearance(currentMode: Int = PREFERRED_PETROL_STATION) {
        if (currentMode == PREFERRED_PETROL_STATION) {
            with(this.preferredStationButton) {
                setTextColor(ContextCompat.getColor(this.context!!, R.color.lightGreen))
                setBackgroundResource(R.drawable.button_rounded)
            }
            with(this.nonPreferredStationButton) {
                setTextColor(ContextCompat.getColor(this.context!!, android.R.color.white))
                setBackgroundColor(ContextCompat.getColor(this.context!!, android.R.color.transparent))
            }
        } else {
            with(this.nonPreferredStationButton) {
                setTextColor(ContextCompat.getColor(this.context!!, R.color.lightGreen))
                setBackgroundResource(R.drawable.button_rounded)
            }
            with(this.preferredStationButton) {
                setTextColor(ContextCompat.getColor(this.context!!, android.R.color.white))
                setBackgroundColor(ContextCompat.getColor(this.context!!, android.R.color.transparent))
            }
        }
    }

    private fun startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.context?.let {
                if (ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(it as Activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
                    this.petrolStationListSwipeRefresh.isRefreshing = false
                    return
                }
            }
        }

        val locationRequest = LocationRequest()
        with(locationRequest) {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 15000
            fastestInterval = 10000
        }

        this.locationCallBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                locationResult?.let {
                    onLocationChanged(it.lastLocation)
                }
            }
        }

        this.fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper())
        this.updateUserLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MapDisplayFragment.LOCATION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.context?.let {
                        if (ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            this.startLocationUpdates()
                        }
                    }
                } else {
                    this.view?.let {
                        Snackbar.make(it, getString(R.string.message_unavailable_location), Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun onLocationChanged(location: Location) {
        if (location.latitude != this.userLatLng?.latitude || location.longitude != this.userLatLng?.longitude) {
            this.updateUserLocation()
        } else {
            Toast.makeText(this.context!!, getString(R.string.message_location_did_not_change), Toast.LENGTH_SHORT).show()
        }
        this.fusedLocationClient?.removeLocationUpdates(this.locationCallBack)
        this.petrolStationListSwipeRefresh.isRefreshing = false
    }

    @SuppressLint("MissingPermission")
    private fun updateUserLocation() {
        this.fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
            if (location != null) {
                this.userLatLng = LatLng(location.latitude, location.longitude)
            }
        }
        this.updateStationsAdapterDataSet(this.userLatLng, this.petrolStationList, this.petrolStationListAdapter, this.currentMode)
    }

    private fun updateStationsAdapterDataSet(userLatLng: LatLng?, petrolStationList: ArrayList<PetrolStation>, petrolStationListAdapter: SearchAdapter, isPreferred: Int) {
        if (userLatLng != null) {
            this.setStationsDistanceFromUser(userLatLng, petrolStationList)
        }
        petrolStationList.sortBy { petrolStation ->
            petrolStation.distanceFromUser
        }
        this.emptyListTextView.visibility = View.GONE
        val filteredPetrolStationList = this.filterByPreferredBrand(petrolStationList, this.user, isPreferred)
        if (filteredPetrolStationList.isEmpty()) {
            this.emptyListTextView.visibility = View.VISIBLE
            if (isPreferred == PREFERRED_PETROL_STATION) {
                this.emptyListTextView.text = getString(R.string.message_empty_list_preferred_station)
            } else {
                this.emptyListTextView.text = getString(R.string.message_empty_list_non_preferred_station)
            }
        }
        this.petrolStationListAdapter.updateDataSet(filteredPetrolStationList)
    }

    override fun onStop() {
        this.locationCallBack?.let {
            this.fusedLocationClient?.removeLocationUpdates(it)
        }
        super.onStop()
    }

    private fun setStationsDistanceFromUser(userLatLng: LatLng?, petrolStationList: ArrayList<PetrolStation>) {
        val userLocation = Location(getString(R.string.user_location))
        userLatLng?.let {
            userLocation.latitude = it.latitude
            userLocation.longitude = it.longitude
        }
        for (petrolStation in petrolStationList) {
            val petrolStationLocation = Location(getString(R.string.petrol_station_location))

            petrolStation.petrolStationLatLng?.let {
                petrolStationLocation.latitude = it.latitude
                petrolStationLocation.longitude = it.longitude
            }
            val distance = userLocation.distanceTo(petrolStationLocation) / 1000
            petrolStation.distanceFromUser = distance.toDouble()
        }
    }

    override fun onStationSelected(petrolStation: PetrolStation) {
        val launchIntent = PetrolStationDetailActivity.newLaunchIntent(this.context!!, petrolStation)
        this.startActivity(launchIntent)
    }

    private fun filterByPreferredPetrol(petrolStationList: java.util.ArrayList<PetrolStation>, user: User): java.util.ArrayList<PetrolStation> {
        var preferredPetrolStationList = java.util.ArrayList<PetrolStation>()

        petrolStationList.forEach { petrolStation ->
            petrolStation.petrolList?.forEach { petrol ->
                if (petrol.petrolId == user.userPreferredPetrol?.petrolId) {
                    preferredPetrolStationList.add(petrolStation)
                }
            }
        }
        return preferredPetrolStationList
    }

    private fun filterByPreferredBrand(petrolStationList: ArrayList<PetrolStation>, user: User, isPreferred: Int): ArrayList<PetrolStation> {
        var preferredStationList = ArrayList<PetrolStation>()
        for (petrolStation in petrolStationList) {
            user.userPreferredPetrolStationBrandList?.let {
                if (it.any { brand ->
                    brand.petrolStationBrandName == petrolStation.petrolStationBrand
                }) {
                    preferredStationList.add(petrolStation)
                }
            }
        }

        return if (isPreferred == PREFERRED_PETROL_STATION) {
            preferredStationList
        } else {
            var nonPreferredStationList = ArrayList<PetrolStation>()
            nonPreferredStationList.addAll(petrolStationList)
            nonPreferredStationList.removeAll(preferredStationList)
            nonPreferredStationList
        }
    }

    override fun onRefresh() {
        this.startLocationUpdates()
    }
}
