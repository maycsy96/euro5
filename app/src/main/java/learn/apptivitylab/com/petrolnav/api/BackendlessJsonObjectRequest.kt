package learn.apptivitylab.com.petrolnav.api

import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject


/**
 * Created by apptivitylab on 14/02/2018.
 */
class BackendlessJsonObjectRequest(method: Int, url: String?, jsonRequest: JSONObject?, listener: Response.Listener<JSONObject>?, errorListener: Response.ErrorListener?) : JsonObjectRequest(method, url, jsonRequest, listener, errorListener) {

    private val additionalHeader = HashMap<String, String>()

    fun putHeader(key: String, value: String) {
        additionalHeader.put(key, value)
    }

    override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap(super.getHeaders())
        headers.putAll(additionalHeader)
        return headers
    }
}