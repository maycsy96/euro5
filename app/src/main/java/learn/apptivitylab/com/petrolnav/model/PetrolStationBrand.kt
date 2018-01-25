package learn.apptivitylab.com.petrolnav.model

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

/**
 * Created by apptivitylab on 16/01/2018.
 */
data class PetrolStationBrand(
        var petrolStationBrandId: String? = null,
        var petrolStationBrandName: String? = null) : Parcelable {

    companion object CREATOR : Parcelable.Creator<PetrolStationBrand> {
        override fun createFromParcel(parcel: Parcel): PetrolStationBrand {
            return PetrolStationBrand(parcel)
        }

        override fun newArray(size: Int): Array<PetrolStationBrand?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this() {
        petrolStationBrandId = parcel.readString()
        petrolStationBrandName = parcel.readString()
    }

    constructor(jsonObject: JSONObject) : this() {
        this.petrolStationBrandId = jsonObject.optString("brand_id")
        this.petrolStationBrandName = jsonObject.optString("brand_name")
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(petrolStationBrandId)
        parcel?.writeString(petrolStationBrandName)
    }
}