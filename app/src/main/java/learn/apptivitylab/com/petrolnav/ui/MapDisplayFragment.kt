package learn.apptivitylab.com.petrolnav.ui

import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
//import android.view.LayoutInflater
import android.view.*


import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
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

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mMapFragment: SupportMapFragment? = null
    private var mGoogleMap: GoogleMap? = null

    private var mLocationMarker: Marker? = null
    private var mLocationCircle: Circle? = null
    //private var mSearchDestination: android.support.v7.SearchView?= null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_map_display, container, false)


        if (savedInstanceState == null) {
            setupGoogleMapsFragment();
        } else {
            //get supportmapfragment and request notification when the map is ready to use
            mMapFragment = activity!!.supportFragmentManager.findFragmentById(R.id.fragment_map_display_vg_container) as SupportMapFragment
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Create an instance of GoogleAPIClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(context!!, this, this)
                    .addApi(LocationServices.API)
                    .build()

//            mGoogleApiClient?.connect()
        }
    }

    //putting the google map into the map fragment
    private fun setupGoogleMapsFragment() {
        mMapFragment = SupportMapFragment.newInstance()

        activity!!.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_map_display_vg_container, mMapFragment)
                .commit()

        mMapFragment!!.getMapAsync { googleMap ->
            mGoogleMap = googleMap

            //location of Malaysia
            val officeLatLng = LatLng(4.2105, 101.9758)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(officeLatLng, 6f)

            mGoogleMap!!.moveCamera(cameraUpdate)
        }

    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient?.connect()

    }

    override fun onStop() {
        super.onStop()
        mGoogleApiClient?.disconnect()
    }

    override fun onPause(){
        super.onPause()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
//        if(mRequestingLocationUpdates){
//            startLocationUpdates()
//        }
    }
    private fun startLocationUpdates() {
        if (mGoogleApiClient!!.isConnected) {
            //checking build version equals or more than the marshmallow build version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //request for permission
                    ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
                    return
                }
            }

            //create location request object
            val locationRequest = LocationRequest()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 5000

            //request location updates from FusedLocationApi
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
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
                mGoogleApiClient,
                this@MapDisplayFragment
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100
                && grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
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

        if (mLocationMarker == null) {
            val markerOptions = MarkerOptions().position(userLatLng)
            mLocationMarker = mGoogleMap!!.addMarker(markerOptions)
        } else {
            mLocationMarker!!.position = userLatLng
        }

        if (mLocationCircle == null) {
            val circleOptions = CircleOptions().center(userLatLng).radius(location.accuracy.toDouble())
            mLocationCircle = mGoogleMap!!.addCircle(circleOptions)
        } else {
            mLocationCircle!!.center = userLatLng
            mLocationCircle!!.radius = location.accuracy.toDouble()
        }

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLatLng, 16f)
        mGoogleMap!!.moveCamera(cameraUpdate)

    }

    companion object {
        val TAG = "MapDisplayFragment"
        val DISPLAY_MAP = "displaymap"
    }
}
