package learn.apptivitylab.com.petrolnav.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import learn.apptivitylab.com.petrolnav.R
import kotlinx.android.synthetic.main.fragment_search.*
import learn.apptivitylab.com.petrolnav.controller.PetrolStationLoader
import learn.apptivitylab.com.petrolnav.model.PetrolStation

/**
 * Created by apptivitylab on 09/01/2018.
 */

class SearchFragment : Fragment(), SearchAdapter.StationViewHolder.onSelectStationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private var googleApiClient: GoogleApiClient? = null
    private var userLatLng: LatLng? = null
    private var petrolStationList = ArrayList<PetrolStation>()
    val petrolStationListAdapter = SearchAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (googleApiClient == null) {
            context?.let {
                googleApiClient = GoogleApiClient.Builder(it, this, this)
                        .addApi(LocationServices.API)
                        .build()
            }
        }

        val layoutManager = LinearLayoutManager(this.activity,LinearLayoutManager.VERTICAL,false)
        petrolStationListRecyclerView.layoutManager = layoutManager

        petrolStationListAdapter.setStationListener(this)
        petrolStationListRecyclerView.adapter = petrolStationListAdapter
        petrolStationListAdapter.updateDataSet(PetrolStationLoader.loadJSONStations(context!!))
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            MapDisplayFragment.LOCATION_REQUEST_CODE -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startLocationUpdates()
                }else{
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
        location?.let{
            this.userLatLng = LatLng(it.latitude, it.longitude)
        }
        this.petrolStationList = PetrolStationLoader.loadJSONStations(context!!)
        calculateDistanceFromUser(userLatLng)
        petrolStationListAdapter.updateDataSet(this.petrolStationList)
    }

    override fun onStart() {
        super.onStart()
        this.googleApiClient?.connect()
    }

    override fun onStop() {
        super.onStop()
        this.googleApiClient?.disconnect()
    }

    override fun onStationSelected(petrolStation: PetrolStation) {
        val intent = Intent(context, PetrolStationDetailActivity::class.java)
        intent.putExtra(getString(R.string.selected_station), petrolStation)
        startActivity(intent)
    }

    private fun calculateDistanceFromUser(userLatlng: LatLng?){
        userLatlng?.let {
            val userLocation = Location(getString(R.string.user_location))
            userLocation.latitude = it.latitude
            userLocation.longitude = it.longitude

            for(petrolStation in this.petrolStationList){
                val petrolStationLocation = Location(getString(R.string.petrol_station_location))

                petrolStation.petrolStationLatLng?.let {
                    petrolStationLocation.latitude = it.latitude
                    petrolStationLocation.longitude = it.longitude
                }
                val distance = userLocation.distanceTo(petrolStationLocation)/1000
                petrolStation.distanceFromUser = distance
            }
        }
    }

    companion object {
        private val TAG = "SearchFragment"
    }
}
