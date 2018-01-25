package learn.apptivitylab.com.petrolnav.model

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONArray
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
        this.userPreferredPetrol = Petrol(jsonObject.optJSONObject("petrol"))

        var petrolStationBrand: PetrolStationBrand
        var petrolStationBrandListJsonArray: JSONArray = jsonObject.optJSONArray("petrol_station_brands")
        this.userPreferredPetrolStationBrandList =  ArrayList<PetrolStationBrand>()
        for (i in 0..petrolStationBrandListJsonArray?.length() - 1) {
            petrolStationBrand = PetrolStationBrand(petrolStationBrandListJsonArray.getJSONObject(i))
            this.userPreferredPetrolStationBrandList?.add(petrolStationBrand)
        }
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