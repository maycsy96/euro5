package learn.apptivitylab.com.petrolnav.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_petrol_station_detail.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.PetrolStation

/**
 * Created by apptivitylab on 17/01/2018.
 */
class PetrolStationDetailFragment : Fragment() {

    companion object {
        private val PETROL_STATION_DETAIL = "petrol_station_detail"

        fun newInstance(petrolStation: PetrolStation): PetrolStationDetailFragment {
            val fragment = PetrolStationDetailFragment()
            val args: Bundle = Bundle()
            args.putParcelable(PETROL_STATION_DETAIL, petrolStation)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var petrolStationSelected: PetrolStation

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_petrol_station_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            petrolStationSelected = it.getParcelable(PETROL_STATION_DETAIL)
        }
        this.petrolStationIdTextView.text = petrolStationSelected.petrolStationId
        this.petrolStationNameTextView.text = petrolStationSelected.petrolStationName
        this.petrolStationBrandTextView.text = petrolStationSelected.petrolStationBrand
        this.petrolStationAddressTextView.text = petrolStationSelected.petrolStationAddress

        if (petrolStationSelected.distanceFromUser != null) {
            this.petrolStationDistanceTextView.text = "%.2f".format(petrolStationSelected.distanceFromUser)
        } else {
            this.petrolStationDistanceTextView.text = context?.getString(R.string.message_unavailable_distance)
        }

        this.petrolTextView.text = petrolStationSelected.petrolList
                ?.map { it.petrolName }
                ?.joinToString(" , ")

        this.navigationButton.setOnClickListener{
            navigateToPetrolStation(this.petrolStationSelected)
        }
    }

    fun navigateToPetrolStation(petrolStation: PetrolStation){
        petrolStation.petrolStationLatLng?.let{
            val locationUri = Uri.parse("geo:0,0?q=${it.latitude},${it.longitude}")
            val navigateIntent = Intent(Intent.ACTION_VIEW, locationUri)
            val packageManager = context!!.packageManager
            val availableApps = packageManager.queryIntentActivities(navigateIntent, PackageManager.MATCH_DEFAULT_ONLY)
            val isIntentSafe = availableApps.size > 0
            val chooser = Intent.createChooser(navigateIntent, getString(R.string.navigate))
            if(isIntentSafe){
                startActivity(chooser)
            }else{
                this.view?.let{
                    Snackbar.make(it, getString(R.string.message_unavailable_location), Snackbar.LENGTH_LONG)
                }
            }
        }
    }
}