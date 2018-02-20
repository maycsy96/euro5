package learn.apptivitylab.com.petrolnav.controller

import android.content.Context
import com.android.volley.VolleyError
import learn.apptivitylab.com.petrolnav.api.RestAPIClient
import learn.apptivitylab.com.petrolnav.model.PetrolStationBrand
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by apptivitylab on 24/01/2018.
 */
class PetrolStationBrandLoader {
    companion object {
        private val TAG = "StationBrandLoader"
        private const val PETROL_STATION_BRAND_URL = "data/companies"
        private var petrolStationBrandList = ArrayList<PetrolStationBrand>()

        fun loadJSONPetrolStationBrands(context: Context): ArrayList<PetrolStationBrand> {
            var path = PETROL_STATION_BRAND_URL
            val petrolStationBrandList: ArrayList<PetrolStationBrand> = ArrayList()
            RestAPIClient.shared(context).loadResource(path,
                    object : RestAPIClient.getResourceCompleteListener {
                        override fun onComplete(jsonObject: JSONObject?, error: VolleyError?) {
                            if (jsonObject != null) {
                                var petrolStationBrand: PetrolStationBrand
                                val jsonArray: JSONArray = jsonObject.optJSONArray("resource")
                                for (i in 0..jsonArray?.length() - 1) {
                                    petrolStationBrand = PetrolStationBrand(jsonArray.getJSONObject(i))
                                    petrolStationBrandList.add(petrolStationBrand)
                                }
                                this@Companion.petrolStationBrandList = petrolStationBrandList
                            }
                        }
                    })
            return this@Companion.petrolStationBrandList
        }
    }
}