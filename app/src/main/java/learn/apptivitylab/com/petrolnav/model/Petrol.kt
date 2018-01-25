package learn.apptivitylab.com.petrolnav.model

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

/**
 * Created by apptivitylab on 12/01/2018.
 */
data class Petrol(
        var petrolId: String? = null,
        var petrolName: String? = null,
        var petrolPrice: Double? = null) : Parcelable {

    constructor(parcel: Parcel) : this() {
        petrolId = parcel.readString()
        petrolName = parcel.readString()
        petrolPrice = parcel.readDouble()
    }

    constructor(jsonObject: JSONObject) : this() {
        this.petrolId = jsonObject.optString("petrol_id")
        this.petrolName = jsonObject.optString("petrol_name")
        this.petrolPrice = jsonObject.optDouble("petrol_price")
    }

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(petrolId)
        parcel?.writeString(petrolName)
        petrolPrice?.let {
            parcel?.writeDouble(it)
        }
    }

    companion object CREATOR : Parcelable.Creator<Petrol> {
        override fun createFromParcel(parcel: Parcel): Petrol {
            return Petrol(parcel)
        }

        override fun newArray(size: Int): Array<Petrol?> {
            return arrayOfNulls(size)
        }
    }

    override fun describeContents(): Int {
        return 0
    }
}