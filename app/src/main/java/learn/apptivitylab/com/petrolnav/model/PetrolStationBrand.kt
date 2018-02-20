package learn.apptivitylab.com.petrolnav.model

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

/**
 * Created by apptivitylab on 16/01/2018.
 */
data class PetrolStationBrand(
        var petrolStationBrandId: String? = null,
        var petrolStationBrandName: String? = null,
        var websiteURL: String? = null) : Parcelable {

    companion object CREATOR : Parcelable.Creator<PetrolStationBrand> {
        override fun createFromParcel(parcel: Parcel): PetrolStationBrand {
            return PetrolStationBrand(parcel)
        }

        override fun newArray(size: Int): Array<PetrolStationBrand?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this() {
        this.petrolStationBrandId = parcel.readString()
        this.petrolStationBrandName = parcel.readString()
        this.websiteURL = parcel.readString()
    }

    constructor(jsonObject: JSONObject?) : this() {
        this.petrolStationBrandId = jsonObject?.optString("uuid")
        this.petrolStationBrandName = jsonObject?.optString("name")
        this.websiteURL = jsonObject?.optString("website")
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(petrolStationBrandId)
        parcel?.writeString(petrolStationBrandName)
        parcel?.writeString(websiteURL)
    }
}