package learn.apptivitylab.com.petrolnav.model

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by apptivitylab on 12/01/2018.
 */
data class PetrolStation (var petrolStationId : String? = null,
                         var petrolStationName : String? = null,
                         var petrolStationBrand: String? = null,
                         var petrolStationAddress : String? = null,
                         var petrolStationLatLng : LatLng? = null) : Parcelable
{

    constructor(parcel: Parcel):this(){
        petrolStationId = parcel.readString()
        petrolStationName = parcel.readString()
        petrolStationBrand = parcel.readString()
        petrolStationAddress = parcel.readString()
        petrolStationLatLng = parcel.readParcelable(LatLng::class.java.classLoader)
    }

    constructor(jsonObject : JSONObject):this(){
        this.petrolStationId = jsonObject.optString("ID:")
        this.petrolStationName = jsonObject.optString("Name:")
        this.petrolStationBrand = jsonObject.optString("Brand:")
        this.petrolStationAddress = jsonObject.optString("Address")
        this.petrolStationLatLng = LatLng(jsonObject.optDouble("Latitude"),jsonObject.optDouble("Longitude"))
    }

    fun toJsonObject():JSONObject{
        var jsonStation = JSONObject()
        try{
            jsonStation.put("petrol_station_id", this.petrolStationId)
            jsonStation.put("petrol_station_name", this.petrolStationName)
            jsonStation.put("petrol_station_brand", this.petrolStationBrand)
            jsonStation.put("petrol_station_address", this.petrolStationAddress)
            jsonStation.put("petrol_station_latlng",this.petrolStationLatLng)
        }catch (e:JSONException){
            e.printStackTrace()
        }
        return jsonStation
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(petrolStationId)
        parcel?.writeString(petrolStationName)
        parcel?.writeString(petrolStationBrand)
        parcel?.writeString(petrolStationAddress)
        parcel?.writeParcelable(petrolStationLatLng,flags)
    }

    companion object CREATOR : Parcelable.Creator<PetrolStation>{
        override fun createFromParcel(parcel: Parcel): PetrolStation {
            return PetrolStation(parcel)
        }

        override fun newArray(size: Int): Array<PetrolStation?> {
            return arrayOfNulls(size)
        }
    }
}