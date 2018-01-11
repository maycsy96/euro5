package learn.apptivitylab.com.petrolnav.ui

import android.location.LocationListener
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.Marker
import learn.apptivitylab.com.petrolnav.R


/**
 * Created by apptivitylab on 09/01/2018.
 */

class MapDisplayFragment : Fragment(),GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private var mGoogleApiClient: GoogleApiClient?=null
    private var mMapFragment : SupportMapFragment?=null
    private var mGoogleMap : GoogleMap?=null

    private var mLocationMarker: Marker?=null
    private var mLocationCircle: Circle?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View? = inflater.inflate(R.layout.fragment_map_display, container, false)

        /*
        if(savedInstanceState==null){
            setupGoogleMapsFragment();
        }else{
            mMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById()
        }
*/
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //testClassObject = TestClass(10.0)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        //inflater?.inflate(R.menu.whatever_name_your_menu_is, menu)

    }

    companion object {
        val TAG = "MapDisplayFragment"
        val DISPLAY_MAP = "displaymap"
    }
}
