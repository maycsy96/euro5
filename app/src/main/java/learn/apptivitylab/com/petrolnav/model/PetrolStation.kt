package learn.apptivitylab.com.petrolnav.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

/**
 * Created by apptivitylab on 12/01/2018.
 */
data class PetrolStation(
        var petrolStationId:String,
        var petrolStationName:String,
        var petrolStationAddress:String,
        var petrolStationLatLng:LatLng)
//        var petrolStationOperatingHour: List<OperatingHour>?,
//        var petrol:List<Petrol>?,
//        var petrolStationBrand: PetrolStationBrand)
{

    constructor(jsonObject : JSONObject){
        this.petrolStationId = jsonObject.optString("ID:")
        this.petrolStationName = jsonObject.optString("Name:")
        this.petrolStationAddress = jsonObject.optString("Address")
        this.petrolStationLatLng = LatLng(jsonObject.optDouble("Latitude"),jsonObject.optDouble("Longitude"))
    }

}