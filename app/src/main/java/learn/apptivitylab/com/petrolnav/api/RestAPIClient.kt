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
        const val CONTENT_TYPE = "application/json; charset=utf-8"
        private var singleton: RestAPIClient? = null

        fun shared(context: Context): RestAPIClient {
            if (this.singleton == null) {
                this.singleton = RestAPIClient(context)
            }
            return this.singleton!!
        }
    }

    private var requestQueue: RequestQueue = Volley.newRequestQueue(this.context)

    interface PostResponseReceivedListener {
        fun onPostResponseReceived(jsonObject: JSONObject?, error: VolleyError?)
    }

    interface VerificationCompleteListener {
        fun onVerificationCompleted(resultCode: Int)
    }

    interface GetResourceCompleteListener {
        fun onComplete(jsonObject: JSONObject?, error: VolleyError?)
    }

    interface ReceiveDataSetListener {
        fun onDataSetReceived(jsonObject: JSONObject?, error: VolleyError?)
    }

    interface ReceiveCompleteDataListener {
        fun onCompleteDataReceived(dataReceived: Boolean, error: VolleyError?)
    }

    fun loadResource(path: String, limit: Int?, completionListener: GetResourceCompleteListener) {
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
                    object : ReceiveDataSetListener {
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

    private fun getNextData(path: String, limit: Int = 100, offset: Int = 0, resultJSONArray: JSONArray, ReceiveDataSetListener: ReceiveDataSetListener) {
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
                            ReceiveDataSetListener.onDataSetReceived(resultJSONObject, null)
                        } else {
                            this@RestAPIClient.getNextData(url, limit, offset + limit, resultJSONArray, ReceiveDataSetListener)
                        }
                    }
                },
                Response.ErrorListener { error ->
                    ReceiveDataSetListener.onDataSetReceived(null, error)
                })
        this.requestQueue.add(request)
    }

    fun postResources(path: String, jsonRequest: JSONObject?, responseReceivedListener: PostResponseReceivedListener) {
        val url = String.format("%s%s", BASE_URL,path)
        val request = BackendlessJsonObjectRequest(Request.Method.POST, url, jsonRequest,
                Response.Listener<JSONObject> { response ->
                    responseReceivedListener.onPostResponseReceived(response, null)
                },
                Response.ErrorListener { error ->
                    responseReceivedListener.onPostResponseReceived(null, error)
                })
        request.putHeader("Content-Type", CONTENT_TYPE)
        this.requestQueue.add(request)
    }
}




