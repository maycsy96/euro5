package learn.apptivitylab.com.petrolnav.controller

import android.content.Context
import android.util.Log
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.PetrolStationBrand
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by apptivitylab on 24/01/2018.
 */
class PetrolStationBrandLoader {
    companion object {
        private val TAG = "StationBrandLoader"

        fun loadJSONPetrolStationBrands(context: Context): ArrayList<PetrolStationBrand> {
            val petrolList: ArrayList<PetrolStationBrand> = ArrayList()
            val inputStream: InputStream = context.resources.openRawResource(R.raw.brands)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var jsonObject: JSONObject
            var petrolStationBrand: PetrolStationBrand

            try {
                var jsonFile = reader.readText()
                jsonObject = JSONObject(jsonFile.substring(jsonFile.indexOf("{"), jsonFile.lastIndexOf("}") + 1))
                val jsonArray: JSONArray = jsonObject.optJSONArray("brands")
                for (i in 0..jsonArray?.length() - 1) {
                    petrolStationBrand = PetrolStationBrand(jsonArray.getJSONObject(i))
                    petrolList.add(petrolStationBrand)
                }
            } catch (e: Exception) {
                Log.e(TAG, "LoadJSONStationBrands exception" + e.toString())
            }
            return petrolList
        }
    }
}