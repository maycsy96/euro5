package learn.apptivitylab.com.petrolnav.controller

import android.content.Context
import android.util.Log
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.User
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by apptivitylab on 24/01/2018.
 */
class UserController {

    companion object {
        private val TAG = "UserController"

        fun loadJSONUser(context: Context): User {
            var user = User()
            val inputStream: InputStream = context.resources.openRawResource(R.raw.user)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var jsonObject: JSONObject

            try {
                var jsonFile = reader.readText()
                jsonObject = JSONObject(jsonFile.substring(jsonFile.indexOf("{"), jsonFile.lastIndexOf("}") + 1))
                user = User(jsonObject)

            } catch (e: Exception) {
                Log.e(UserController.TAG, "LoadJSONUser exception" + e.toString())
            }
            return user
        }
    }
}