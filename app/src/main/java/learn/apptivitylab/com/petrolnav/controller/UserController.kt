package learn.apptivitylab.com.petrolnav.controller

import android.content.Context
import android.util.Log
import learn.apptivitylab.com.petrolnav.model.User
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*

/**
 * Created by apptivitylab on 24/01/2018.
 */
class UserController {

    companion object {
        private val TAG = "UserController"

        fun loadJSONUser(context: Context): User? {
            val fileName = "user.txt"

            try {
                val inputStream = BufferedInputStream(context.openFileInput(fileName))
                val data = ByteArray(inputStream.available())
                inputStream.read(data)
                inputStream.close()

                val jsonObjectString = String(data)
                val jsonObject = JSONObject(jsonObjectString)

                val user = User(jsonObject)
                return user

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }
    }
}