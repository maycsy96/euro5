package learn.apptivitylab.com.petrolnav.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import org.json.JSONArray
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

    interface receiveDataSetListener {
        fun onDataSetReceived(jsonObject: JSONObject?, error: VolleyError?)
    }

    interface receiveCompleteDataListener {
        fun onCompleteDataReceived(dataReceived: Boolean, error: VolleyError?)
    }

    fun loadResource(path: String, limit: Int?, completionListener: getResourceCompleteListener) {
        val url = String.format("%s/%s", BASE_URL, path)
        if (limit == null) {
            val request = BackendlessJsonObjectRequest(Request.Method.GET, url, null,
                    Response.Listener<JSONObject> { response ->
                        completionListener.onComplete(response, null)
                    },
                    Response.ErrorListener { error ->
                        completionListener.onComplete(null, error)
                    })
            this.requestQueue.add(request)
        } else {
            var resultJSONArray = JSONArray()
            this.getNextData(url, limit, 0, resultJSONArray,
                    object : receiveDataSetListener {
                        override fun onDataSetReceived(jsonObject: JSONObject?, error: VolleyError?) {
                            if (jsonObject != null) {
                                completionListener.onComplete(jsonObject, null)
                            } else {
                                completionListener.onComplete(null, error)
                            }
                        }

                    })
        }
    }

    fun getNextData(path: String, limit: Int = 100, offset: Int = 0, resultJSONArray: JSONArray, receiveDataSetListener: receiveDataSetListener) {
        var limitParameter = "&limit=" + limit
        var offsetParameter = "&offset=" + offset
        val url = String.format("%s%s%s", path, limitParameter, offsetParameter)

        val request = BackendlessJsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener<JSONObject> { response ->
                    response?.let {
                        val jsonArray = it.optJSONArray("resource")
                        (0 until jsonArray.length()).forEach { item ->
                            resultJSONArray.put(jsonArray.getJSONObject(item))
                        }
                        if (jsonArray.length() < limit || jsonArray.length() == 0) {
                            var resultJSONObject = JSONObject()
                            resultJSONObject.put("resource", resultJSONArray)
                            receiveDataSetListener.onDataSetReceived(resultJSONObject, null)
                        } else {
                            this@RestAPIClient.getNextData(url, limit, offset + limit, resultJSONArray, receiveDataSetListener)
                        }
                    }
                },
                Response.ErrorListener { error ->
                    receiveDataSetListener.onDataSetReceived(null, error)
                })
        this.requestQueue.add(request)
    }
}




