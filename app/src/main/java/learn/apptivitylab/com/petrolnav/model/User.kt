package learn.apptivitylab.com.petrolnav.model

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by apptivitylab on 12/01/2018.
 */
data class User(var userId: String? = null,
                var userName: String? = null,
                var userEmail: String? = null,
                var userPassword: String? = null,
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
        userId = parcel.readString()
        userName = parcel.readString()
        userEmail = parcel.readString()
        userPassword = parcel.readString()
        userPreferredPetrol = parcel.readParcelable(Petrol::class.java.classLoader)
        userPreferredPetrolStationBrandList = parcel.readArrayList(PetrolStationBrand::class.java.classLoader) as ArrayList<PetrolStationBrand>
    }

    constructor(jsonObject: JSONObject) : this() {
        this.userId = jsonObject.optString("user_id")
        this.userName = jsonObject.optString("user_name")
        this.userEmail = jsonObject.optString("user_email")
        this.userPassword = jsonObject.optString("user_password")
        this.userPreferredPetrol = Petrol(jsonObject?.optJSONObject("petrol"))

        var petrolStationBrand: PetrolStationBrand
        var petrolStationBrandListJsonArray = jsonObject.optJSONArray("petrol_station_brands")
        this.userPreferredPetrolStationBrandList = ArrayList<PetrolStationBrand>()
        for (i in 0..petrolStationBrandListJsonArray.length() - 1) {
            try {
                petrolStationBrand = PetrolStationBrand(petrolStationBrandListJsonArray.getJSONObject(i))
                this.userPreferredPetrolStationBrandList?.add(petrolStationBrand)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    fun toJsonObject(user: User): JSONObject {
        var jsonObjectUser = JSONObject()
        jsonObjectUser.put("user_id", user.userId)
        jsonObjectUser.put("user_name", user.userName)
        jsonObjectUser.put("user_email", user.userEmail)
        jsonObjectUser.put("user_password", user.userPassword)

        var jsonObjectPetrol = JSONObject()
        with(jsonObjectPetrol) {
            put("petrol_id", user.userPreferredPetrol?.petrolId)
            put("petrol_name", user.userPreferredPetrol?.petrolName)
            put("petrol_price", user.userPreferredPetrol?.petrolPrice)
        }
        jsonObjectUser.put("petrol", jsonObjectPetrol)

        var jsonObjectPetrolStationBrands = JSONArray()
        user.userPreferredPetrolStationBrandList?.forEach {
            var jsonObjectBrand = JSONObject()
            with(jsonObjectBrand) {
                put("brand_id", it.petrolStationBrandId)
                put("brand_name", it.petrolStationBrandName)
            }
            jsonObjectPetrolStationBrands.put(jsonObjectBrand)
        }
        jsonObjectUser.putOpt("petrol_station_brands", jsonObjectPetrolStationBrands)

        return jsonObjectUser
    }
    
    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(userId)
        parcel?.writeString(userName)
        parcel?.writeString(userEmail)
        parcel?.writeString(userPassword)
        parcel?.writeParcelable(userPreferredPetrol, flags)
        userPreferredPetrolStationBrandList?.let {
            parcel?.writeList(it)
        }
    }

    override fun describeContents(): Int {
        return 0
    }
}