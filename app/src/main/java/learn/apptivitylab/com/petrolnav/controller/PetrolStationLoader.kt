package learn.apptivitylab.com.petrolnav.controller

import android.content.Context
import com.android.volley.VolleyError
import learn.apptivitylab.com.petrolnav.api.RestAPIClient
import learn.apptivitylab.com.petrolnav.model.PetrolStation
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by apptivitylab on 16/01/2018.
 */
class PetrolStationLoader {

    companion object {
        private val TAG = "PetrolStationLoader"
        const val PETROL_STATION_URL = "data/stations?related=*"
        private var petrolStationList = ArrayList<PetrolStation>()

        fun loadJSONStations(context: Context): ArrayList<PetrolStation> {
            var path = PETROL_STATION_URL
            val petrolStationList = ArrayList<PetrolStation>()
            RestAPIClient.shared(context).loadResource(path,
                    object : RestAPIClient.getResourceCompleteListener {
                        override fun onComplete(jsonObject: JSONObject?, error: VolleyError?) {
                            if (jsonObject != null) {
                                var petrolStation: PetrolStation
                                val jsonArray: JSONArray = jsonObject.optJSONArray("resource")
                                for (i in 0..jsonArray?.length() - 1) {
                                    petrolStation = PetrolStation(jsonArray.getJSONObject(i))
                                    petrolStationList.add(petrolStation)
                                }
                                this@Companion.petrolStationList = petrolStationList
                            }
                        }
                    })
            return this@Companion.petrolStationList
        }
    }
}