package learn.apptivitylab.com.petrolnav.model

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by apptivitylab on 12/01/2018.
 */
data class Petrol(
        var petrolId: String? = null,
        var petrolName: String? = null,
        var petrolPrice: Double? = null,
        var petrolPriceChange: Double? = null,
        var petrolPriceHistoryList: ArrayList<PriceHistory> = ArrayList<PriceHistory>()) : Parcelable {

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

    constructor(parcel: Parcel) : this() {
        with(parcel) {
            this@Petrol.petrolId = readString()
            this@Petrol.petrolName = readString()
            this@Petrol.petrolPrice = readDouble()
            this@Petrol.petrolPriceChange = readDouble()
            this@Petrol.petrolPriceHistoryList = readArrayList(PriceHistory::class.java.classLoader) as ArrayList<PriceHistory>
        }
    }

    constructor(jsonObject: JSONObject?) : this() {
        this.petrolId = jsonObject?.optString("uuid")
        this.petrolName = jsonObject?.optString("name")
        var petrolPriceHistory: PriceHistory
        val petrolPriceHistoryListJsonArray = jsonObject?.optJSONArray("price_histories_by_petrol_uuid")
        this.petrolPriceHistoryList = ArrayList<PriceHistory>()

        petrolPriceHistoryListJsonArray?.let {
            for (i in 0..it.length() - 1) {
                try {
                    petrolPriceHistory = PriceHistory(petrolPriceHistoryListJsonArray.getJSONObject(i))
                    this.petrolPriceHistoryList.add(petrolPriceHistory)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            this.petrolPriceHistoryList.sortByDescending { it.dateCreated }
            this.petrolPrice = this.petrolPriceHistoryList.first().price
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeString(this@Petrol.petrolId)
            writeString(this@Petrol.petrolName)
            if (this@Petrol.petrolPrice == null) {
                writeDouble(0.0)
            } else {
                this@Petrol.petrolPrice?.let {
                    writeDouble(it)
                }
            }
            if (this@Petrol.petrolPriceChange == null) {
                writeDouble(0.0)
            } else {
                this@Petrol.petrolPriceChange?.let {
                    writeDouble(it)
                }
            }
            writeList(this@Petrol.petrolPriceHistoryList)
        }
    }

}