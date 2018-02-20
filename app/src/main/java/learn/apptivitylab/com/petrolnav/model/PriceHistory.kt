package learn.apptivitylab.com.petrolnav.model

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by apptivitylab on 01/02/2018.
 */
data class PriceHistory(var price: Double? = null,
                        var dateCreated: Date? = null
) : Parcelable {

    companion object CREATOR : Parcelable.Creator<PriceHistory> {
        override fun createFromParcel(parcel: Parcel): PriceHistory {
            return PriceHistory(parcel)
        }

        override fun newArray(size: Int): Array<PriceHistory?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this() {
        price = parcel.readDouble()
        dateCreated = parcel.readDate()
    }

    constructor(jsonObject: JSONObject) : this() {
        this.price = jsonObject.optDouble("price_cents")
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd kk:mm:ss")
        this.dateCreated = dateFormatter.parse(jsonObject.optString("created_at"))
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        price?.let {
            parcel?.writeDouble(it)
        }
        parcel?.writeDate(dateCreated)
    }

    fun Parcel.writeDate(date: Date?) {
        writeLong(date?.time ?: -1)
    }

    fun Parcel.readDate(): Date? {
        val long = readLong()
        return if (long != -1L) Date(long) else null
    }
}