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
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_map_display.*
import learn.apptivitylab.com.petrolnav.R

/**
 * Created by apptivitylab on 09/01/2018.
 */

class MapDisplayFragment : Fragment() {

    companion object {
        val LOCATION_REQUEST_CODE = 100
    }

    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallBack: LocationCallback? = null

    private var locationMarker: Marker? = null
    private var userLatLng: LatLng?= null

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

        this.centerUserButton.setOnClickListener{
            centerMapOnUserLocation()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        startLocationUpdates()
    }

    private fun centerMapOnUserLocation() {
        if (userLatLng != null){
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLatLng, 16f)
            this.googleMap?.moveCamera(cameraUpdate)
        }else{
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

        createLocationCallBack()
        fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper())
    }

    private fun createLocationCallBack(){
        locationCallBack = object: LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                locationResult?.let{
                    onLocationChanged(it.lastLocation)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            LOCATION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    this.context?.let {
                        if (ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                            this.googleMap?.isMyLocationEnabled = true
                            startLocationUpdates()
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

   private fun onLocationChanged(location: Location) {
       userLatLng = LatLng(location.latitude, location.longitude)
       if (this.locationMarker == null){
           userLatLng?.let{
               val markerOptions= MarkerOptions().position(it)
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
               this.locationMarker = googleMap?.addMarker(markerOptions)
           }
       }else{
           this.locationMarker?.let{
               it.position = this.userLatLng
           }
       }
       val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLatLng, 16f)
       this.googleMap?.moveCamera(cameraUpdate)
    }

    override fun onStop(){
        fusedLocationClient?.removeLocationUpdates(locationCallBack)
        super.onStop()
    }
}
