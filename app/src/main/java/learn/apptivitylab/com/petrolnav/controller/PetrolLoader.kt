package learn.apptivitylab.com.petrolnav.controller

import android.content.Context
import com.android.volley.VolleyError
import learn.apptivitylab.com.petrolnav.api.RestAPIClient
import learn.apptivitylab.com.petrolnav.model.Petrol
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by apptivitylab on 24/01/2018.
 */
class PetrolLoader {
    companion object {
        val TAG = "PetrolLoader"
        const val PETROL_URL = "data/petrols?related=*"
        var petrolList = ArrayList<Petrol>()

        fun loadJSONPetrols(context: Context, petrolDataListener: RestAPIClient.ReceiveCompleteDataListener) {
            var path = PETROL_URL
            val petrolList: ArrayList<Petrol> = ArrayList()
            RestAPIClient.shared(context).loadResource(path, null,
                    object : RestAPIClient.GetResourceCompleteListener {
                        override fun onComplete(jsonObject: JSONObject?, error: VolleyError?) {
                            if (jsonObject != null) {
                                var petrol: Petrol
                                val jsonArray: JSONArray = jsonObject.optJSONArray("resource")
                                for (i in 0..jsonArray?.length() - 1) {
                                    petrol = Petrol(jsonArray.getJSONObject(i))
                                    petrolList.add(petrol)
                                }
                                this@Companion.petrolList = petrolList
                                petrolDataListener.onCompleteDataReceived(true, null)
                            } else {
                                petrolDataListener.onCompleteDataReceived(false, error)
                            }
                        }
                    })
        }
    }
}