package learn.apptivitylab.com.petrolnav.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import learn.apptivitylab.com.petrolnav.controller.PetrolLoader
import learn.apptivitylab.com.petrolnav.model.PetrolStation
import org.json.JSONObject


/**
 * Created by apptivitylab on 14/02/2018.
 */
class RestAPIClient(val context: Context) {

    companion object {
        const val BASE_URL = "https://kong-gateway.apptivitylab.com/euro5-api-dev/v1"
        const val APPLICATION_KEY = "FrPSC2hFKKdsQgfASCBSrnwQdVg3rv4SXSpn"
        const val AUTHORIZATION = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiODVjOGE4Y2ItNGU0Yi00YmY2LTgzZWUtZWRhMzdiZGZmNGQxIiwibmFtZSI6IkFwcExhYiBEZXYgT25lIiwiZW1haWwiOiJkZXZAYXBwbGFiLm15IiwicGhvbmUiOm51bGwsInBob3RvX3VybCI6bnVsbCwiY3JlYXRlZF9hdCI6IjIwMTgtMDItMDYgMDY6MDY6MTEiLCJ1cGRhdGVkX2F0IjpudWxsLCJpZGVudGl0aWVzIjpbeyJ1dWlkIjoiNzczMjNkYzItY2M3YS00OTU2LThmMzgtMWRkMzUzOTc5ZTg0IiwidXNlcl9wcm9maWxlX3V1aWQiOiI4NWM4YThjYi00ZTRiLTRiZjYtODNlZS1lZGEzN2JkZmY0ZDEiLCJ0eXBlIjoidXNlcnBhc3MiLCJwcm92aWRlciI6ImFjY291bnRzLmFwcHRpdml0eWxhYi5jb20iLCJ2ZXJpZmllZCI6dHJ1ZSwiaWRlbnRpZmllciI6ImRldkBhcHBsYWIubXkiLCJpc19hY3RpdmF0ZWQiOmZhbHNlLCJjcmVhdGVkX2F0IjoiMjAxOC0wMi0wNiAwNjowNjoxMSIsInVwZGF0ZWRfYXQiOm51bGx9XSwiaWF0IjoxNTE4MDgyNTMwLCJleHAiOjE1MjMyNjY1MzAsImlzcyI6ImFjY291bnRzLmFwcHRpdml0eWxhYi5jb20ifQ.QZ1ACM0cYtpOCNDd4CiQlGLkkofRh339AVA2t8Hnplc"

        private var singleton: RestAPIClient? = null
            get() = field

        fun shared(context: Context): RestAPIClient {
            if (singleton == null) {
                singleton = RestAPIClient(context)
            }
            return singleton!!
        }
    }

    private lateinit var requestQueue: RequestQueue

    init {
        requestQueue = Volley.newRequestQueue(this.context)
    }

    interface getPetrolStationListListener {
        fun onComplete(petrolStationList: ArrayList<PetrolStation>, error: VolleyError?)
    }

    private fun newRequest(method: Int, path: String, parameters: JSONObject?, successListener: Response.Listener<JSONObject>?, errorListener: Response.ErrorListener?): JsonObjectRequest {
        val request = BackendlessJsonObjectRequest(method, path, parameters, successListener, errorListener)
        request.putHeader("X-Harbour-Application-Key", APPLICATION_KEY)
        request.putHeader("Authorization", AUTHORIZATION)
        return request
    }

    fun loadPetrolStationList(completionListener: getPetrolStationListListener) {
        val PETROL_STATION_URL = "data/companies"
        val url = String.format("%s/%s", BASE_URL, PETROL_STATION_URL)
        val request = newRequest(Request.Method.GET, url, null,
                Response.Listener<JSONObject> { response ->
                    val petrolStationList = ArrayList<PetrolStation>()

                    //TODO LOAD PETROLSTATION YO
                    PetrolLoader.loadJSONPetrols(context)
                    var data = response.optJSONArray("resource")
                    completionListener.onComplete(petrolStationList, null)

                },
                Response.ErrorListener { error ->
                    completionListener.onComplete(ArrayList(), error)
                })
        this.requestQueue.add(request)
    }


}




