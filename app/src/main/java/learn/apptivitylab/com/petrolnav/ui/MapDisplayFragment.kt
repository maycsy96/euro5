package learn.apptivitylab.com.petrolnav.ui

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
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
import kotlinx.android.synthetic.main.fragment_map_display.*


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
    private var currentUserLocation: LatLng?= null

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

        if (this.googleApiClient == null) {
            buildGoogleApiClient()
        }

        this.context?.let {
            this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(it)
        }

        this.centerUserButton.setOnClickListener{
            centerMapOnUserLocation()
        }
    }

    private fun centerMapOnUserLocation() {
        if(currentUserLocation != null){
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentUserLocation, 16f)
            this.googleMap?.moveCamera(cameraUpdate)
        }else{
            return
        }
    }

    private fun buildGoogleApiClient(){
        this.context?.let {
            this.googleApiClient = GoogleApiClient.Builder(it, this, this)
                    .addApi(LocationServices.API)
                    .build()
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
            val officeLatLng = LatLng(4.2105, 101.9758)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(officeLatLng, 6f)
            this.googleMap?.moveCamera(cameraUpdate)
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
        if (this.googleApiClient?.isConnected == true) {
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

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    this.googleApiClient,
                    locationRequest,
                    this
            )
        } else {
            this.view?.let{
                Snackbar.make(it, "GoogleApiClient is not ready yet", Snackbar.LENGTH_LONG).show()
            }
        }
    }

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

                    this.context?.let {
                        if(ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                            if(this.googleApiClient==null){
                                buildGoogleApiClient()
                            }
                        this.googleMap?.isMyLocationEnabled = true
                        }
                    }

                }else{
                    this.googleMap?.isMyLocationEnabled = false
                    this.view?.let{
                        Snackbar.make(it, "Unable to show current location - permission is required", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onConnected(bundle: Bundle?) {
        startLocationUpdates()
    }

    override fun onConnectionSuspended(i: Int) {
    }

   override fun onConnectionFailed(connectionResult: ConnectionResult) {
    }

   override fun onLocationChanged(location: Location) {
        val userLatLng = LatLng(location.latitude, location.longitude)
        currentUserLocation = userLatLng
        if(this.locationMarker ==null){
            val markerOptions= MarkerOptions().position(userLatLng)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            this.locationMarker = googleMap?.addMarker(markerOptions)
        }else{
            this.locationMarker?.position=userLatLng
        }

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLatLng, 16f)
        this.googleMap?.moveCamera(cameraUpdate)
    }

    companion object {
        val LOCATION_REQUEST_CODE = 100
    }

}
