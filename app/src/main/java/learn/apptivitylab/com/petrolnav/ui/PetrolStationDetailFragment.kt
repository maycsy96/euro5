package learn.apptivitylab.com.petrolnav.ui

import android.os.Bundle
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
class PetrolStationDetailFragment : Fragment(){
    private lateinit var petrolStationSelected: PetrolStation

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_petrol_station_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            petrolStationSelected = it.getParcelable(PETROL_STATION_DETAIL)
        }
        petrolStationIdTextView.text = petrolStationSelected.petrolStationId
        petrolStationNameTextView.text = petrolStationSelected.petrolStationName
        petrolStationBrandTextView.text = petrolStationSelected.petrolStationBrand
        petrolStationAddressTextView.text = petrolStationSelected.petrolStationAddress
    }

    companion object {
        private val PETROL_STATION_DETAIL = "petrol_station_detail"

        fun newInstance(petrolStation: PetrolStation):PetrolStationDetailFragment{
            val fragment = PetrolStationDetailFragment()
            val args: Bundle = Bundle()
            args.putParcelable(PETROL_STATION_DETAIL,petrolStation)
            fragment.arguments = args
            return fragment
        }
    }

}