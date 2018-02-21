package learn.apptivitylab.com.petrolnav.api

import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject


/**
 * Created by apptivitylab on 14/02/2018.
 */
class BackendlessJsonObjectRequest(method: Int, url: String?, jsonRequest: JSONObject?, listener: Response.Listener<JSONObject>?, errorListener: Response.ErrorListener?) : JsonObjectRequest(method, url, jsonRequest, listener, errorListener) {

    companion object {
        const val APPLICATION_KEY = "FrPSC2hFKKdsQgfASCBSrnwQdVg3rv4SXSpn"
        const val AUTHORIZATION = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiODVjOGE4Y2ItNGU0Yi00YmY2LTgzZWUtZWRhMzdiZGZmNGQxIiwibmFtZSI6IkFwcExhYiBEZXYgT25lIiwiZW1haWwiOiJkZXZAYXBwbGFiLm15IiwicGhvbmUiOm51bGwsInBob3RvX3VybCI6bnVsbCwiY3JlYXRlZF9hdCI6IjIwMTgtMDItMDYgMDY6MDY6MTEiLCJ1cGRhdGVkX2F0IjpudWxsLCJpZGVudGl0aWVzIjpbeyJ1dWlkIjoiNzczMjNkYzItY2M3YS00OTU2LThmMzgtMWRkMzUzOTc5ZTg0IiwidXNlcl9wcm9maWxlX3V1aWQiOiI4NWM4YThjYi00ZTRiLTRiZjYtODNlZS1lZGEzN2JkZmY0ZDEiLCJ0eXBlIjoidXNlcnBhc3MiLCJwcm92aWRlciI6ImFjY291bnRzLmFwcHRpdml0eWxhYi5jb20iLCJ2ZXJpZmllZCI6dHJ1ZSwiaWRlbnRpZmllciI6ImRldkBhcHBsYWIubXkiLCJpc19hY3RpdmF0ZWQiOmZhbHNlLCJjcmVhdGVkX2F0IjoiMjAxOC0wMi0wNiAwNjowNjoxMSIsInVwZGF0ZWRfYXQiOm51bGx9XSwiaWF0IjoxNTE4MDgyNTMwLCJleHAiOjE1MjMyNjY1MzAsImlzcyI6ImFjY291bnRzLmFwcHRpdml0eWxhYi5jb20ifQ.QZ1ACM0cYtpOCNDd4CiQlGLkkofRh339AVA2t8Hnplc"
    }

    private val additionalHeader = HashMap<String, String>()

    fun putHeader(key: String, value: String) {
        additionalHeader.put(key, value)
    }

    override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap(super.getHeaders())
        headers.put("X-Harbour-Application-Key", APPLICATION_KEY)
        headers.put("Authorization", AUTHORIZATION)
        headers.putAll(additionalHeader)
        return headers
    }
}