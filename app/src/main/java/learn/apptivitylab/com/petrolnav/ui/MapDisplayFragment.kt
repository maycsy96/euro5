package learn.apptivitylab.com.petrolnav.ui

import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
//import android.view.LayoutInflater
import android.view.*


import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


import learn.apptivitylab.com.petrolnav.R

/**
 * Created by apptivitylab on 09/01/2018.
 */

class MapDisplayFragment : Fragment(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private var googleApiClient: GoogleApiClient? = null
    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null
    private var fusedLocationClient: FusedLocationProviderClient? =null

    private var locationMarker: Marker? = null

    //private var mSearchDestination: android.support.v7.SearchView?= null
    private var centerMapOnUserBtn: FloatingActionButton? = null

    //not UI member
    private var currentUserLocation: LatLng?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_map_display, container, false)
        this.centerMapOnUserBtn = rootView.findViewById<View>(R.id.fragment_map_display_btn_center_user) as FloatingActionButton

        if (savedInstanceState == null) {
            setupGoogleMapsFragment();
        } else {
            //get supportmapfragment and request notification when the map is ready to use
            this.mapFragment = activity!!.supportFragmentManager.findFragmentById(R.id.fragment_map_display_vg_container) as SupportMapFragment
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Create an instance of GoogleAPIClient
        if (this.googleApiClient == null) {
            buildGoogleApiClient()
        }
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context!!)
        this.centerMapOnUserBtn!!.setOnClickListener{
            centerMapOnUserLocation()
        }
    }

    private fun centerMapOnUserLocation() {
        //this if condition is for checking "if there is no user location, this button will not work instead of crash the app"
        if(currentUserLocation != null){
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentUserLocation, 16f)
            this.googleMap!!.moveCamera(cameraUpdate)
        }else{
            return
        }
    }

    private fun buildGoogleApiClient(){
        this.googleApiClient = GoogleApiClient.Builder(context!!, this, this)
                .addApi(LocationServices.API)
                .build()
    }
    //putting the google map into the map fragment
    private fun setupGoogleMapsFragment() {
        this.mapFragment = SupportMapFragment.newInstance()

        activity!!.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_map_display_vg_container, mapFragment)
                .commit()

        this.mapFragment!!.getMapAsync { googleMap ->
            this.googleMap = googleMap
            val officeLatLng = LatLng(4.2105, 101.9758)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(officeLatLng, 6f)
            this.googleMap!!.moveCamera(cameraUpdate)
        }
    }

    override fun onStart() {
        super.onStart()
        this.googleApiClient?.connect()
    }

    override fun onStop() {
        super.onStop()
        this.googleApiClient?.disconnect()
    }

    override fun onPause(){
        super.onPause()
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (this.googleApiClient!!.isConnected) {

            //Requesting runtime location permission. Only for android build 6.0 version above
            //checking build version equals or more than the marshmallow build version (6.0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this.context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //request for permission
                    ActivityCompat.requestPermissions(this.activity!!, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
                    return
                }
            }

            //create location request object
            val locationRequest = LocationRequest()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 5000

            //request location updates from FusedLocationApi
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    this.googleApiClient,
                    locationRequest,
                    this
            )
        } else {
            Snackbar.make(view!!, "GoogleApiClient is not ready yet", Snackbar.LENGTH_LONG).show()
        }
    }

    //stop updating the location
    private fun stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                this.googleApiClient,
                this@MapDisplayFragment
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            LOCATION_REQUEST_CODE -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this.context!!, android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        if(this.googleApiClient==null){
                            buildGoogleApiClient()
                        }
                        this.googleMap!!.isMyLocationEnabled = true
                    }
                }else{
                    this.googleMap!!.isMyLocationEnabled = false
                    Snackbar.make(view!!, "Unable to show current location - permission is required", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    //region GoogleApiClient.ConnectionCallbacks
    //This callback will have a public function onConnected() which will be called whenever device is connected and disconnected.
    override fun onConnected(bundle: Bundle?) {
        startLocationUpdates()
    }

    override fun onConnectionSuspended(i: Int) {
    }
    //endregion


    //region GoogleApiClient.OnConnectionFailedListener
    //Provides callbacks for scenarios that result in a failed attempt to connect the client to the service. Whenever connection is failed onConnectionFailed() will be called.
    override fun onConnectionFailed(connectionResult: ConnectionResult) {
    }
    //endregion

    //LocationListener: This callback will be called whenever there is change in location of device. Function onLocationChanged() will be called.
    override fun onLocationChanged(location: Location) {
        val userLatLng = LatLng(location.latitude, location.longitude)
        currentUserLocation = userLatLng
        if(this.locationMarker ==null){
            val markerOptions= MarkerOptions().position(userLatLng)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            this.locationMarker = googleMap!!.addMarker(markerOptions)
        }else{
            this.locationMarker!!.position=userLatLng
        }

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLatLng, 16f)
        this.googleMap!!.moveCamera(cameraUpdate)
    }

    companion object {
        val LOCATION_REQUEST_CODE = 100
    }

}
