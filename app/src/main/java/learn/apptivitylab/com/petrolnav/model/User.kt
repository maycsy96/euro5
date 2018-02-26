package learn.apptivitylab.com.petrolnav.model

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by apptivitylab on 12/01/2018.
 */
data class User(var userId: String? = null,
                var userName: String? = null,
                var userEmail: String? = null,
                var userPassword: String? = null,
                var userPhoneNumber: String? = null,
                var userCreatedAt: Date? = null,
                var userPreferredPetrol: Petrol? = null,
                var userPreferredPetrolStationBrandList: ArrayList<PetrolStationBrand>? = null) : Parcelable {

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this() {
        this.userId = parcel.readString()
        this.userName = parcel.readString()
        this.userEmail = parcel.readString()
        this.userPassword = parcel.readString()
        this.userPhoneNumber = parcel.readString()
        this.userCreatedAt = parcel.readDate()
        this.userPreferredPetrol = parcel.readParcelable(Petrol::class.java.classLoader)
        this.userPreferredPetrolStationBrandList = parcel.readArrayList(PetrolStationBrand::class.java.classLoader) as ArrayList<PetrolStationBrand>
    }

    constructor(jsonObject: JSONObject) : this() {
        this.userId = jsonObject.optString("uuid")
        this.userName = jsonObject.optString("name")
        this.userEmail = jsonObject.optString("password")
        this.userPassword = jsonObject.optString("email")
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd kk:mm:ss")
        this.userCreatedAt = dateFormatter.parse(jsonObject.optString("created_at"))
        if (jsonObject?.optJSONObject("petrol") != null) {
            this.userPreferredPetrol = Petrol(jsonObject?.optJSONObject("petrol"))
        }

        var petrolStationBrand: PetrolStationBrand
        var petrolStationBrandListJsonArray = jsonObject.optJSONArray("petrol_station_brands")
        this.userPreferredPetrolStationBrandList = ArrayList<PetrolStationBrand>()
        if (petrolStationBrandListJsonArray != null) {
            for (i in 0..petrolStationBrandListJsonArray.length() - 1) {
                try {
                    petrolStationBrand = PetrolStationBrand(petrolStationBrandListJsonArray.getJSONObject(i))
                    this.userPreferredPetrolStationBrandList?.add(petrolStationBrand)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun toJsonObject(): JSONObject {
        var jsonObjectUser = JSONObject()
        jsonObjectUser.put("user_id", this.userId)
        jsonObjectUser.put("user_name", this.userName)
        jsonObjectUser.put("user_email", this.userEmail)
        jsonObjectUser.put("user_password", this.userPassword)

        var jsonObjectPetrol = JSONObject()
        with(jsonObjectPetrol) {
            put("uuid", this@User.userPreferredPetrol?.petrolId)
            put("name", this@User.userPreferredPetrol?.petrolName)
        }
        jsonObjectUser.put("petrol", jsonObjectPetrol)

        var jsonObjectPetrolStationBrands = JSONArray()
        this.userPreferredPetrolStationBrandList?.forEach {
            var jsonObjectBrand = JSONObject()
            with(jsonObjectBrand) {
                put("uuid", it.petrolStationBrandId)
                put("name", it.petrolStationBrandName)
            }
            jsonObjectPetrolStationBrands.put(jsonObjectBrand)
        }
        jsonObjectUser.putOpt("petrol_station_brands", jsonObjectPetrolStationBrands)

        return jsonObjectUser
    }

    private fun Parcel.writeDate(date: Date?) {
        writeLong(date?.time ?: -1)
    }

    private fun Parcel.readDate(): Date? {
        val long = readLong()
        return if (long != -1L) Date(long) else null
    }

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(this.userId)
        parcel?.writeString(this.userName)
        parcel?.writeString(this.userEmail)
        parcel?.writeString(this.userPassword)
        parcel?.writeString(this.userPhoneNumber)
        parcel?.writeDate(this.userCreatedAt)
        parcel?.writeParcelable(this.userPreferredPetrol, flags)
        this.userPreferredPetrolStationBrandList?.let {
            parcel?.writeList(it)
        }
    }

    override fun describeContents(): Int {
        return 0
    }
}