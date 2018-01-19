package learn.apptivitylab.com.petrolnav.controller

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.PetrolStation
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

        fun loadJSONStations (context:Context):ArrayList<PetrolStation>{
            val petrolStationList :ArrayList<PetrolStation> = ArrayList()
            val inputStream : InputStream = context.resources.openRawResource(R.raw.stations)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var jsonObject :JSONObject
            var petrolStation: PetrolStation

            reader.forEachLine {
                jsonObject = JSONObject(it)
                petrolStation = PetrolStation(jsonObject)
                petrolStationList.add(petrolStation)
            }
            return petrolStationList
        }
    }

}