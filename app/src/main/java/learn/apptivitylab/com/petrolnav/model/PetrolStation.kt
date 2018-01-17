package learn.apptivitylab.com.petrolnav.model

/**
 * Created by apptivitylab on 12/01/2018.
 */
data class PetrolStation(
        var id:Int,
        var name:String,
        var address:String,
        var operatingHours: List<OperatingHour>?,
        var petrol:List<Petrol>?,
        var petrolStationBrand: PetrolStationBrand?) {

}