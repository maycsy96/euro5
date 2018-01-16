package learn.apptivitylab.com.petrolnav.model

/**
 * Created by apptivitylab on 16/01/2018.
 */
data class PetrolStationBrand (
        var id:String,
        var name:String,
        var petrolList:List<Petrol>?,
        var petrolStationList:List<PetrolStation>?){

}