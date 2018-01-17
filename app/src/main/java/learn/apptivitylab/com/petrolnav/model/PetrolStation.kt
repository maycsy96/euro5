package learn.apptivitylab.com.petrolnav.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

/**
 * Created by apptivitylab on 12/01/2018.
 */
data class PetrolStation(var petrolStationId : String? = null,
                         var petrolStationName : String? = null,
                         var petrolStationBrand: String? = null,
                         var petrolStationAddress : String? = null,
                         var petrolStationLatLng : LatLng? = null)
{

//        var petrolStationOperatingHour: List<OperatingHour>?,
//        var petrol:List<Petrol>?,
//        var petrolStationBrand: PetrolStationBrand)

    constructor(jsonObject : JSONObject):this(){
        this.petrolStationId = jsonObject.optString("ID:")
        this.petrolStationName = jsonObject.optString("Name:")
        this.petrolStationBrand = jsonObject.optString("Brand:")
        this.petrolStationAddress = jsonObject.optString("Address")
        this.petrolStationLatLng = LatLng(jsonObject.optDouble("Latitude"),jsonObject.optDouble("Longitude"))
    }

}