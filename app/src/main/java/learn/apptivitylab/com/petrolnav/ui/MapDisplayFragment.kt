package learn.apptivitylab.com.petrolnav.ui

import android.content.pm.PackageManager
import android.location.Location
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

    private var mGoogleApiClient: GoogleApiClient?=null
    private var mMapFragment : SupportMapFragment?=null
    private var mGoogleMap : GoogleMap?=null

    private var mLocationMarker: Marker?=null
    private var mLocationCircle: Circle?=null
    //private var mSearchDestination: android.support.v7.SearchView?= null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_map_display, container, false)


        if(savedInstanceState==null){
            setupGoogleMapsFragment();
        }else{
            //get supportmapfragment and request notification when the map is ready to use
            mMapFragment = activity!!.supportFragmentManager.findFragmentById(R.id.fragment_map_display_vg_container) as SupportMapFragment
        }
        return view
    }

    private fun setupGoogleMapsFragment() {
        mMapFragment = SupportMapFragment.newInstance()

        activity!!.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_map_display_vg_container,mMapFragment)
                .commit()

        mMapFragment!!.getMapAsync { googleMap ->
            mGoogleMap = googleMap

            val officeLatLng = LatLng(4.2105,101.9758)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(officeLatLng,6f)

            mGoogleMap!!.moveCamera(cameraUpdate)
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Create an instance of GoogleAPIClient
        if(mGoogleApiClient==null){
            mGoogleApiClient= GoogleApiClient.Builder(context!!,this,this)
                    .addApi(LocationServices.API)
                    .build()
        }
        startLocationUpdates()

    }

    private fun startLocationUpdates() {
        if(mGoogleApiClient!!.isConnected){
            if(ActivityCompat.checkSelfPermission(context!!,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),100)
                return
            }

            val locationRequest = LocationRequest()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 5000

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    locationRequest,
                    this
            )
        }else{
            Snackbar.make(view!!,"GoogleApiClient is not ready yet", Snackbar.LENGTH_LONG).show()
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
    override fun onConnected(bundle: Bundle?) {

    }

    override fun onConnectionSuspended(i: Int) {

    }
    //endregion


    //region GoogleApiClient.OnConnectionFailedListener
    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }
    //endregion

    override fun onLocationChanged(location: Location) {
        Snackbar.make(view!!, "Location update received!", Snackbar.LENGTH_SHORT).show()
        val latitudeString = "Latitude: " + location.latitude.toString()
        val longitudeString = "Longitude: " + location.longitude.toString()
        val accuracyString = "Accuracy: " + location.accuracy.toString() + "m"

        //texrview

        val userLatLng = LatLng(location.latitude,location.longitude)

        if (mLocationMarker == null) {
            val markerOptions = MarkerOptions().position(userLatLng).title("Here I am!")
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
