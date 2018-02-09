package learn.apptivitylab.com.petrolnav.ui

import learn.apptivitylab.com.petrolnav.model.PetrolStation

/**
 * Created by apptivitylab on 09/02/2018.
 */
interface PetrolStationListListener {
    fun onUpdatePetrolStationList(petrolStationList : ArrayList<PetrolStation>)
}