package learn.apptivitylab.com.petrolnav.ui

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
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import learn.apptivitylab.com.petrolnav.R
import kotlinx.android.synthetic.main.fragment_search.*
import learn.apptivitylab.com.petrolnav.controller.PetrolStationLoader
import learn.apptivitylab.com.petrolnav.model.PetrolStation
import java.util.*

/**
 * Created by apptivitylab on 09/01/2018.
 */

class SearchFragment : Fragment(), SearchAdapter.StationViewHolder.onSelectStationListener {

    companion object {
        private val TAG = "SearchFragment"
    }

    private var userLatLng: LatLng? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallBack: LocationCallback? = null

    private var petrolStationList = ArrayList<PetrolStation>()
    val petrolStationListAdapter = SearchAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(this.activity, LinearLayoutManager.VERTICAL, false)
        petrolStationListRecyclerView.layoutManager = layoutManager

        petrolStationListAdapter.setStationListener(this)
        petrolStationListRecyclerView.adapter = petrolStationListAdapter
        petrolStationListAdapter.updateDataSet(PetrolStationLoader.loadJSONStations(context!!))

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.context?.let {
                if (ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(it as Activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
                    return
                }
            }
        }

        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 15000
        locationRequest.fastestInterval = 10000

        locationCallBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                locationResult?.let {
                    onLocationChanged(it.lastLocation)
                }
            }
        }

        fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MapDisplayFragment.LOCATION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.context?.let {
                        if (ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            startLocationUpdates()
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
        location?.let {
            this.userLatLng = LatLng(it.latitude, it.longitude)
        }
        this.petrolStationList = PetrolStationLoader.loadJSONStations(context!!)
        updatePetrolStationsDistanceFromUser(userLatLng,this.petrolStationList)
        this.petrolStationList.sortBy { petrolStation ->
            petrolStation.distanceFromUser
        }
        this.petrolStationListAdapter.updateDataSet(this.petrolStationList)
    }

    override fun onStop() {
        this.fusedLocationClient?.removeLocationUpdates(locationCallBack)
        super.onStop()
    }

    override fun onStationSelected(petrolStation: PetrolStation) {
        val launchIntent = PetrolStationDetailActivity.newLaunchIntent(this.context!!, petrolStation)
        startActivity(launchIntent)
    }

    fun updatePetrolStationsDistanceFromUser(userLatlng: LatLng?, petrolStationList: ArrayList<PetrolStation>) {
        val userLocation = Location(getString(R.string.user_location))
        userLatlng?.let {
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
}
