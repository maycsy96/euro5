package learn.apptivitylab.com.petrolnav.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.progress_bar_dialog.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.api.RestAPIClient
import learn.apptivitylab.com.petrolnav.controller.PetrolStationLoader
import learn.apptivitylab.com.petrolnav.model.PetrolStation
import learn.apptivitylab.com.petrolnav.model.User
import java.util.*

/**
 * Created by apptivitylab on 09/01/2018.
 */

class MapDisplayFragment : Fragment(), RestAPIClient.ReceiveCompleteDataListener {

    companion object {
        const val LOCATION_REQUEST_CODE = 100
        private const val ARG_USER_DETAIL = "user_detail"

        fun newInstance(user: User): MapDisplayFragment {
            val fragment = MapDisplayFragment()
            val args: Bundle = Bundle()
            args.putParcelable(ARG_USER_DETAIL, user)
            fragment.arguments = args
            return fragment
        }
    }

    private val NEAREST_STATION_COUNT = 5
    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallBack: LocationCallback? = null

    private var locationMarker: Marker? = null
    private var userLatLng: LatLng? = null

    private var petrolStationList = ArrayList<PetrolStation>()
    private var filteredListByPreferredPetrol = ArrayList<PetrolStation>()
    private var user = User()
    private var progressBarDialog: Dialog? = null

