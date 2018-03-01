package learn.apptivitylab.com.petrolnav.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.progress_bar_dialog.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.api.RestAPIClient
import learn.apptivitylab.com.petrolnav.controller.PetrolStationLoader
import learn.apptivitylab.com.petrolnav.model.PetrolStation
import learn.apptivitylab.com.petrolnav.model.User

/**
 * Created by apptivitylab on 09/01/2018.
 */

class MapDisplayFragment : Fragment(), RestAPIClient.ReceiveCompleteDataListener, MainActivity.SearchLocationListener {

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

    private var locationMarker: Marker? = null
    private var userLatLng: LatLng? = null
    private var placeLatLng: LatLng? = null

    private var petrolStationList = ArrayList<PetrolStation>()
    private var filteredListByPreferredPetrol = ArrayList<PetrolStation>()
    private var user = User()
    private var progressBarDialog: Dialog? = null

    private lateinit var clusterManager: ClusterManager<PetrolStation>
    private lateinit var clusterRenderer: PetrolStationMarkerRenderer

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

        this.startLocationUpdates()
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context!!)

        if (PetrolStationLoader.petrolStationList.isEmpty()) {
            this.progressBarDialog?.show()
            PetrolStationLoader.loadJSONStations(this.context!!, this)
        } else {
            this.setupPetrolStationList(PetrolStationLoader.petrolStationList)
        }
    }

    private fun setupPetrolStationList(petrolStationList: ArrayList<PetrolStation>) {
        this.petrolStationList = petrolStationList
        this.filteredListByPreferredPetrol = this.filterByPreferredPetrol(this.petrolStationList, this.user)
        if (this.filteredListByPreferredPetrol.isEmpty()) {
            this.filteredListByPreferredPetrol = this.petrolStationList
        }
    }

    private fun moveCamera(selectedLatLng: LatLng?, petrolStationList: ArrayList<PetrolStation>) {
        this.locationMarker?.let {
            it.remove()
        }
        selectedLatLng?.let {
            val markerOptions = MarkerOptions().position(it)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            this.locationMarker = googleMap?.addMarker(markerOptions)
        }

        this.setStationsDistanceFromUser(selectedLatLng, petrolStationList)
        petrolStationList.sortBy { petrolStation ->
            petrolStation.distanceFromUser
        }

        var boundsBuilder = LatLngBounds.Builder()
        val nearestStationList = petrolStationList.take(this.NEAREST_STATION_COUNT)
        nearestStationList.forEach { nearestStation ->
            boundsBuilder.include(nearestStation.petrolStationLatLng)
        }
        boundsBuilder.include(selectedLatLng)
        var bounds = boundsBuilder.build()
        this.googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun setupGoogleMapsFragment() {
        this.mapFragment = SupportMapFragment.newInstance()

        this.activity!!.supportFragmentManager
                .beginTransaction()
                .add(R.id.mapViewgroupContainer, mapFragment)
                .commit()

        this.mapFragment?.getMapAsync { googleMap ->
            this.googleMap = googleMap

            this.clusterManager = ClusterManager(this.context, googleMap)
            this.clusterRenderer = PetrolStationMarkerRenderer(this.context, googleMap, this.clusterManager)
            this@MapDisplayFragment.setupClusterManager(this.filteredListByPreferredPetrol)

            with(googleMap) {
                setOnMapLoadedCallback {
                    if (this@MapDisplayFragment.userLatLng == null) {
                        val malaysiaLatLng = LatLng(4.2105, 101.9758)
                        animateCamera(CameraUpdateFactory.newLatLngZoom(malaysiaLatLng, 6f))
                    }
                }

                this@MapDisplayFragment.context?.let {
                    if (ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        isMyLocationEnabled = true
                        setOnMyLocationButtonClickListener {
                            this@MapDisplayFragment.updateUserLocation()
                            true
                        }
                        if (this@MapDisplayFragment.petrolStationList.isNotEmpty()) {
                            this@MapDisplayFragment.updateUserLocation()
                        }
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

    private fun setupClusterManager(petrolStationList: ArrayList<PetrolStation>) {
        with(this.clusterManager) {
            addItems(petrolStationList)
            renderer = this@MapDisplayFragment.clusterRenderer
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
                    requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
                } else {
                    googleMap?.isMyLocationEnabled = true
                    if (this.petrolStationList.isNotEmpty()) {
                        this.updateUserLocation()
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.context?.let {
                        this.googleMap?.isMyLocationEnabled = true
                        if (this.petrolStationList.isNotEmpty()) {
                            this.updateUserLocation()
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
    private fun updateUserLocation() {
        this.fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
            location?.let {
                if (this.userLatLng == null) {
                    this.userLatLng = LatLng(it.latitude, it.longitude)
                }
                this.activity!!.locationSearchAutoComplete.text = null
                this.moveCamera(this.userLatLng, this.filteredListByPreferredPetrol)
                this.googleMap?.isMyLocationEnabled = true
            }
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


    inner class PetrolStationMarkerRenderer(context: Context?, googleMap: GoogleMap?, clusterManager: ClusterManager<PetrolStation>?) : DefaultClusterRenderer<PetrolStation>(context, googleMap, clusterManager) {
        override public fun onBeforeClusterItemRendered(item: PetrolStation, markerOptions: MarkerOptions) {
            this@MapDisplayFragment.user.userPreferredPetrolStationBrandList?.let {
                if (it.any { brand ->
                    brand.petrolStationBrandName == item.petrolStationBrand
                }) {
                    markerOptions
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.preferred_petrol_station_marker))
                } else {
                    markerOptions
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.non_preferred_petrol_station_marker))
                }
                markerOptions
                        .title(item.title)
                        .position(item.position)
            }
            super.onBeforeClusterItemRendered(item, markerOptions)
        }
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
            this.setupPetrolStationList(PetrolStationLoader.petrolStationList)
            this.setupClusterManager(this.filteredListByPreferredPetrol)
            this.context?.let {
                if (ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    this.updateUserLocation()
                }
            }
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

    override fun onLocationSelected(place: Place) {
        this.activity!!.locationSearchAutoComplete.setText(place.name)
        this.placeLatLng = place.latLng
        this.moveCamera(this.placeLatLng, this.filteredListByPreferredPetrol)
    }
}
