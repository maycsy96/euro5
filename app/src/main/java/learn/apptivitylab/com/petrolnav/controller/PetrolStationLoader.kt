package learn.apptivitylab.com.petrolnav.controller

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.PetrolStation
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by apptivitylab on 16/01/2018.
 */
class PetrolStationLoader {

    companion object {
        private val TAG = "PetrolStationLoader"

        fun loadJSONStations(context: Context): ArrayList<PetrolStation> {
            val petrolStationList: ArrayList<PetrolStation> = ArrayList()
            val inputStream: InputStream = context.resources.openRawResource(R.raw.stations)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var jsonObject: JSONObject
            var petrolStation: PetrolStation

            try {
                var jsonFile = reader.readText()
                jsonObject = JSONObject(jsonFile.substring(jsonFile.indexOf("{"), jsonFile.lastIndexOf("}") + 1))
                val jsonArray: JSONArray = jsonObject.optJSONArray("petrol_stations")
                for (i in 0..jsonArray?.length() - 1) {
                    petrolStation = PetrolStation(jsonArray.getJSONObject(i))
                    petrolStationList.add(petrolStation)
                }
            } catch (e: Exception) {
                Log.e(TAG, "LoadJSON exception" + e.toString())
            }
            return petrolStationList
        }
    }

}