    private lateinit var clusterManager: ClusterManager<PetrolStation>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_map_display, container, false)

        if (savedInstanceState == null) {
            this.setupGoogleMapsFragment();
        } else {
            this.mapFragment = this.activity!!.supportFragmentManager.findFragmentById(R.id.mapViewgroupContainer) as SupportMapFragment
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            this.user = it.getParcelable(ARG_USER_DETAIL)
        }

        this.setupProgressBarDialog()
        if (PetrolStationLoader.petrolStationList.isEmpty()) {
            this.progressBarDialog?.show()
            PetrolStationLoader.loadJSONStations(this.context!!, this)
        } else {
            this.petrolStationList = PetrolStationLoader.petrolStationList
            this.filteredListByPreferredPetrol = this.filterByPreferredPetrol(this.petrolStationList, this.user)
            if (this.filteredListByPreferredPetrol.isEmpty()) {
                this.filteredListByPreferredPetrol = this.petrolStationList
            }
            this.createPetrolStationMarker(this.filteredListByPreferredPetrol, this.user)
        }
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context!!)
        this.startLocationUpdates()
    }

    private fun setupGoogleMapsFragment() {
        this.mapFragment = SupportMapFragment.newInstance()

        this.activity!!.supportFragmentManager
                .beginTransaction()
                .add(R.id.mapViewgroupContainer, mapFragment)
                .commit()

        this.mapFragment?.getMapAsync { googleMap ->
            this.googleMap = googleMap
            this.googleMap?.setOnInfoWindowClickListener(this)

            var typedValue = TypedValue()
            this.context!!.theme?.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)
            var toolbarHeight = resources.getDimensionPixelSize(typedValue.resourceId)
            this.googleMap?.setPadding(0, toolbarHeight, 0, 0)

            val officeLatLng = LatLng(4.2105, 101.9758)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(officeLatLng, 6f)
            this.googleMap?.moveCamera(cameraUpdate)

            this.clusterManager = ClusterManager(this.context, googleMap)
            this.petrolStationList = PetrolStationLoader.petrolStationList
            this.filteredListByPreferredPetrol = this.filterByPreferredPetrol(this.petrolStationList, this.user)
            if (this.filteredListByPreferredPetrol.isEmpty()) {
                this.filteredListByPreferredPetrol = this.petrolStationList
            }
            this@MapDisplayFragment.setUpClusterManager(this.filteredListByPreferredPetrol)

            with(googleMap) {
                setOnMapLoadedCallback {
                    if (this@MapDisplayFragment.userLatLng == null) {
                        val malaysiaLatLng = LatLng(4.2105, 101.9758)
                        animateCamera(CameraUpdateFactory.newLatLngZoom(malaysiaLatLng, 6f))
                    } else {
                        this@MapDisplayFragment.setStationsDistanceFromUser(this@MapDisplayFragment.userLatLng, this@MapDisplayFragment.petrolStationList)
                        this@MapDisplayFragment.filteredListByPreferredPetrol.sortBy { petrolStation ->
                            petrolStation.distanceFromUser
                        }
                        var boundsBuilder = LatLngBounds.Builder()
                        val nearestStationList = this@MapDisplayFragment.filteredListByPreferredPetrol.take(this@MapDisplayFragment.NEAREST_STATION_COUNT)
                        nearestStationList.forEach { nearestStation ->
                            boundsBuilder.include(nearestStation.petrolStationLatLng)
                        }
                        boundsBuilder.include(this@MapDisplayFragment.userLatLng)
                        var bounds = boundsBuilder.build()
                        animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                    }
                }
                setInfoWindowAdapter(this@MapDisplayFragment.clusterManager.markerManager)
                setOnInfoWindowClickListener(this@MapDisplayFragment.clusterManager)
                setOnCameraIdleListener(this@MapDisplayFragment.clusterManager)
                setOnMarkerClickListener(this@MapDisplayFragment.clusterManager)

                var typedValue = TypedValue()
                this@MapDisplayFragment.context!!.theme?.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)
                var toolbarHeight = resources.getDimensionPixelSize(typedValue.resourceId)
                setPadding(0, toolbarHeight, 0, 0)
            }
        }
    }

    private fun setUpClusterManager(petrolStationList: ArrayList<PetrolStation>) {
        with(this.clusterManager) {
            addItems(petrolStationList)
            setOnClusterClickListener { cluster ->
                val boundsBuilder = LatLngBounds.builder()
                cluster.items.forEach { item ->
                    boundsBuilder.include(item.position)
                }
                val bounds = boundsBuilder.build()
                this@MapDisplayFragment.googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                true
            }
            setOnClusterItemInfoWindowClickListener { petrolStation ->
                val launchIntent = PetrolStationDetailActivity.newLaunchIntent(this@MapDisplayFragment.context!!, petrolStation)
                this@MapDisplayFragment.startActivity(launchIntent)
            }
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
        with(locationRequest) {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            fastestInterval = 3000
        }

        this.locationCallBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                locationResult?.let {
                    onLocationChanged(it.lastLocation)
                }
            }
        }
        this.fusedLocationClient?.requestLocationUpdates(locationRequest, this.locationCallBack, Looper.myLooper())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.context?.let {
                        if (ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            this.googleMap?.isMyLocationEnabled = true
                            this.startLocationUpdates()
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

    @SuppressLint("MissingPermission")
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

            this.setStationsDistanceFromUser(this.userLatLng, this.petrolStationList)

            this.filteredListByPreferredPetrol.sortBy { petrolStation ->
                petrolStation.distanceFromUser
            }

            var boundsBuilder = LatLngBounds.Builder()
            val nearestStationList = this.filteredListByPreferredPetrol.take(this.NEAREST_STATION_COUNT)
            nearestStationList.forEach { nearestStation ->
                boundsBuilder.include(nearestStation.petrolStationLatLng)
            }
            boundsBuilder.include(this.userLatLng)
            var bounds = boundsBuilder.build()

            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
            this.googleMap?.animateCamera(cameraUpdate)
            this.googleMap?.isMyLocationEnabled = true
        }
    }

    private fun setStationsDistanceFromUser(userLatlng: LatLng?, petrolStationList: ArrayList<PetrolStation>) {
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

    private fun filterByPreferredPetrol(petrolStationList: ArrayList<PetrolStation>, user: User): ArrayList<PetrolStation> {
        var preferredPetrolStationList = ArrayList<PetrolStation>()

        petrolStationList.forEach { petrolStation ->
            petrolStation.petrolList?.forEach { petrol ->
                if (petrol.petrolId == user.userPreferredPetrol?.petrolId) {
                    preferredPetrolStationList.add(petrolStation)
                }
            }
        }
        return preferredPetrolStationList
    }

    private fun createPetrolStationMarker(petrolStationList: List<PetrolStation>, user: User) {
        var bitmapImage: Bitmap
        var resizedBitmapImage: Bitmap
        var petrolStationLocationMarker: Marker?
    override fun onStop() {
        this.fusedLocationClient?.removeLocationUpdates(locationCallBack)
        super.onStop()
    }

        for (nearestStation in petrolStationList) {
            var nearestStationLatLng = nearestStation.petrolStationLatLng
            user.userPreferredPetrolStationBrandList?.let {
                if (it.any { brand ->
                    brand.petrolStationBrandName == nearestStation.petrolStationBrand
                }) {
                    bitmapImage = BitmapFactory.decodeResource(resources, R.drawable.ic_petrol_station_marker)
                    resizedBitmapImage = Bitmap.createScaledBitmap(bitmapImage, 100, 100, false)
                } else {
                    bitmapImage = BitmapFactory.decodeResource(resources, R.drawable.circle_marker)
                    resizedBitmapImage = Bitmap.createScaledBitmap(bitmapImage, 50, 50, false)
                }
                nearestStationLatLng?.let {
                    var preferredStationMarkerOptions = MarkerOptions().position(it)
                            .title(nearestStation.petrolStationName)
                            .snippet(nearestStation.petrolStationId)
                            .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmapImage))
                    petrolStationLocationMarker = this.googleMap?.addMarker(preferredStationMarkerOptions)
                }
            }
        }
    }

    override fun onStop() {
        this.fusedLocationClient?.removeLocationUpdates(locationCallBack)
        super.onStop()
    }

    private fun setupProgressBarDialog() {
        this.progressBarDialog = Dialog(this.activity)
        this.progressBarDialog?.let {
            it.setContentView(R.layout.progress_bar_dialog)
            it.window.setBackgroundDrawableResource(android.R.color.transparent)
            it.progressBarTextView.text = getString(R.string.message_loading_petrol_station_data)
        }
    }

    override fun onCompleteDataReceived(dataReceived: Boolean, error: VolleyError?) {
        this.progressBarDialog?.dismiss()
        if (dataReceived || error == null) {
            this.petrolStationList = PetrolStationLoader.petrolStationList
            this.filteredListByPreferredPetrol = this.filterByPreferredPetrol(this.petrolStationList, this.user)
            if (this.filteredListByPreferredPetrol.isEmpty()) {
                this.filteredListByPreferredPetrol = this.petrolStationList
            }
            this.createPetrolStationMarker(this.filteredListByPreferredPetrol, this.user)
        } else {
            view?.let {
                Snackbar.make(it, getString(R.string.message_retrieval_data_fail), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.button_retry), {
                            this.progressBarDialog?.show()
                            PetrolStationLoader.loadJSONStations(this.context!!, this)
                        })
                        .show()
            }
        }
    }
}
