package learn.apptivitylab.com.petrolnav.controller

import android.content.Context
import android.util.Log
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.Petrol
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by apptivitylab on 24/01/2018.
 */
class PetrolLoader {
    companion object {
        private val TAG = "PetrolLoader"

        fun loadJSONPetrols(context: Context): ArrayList<Petrol> {
            val petrolList: ArrayList<Petrol> = ArrayList()
            val inputStream: InputStream = context.resources.openRawResource(R.raw.petrols)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var jsonObject: JSONObject
            var petrol: Petrol

            try {
                var jsonFile = reader.readText()
                jsonObject = JSONObject(jsonFile.substring(jsonFile.indexOf("{"), jsonFile.lastIndexOf("}") + 1))
                val jsonArray: JSONArray = jsonObject.optJSONArray("petrols")
                for (i in 0..jsonArray?.length() - 1) {
                    petrol = Petrol(jsonArray.getJSONObject(i))
                    petrolList.add(petrol)
                }
            } catch (e: Exception) {
                Log.e(TAG, "LoadJSONPetrols exception" + e.toString())
            }
            return petrolList
        }
    }
}