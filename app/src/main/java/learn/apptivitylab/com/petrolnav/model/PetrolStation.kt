package learn.apptivitylab.com.petrolnav.model

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by apptivitylab on 12/01/2018.
 */
data class PetrolStation(var petrolStationId: String? = null,
                         var petrolStationName: String? = null,
                         var petrolStationBrand: String? = null,
                         var petrolStationAddress: String? = null,
                         var petrolStationLatLng: LatLng? = null,
                         var distanceFromUser: Double? = null,
                         var petrolList: ArrayList<Petrol>? = null) : Parcelable {

    companion object CREATOR : Parcelable.Creator<PetrolStation> {
        override fun createFromParcel(parcel: Parcel): PetrolStation {
            return PetrolStation(parcel)
        }

        override fun newArray(size: Int): Array<PetrolStation?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this() {
        petrolStationId = parcel.readString()
        petrolStationName = parcel.readString()
        petrolStationBrand = parcel.readString()
        petrolStationAddress = parcel.readString()
        petrolStationLatLng = parcel.readParcelable(LatLng::class.java.classLoader)
        distanceFromUser = parcel.readDouble()
        petrolList = parcel.readArrayList(Petrol::class.java.classLoader) as ArrayList<Petrol>
    }

    constructor(jsonObject: JSONObject) : this() {
        this.petrolStationId = jsonObject.optString("uuid")
        this.petrolStationName = jsonObject.optString("name")
        this.petrolStationBrand = jsonObject.optJSONObject("companies_by_company_uuid").optString("name")

        //TODO no address
        this.petrolStationAddress = jsonObject.optString("petrol_station_address")
        this.petrolStationLatLng = LatLng(jsonObject.optDouble("latitude"), jsonObject.optDouble("longitude"))

        var petrol: Petrol
        var petrolListJsonArray = jsonObject.optJSONArray("petrols_by_station_petrols")
        this.petrolList = ArrayList<Petrol>()
        for (i in 0..petrolListJsonArray.length() - 1) {
            try {
                petrol = Petrol(petrolListJsonArray?.getJSONObject(i))
                this.petrolList?.add(petrol)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(petrolStationId)
        parcel?.writeString(petrolStationName)
        parcel?.writeString(petrolStationBrand)
        parcel?.writeString(petrolStationAddress)
        parcel?.writeParcelable(petrolStationLatLng, flags)
        distanceFromUser?.let {
            parcel?.writeDouble(it)
        }
        parcel?.writeList(petrolList)
    }
}