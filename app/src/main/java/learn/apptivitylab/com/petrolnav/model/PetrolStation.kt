package learn.apptivitylab.com.petrolnav.model

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by apptivitylab on 12/01/2018.
 */
data class PetrolStation(var petrolStationId: String? = null,
                         var petrolStationName: String? = null,
                         var petrolStationBrand: String? = null,
                         var petrolStationLatLng: LatLng? = null,
                         var distanceFromUser: Double? = null,
                         var petrolList: ArrayList<Petrol>? = null) : Parcelable, ClusterItem {

    companion object CREATOR : Parcelable.Creator<PetrolStation> {
        override fun createFromParcel(parcel: Parcel): PetrolStation {
            return PetrolStation(parcel)
        }

        override fun newArray(size: Int): Array<PetrolStation?> {
            return arrayOfNulls(size)
        }
    }

    override fun getSnippet(): String {
        return this.petrolStationBrand ?: ""
    }

    override fun getTitle(): String {
        return this.petrolStationName ?: ""
    }

    override fun getPosition(): LatLng {
        return this.petrolStationLatLng ?: LatLng(0.0, 0.0)
    }

    constructor(parcel: Parcel) : this() {
        with(parcel) {
            this@PetrolStation.petrolStationId = readString()
            this@PetrolStation.petrolStationName = readString()
            this@PetrolStation.petrolStationBrand = readString()
            this@PetrolStation.petrolStationLatLng = readParcelable(LatLng::class.java.classLoader)
            val distanceFromUser = readDouble()
            this@PetrolStation.distanceFromUser = if (distanceFromUser == -1.0) null else distanceFromUser
            this@PetrolStation.petrolList = readArrayList(Petrol::class.java.classLoader) as ArrayList<Petrol>
        }
    }

    constructor(jsonObject: JSONObject) : this() {
        this.petrolStationId = jsonObject.optString("uuid")
        this.petrolStationName = jsonObject.optString("name")
        this.petrolStationBrand = jsonObject.optJSONObject("companies_by_company_uuid").optString("name")
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeString(this@PetrolStation.petrolStationId)
            writeString(this@PetrolStation.petrolStationName)
            writeString(this@PetrolStation.petrolStationBrand)
            writeParcelable(this@PetrolStation.petrolStationLatLng, flags)
            if (this@PetrolStation.distanceFromUser == null) {
                writeDouble(-1.0)
            } else {
                this@PetrolStation.distanceFromUser?.let { writeDouble(it) }
            }
            writeList(this@PetrolStation.petrolList)
        }
    }
}