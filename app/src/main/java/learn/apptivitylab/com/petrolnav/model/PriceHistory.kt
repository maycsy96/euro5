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
                        var dateFrom: Date? = null,
                        var dateTo: Date? = null) : Parcelable {

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
        dateFrom = parcel.readDate()
        dateTo = parcel.readDate()
    }

    constructor(jsonObject: JSONObject) : this() {
        this.price = jsonObject.optDouble("price")
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
        this.dateFrom = dateFormatter.parse(jsonObject.optString("date_from"))
        this.dateTo = dateFormatter.parse(jsonObject.optString("date_to"))
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        price?.let {
            parcel?.writeDouble(it)
        }
        parcel?.writeDate(dateFrom)
        parcel?.writeDate(dateTo)

    }

    fun Parcel.writeDate(date: Date?) {
        writeLong(date?.time ?: -1)
    }

    fun Parcel.readDate(): Date? {
        val long = readLong()
        return if (long != -1L) Date(long) else null
    }
}