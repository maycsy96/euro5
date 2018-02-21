package learn.apptivitylab.com.petrolnav.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


/**
 * Created by apptivitylab on 14/02/2018.
 */
class RestAPIClient(val context: Context) {

    companion object {
        const val BASE_URL = "https://kong-gateway.apptivitylab.com/euro5-api-dev/v1"
        private var singleton: RestAPIClient? = null

        fun shared(context: Context): RestAPIClient {
            if (this.singleton == null) {
                this.singleton = RestAPIClient(context)
            }
            return this.singleton!!
        }
    }

    private var requestQueue: RequestQueue

    init {
        this.requestQueue = Volley.newRequestQueue(this.context)
    }

    interface getResourceCompleteListener {
        fun onComplete(jsonObject: JSONObject?, error: VolleyError?)
    }

    }

    fun loadResource(path: String, completionListener: getResourceCompleteListener) {
        val url = String.format("%s/%s", BASE_URL, path)
        val request = newRequest(Request.Method.GET, url, null,
                Response.Listener<JSONObject> { response ->
                    completionListener.onComplete(response, null)
                },
                Response.ErrorListener { error ->
                    completionListener.onComplete(null, error)
                })
        this.requestQueue.add(request)
    }

}




