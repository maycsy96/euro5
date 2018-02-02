package learn.apptivitylab.com.petrolnav.model

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by apptivitylab on 12/01/2018.
 */
data class Petrol(
        var petrolId: String? = null,
        var petrolName: String? = null,
        var petrolPrice: Double? = null,
        var petrolPriceHistoryList: ArrayList<PriceHistory> = ArrayList<PriceHistory>()) : Parcelable {

    constructor(parcel: Parcel) : this() {
        petrolId = parcel.readString()
        petrolName = parcel.readString()
        petrolPrice = parcel.readDouble()
        petrolPriceHistoryList = parcel.readArrayList(PriceHistory::class.java.classLoader) as ArrayList<PriceHistory>
    }

    constructor(jsonObject: JSONObject?) : this() {
        this.petrolId = jsonObject?.optString("petrol_id")
        this.petrolName = jsonObject?.optString("petrol_name")
        this.petrolPrice = jsonObject?.optDouble("petrol_price")

        var petrolPriceHistory: PriceHistory
        var petrolPriceHistoryListJsonArray = jsonObject?.optJSONArray("petrol_price_history")
        this.petrolPriceHistoryList = ArrayList<PriceHistory>()

        petrolPriceHistoryListJsonArray?.let{
            for(i in 0..it.length()-1){
                try {
                    petrolPriceHistory = PriceHistory(petrolPriceHistoryListJsonArray.getJSONObject(i))
                    this.petrolPriceHistoryList.add(petrolPriceHistory)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(petrolId)
        parcel?.writeString(petrolName)
        petrolPrice?.let {
            parcel?.writeDouble(it)
        }
        parcel?.writeList(petrolPriceHistoryList)
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