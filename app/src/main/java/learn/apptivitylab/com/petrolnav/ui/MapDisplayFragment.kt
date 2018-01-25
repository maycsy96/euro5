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
import android.view.*

import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_map_display.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.controller.PetrolStationLoader
import learn.apptivitylab.com.petrolnav.model.PetrolStation
import learn.apptivitylab.com.petrolnav.model.User
import java.util.ArrayList

/**
 * Created by apptivitylab on 09/01/2018.
 */

class MapDisplayFragment : Fragment(), OnInfoWindowClickListener {

    companion object {
        val LOCATION_REQUEST_CODE = 100
        val TAG = "MapDisplayFragment"

        private val ARG_USER_DETAIL = "user_detail"

        fun newInstance(user: User): MapDisplayFragment{
            val fragment = MapDisplayFragment()
            val args: Bundle = Bundle()
            args.putParcelable(ARG_USER_DETAIL,user)
            fragment.arguments = args
            return fragment
        }
    }

    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallBack: LocationCallback? = null

    private var locationMarker: Marker? = null
    private var userLatLng: LatLng? = null

    private var petrolStationList = ArrayList<PetrolStation>()
    private var nearestStationLocationMarker: Marker? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_map_display, container, false)

        if (savedInstanceState == null) {
            setupGoogleMapsFragment();
        } else {
            this.mapFragment = activity!!.supportFragmentManager.findFragmentById(R.id.mapViewgroupContainer) as SupportMapFragment
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.context?.let {
            this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(it)
        }

        this.centerUserButton.setOnClickListener {
            centerMapOnUserLocation()
        }

        this.petrolStationList = PetrolStationLoader.loadJSONStations(context!!)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        startLocationUpdates()
    }

    override fun onInfoWindowClick(marker: Marker?) {
        val petrolStation = this.petrolStationList.firstOrNull { it.equals(marker?.snippet) }
        petrolStation?.let {
            val launchIntent = PetrolStationDetailActivity.newLaunchIntent(this.context!!, it)
            startActivity(launchIntent)
        }
    }

    private fun centerMapOnUserLocation() {
        if (userLatLng != null) {
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLatLng, 16f)
            this.googleMap?.moveCamera(cameraUpdate)
        } else {
            return
        }
    }

    private fun setupGoogleMapsFragment() {
        this.mapFragment = SupportMapFragment.newInstance()

        activity!!.supportFragmentManager
                .beginTransaction()
                .add(R.id.mapViewgroupContainer, mapFragment)
                .commit()

        this.mapFragment?.getMapAsync { googleMap ->
            this.googleMap = googleMap
            this.googleMap?.setOnInfoWindowClickListener(this)
            val officeLatLng = LatLng(4.2105, 101.9758)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(officeLatLng, 6f)
            this.googleMap?.moveCamera(cameraUpdate)
        }
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
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000

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
            LOCATION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.context?.let {
                        if (ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            this.googleMap?.isMyLocationEnabled = true
                            startLocationUpdates()
                        }
                    }
                } else {
                    this.googleMap?.isMyLocationEnabled = false
                    this.view?.let {
                        Snackbar.make(it, getString(R.string.message_unavailable_location), Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun onLocationChanged(location: Location?) {
        location?.let {
            this.userLatLng = LatLng(it.latitude, it.longitude)
            if (this.locationMarker != null) {
                this.locationMarker?.remove()
            }
            this.userLatLng?.let {
                val markerOptions = MarkerOptions().position(it)
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                this.locationMarker = googleMap?.addMarker(markerOptions)
            }

            updatePetrolStationsDistanceFromUser(this.userLatLng, this.petrolStationList)
            this.petrolStationList.sortBy { petrolStation ->
                petrolStation.distanceFromUser
            }

            val nearestStationsCount = 5
            var nearestStationsList = this.petrolStationList.take(nearestStationsCount)

            for (nearestStation in nearestStationsList) {
                var nearestStationLatLng = nearestStation.petrolStationLatLng
                nearestStationLatLng?.let {
                    var nearestStationMarkerOptions = MarkerOptions().position(it)
                            .title(nearestStation.petrolStationName)
                            .snippet(nearestStation.petrolStationId)
                    nearestStationLocationMarker = googleMap?.addMarker(nearestStationMarkerOptions)
                }
            }
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLatLng, 16f)
            this.googleMap?.moveCamera(cameraUpdate)
        }
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

    override fun onStop() {
        fusedLocationClient?.removeLocationUpdates(locationCallBack)
        super.onStop()
    }
}
