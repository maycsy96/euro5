package learn.apptivitylab.com.petrolnav.controller

import android.content.Context
import learn.apptivitylab.com.petrolnav.model.User
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedInputStream
import java.io.FileNotFoundException
import java.io.IOException

/**
 * Created by apptivitylab on 24/01/2018.
 */
class UserController {

    companion object {
        private val TAG = "UserController"

        fun loadJSONUserList(context: Context): ArrayList<User> {
            val fileName = "userlist.json"
            try {
                val inputStream = BufferedInputStream(context.openFileInput(fileName))
                val data = ByteArray(inputStream.available())
                inputStream.read(data)
                inputStream.close()

                val jsonArrayString = String(data)
                val jsonArray = JSONArray(jsonArrayString)
                val userList: ArrayList<User> = ArrayList<User>()
                for (i in 0..jsonArray.length() - 1) {
                    try {
                        var user = User(jsonArray.getJSONObject(i))
                        userList.add(user)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                return userList

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return ArrayList<User>()
        }

        fun writeToJSONUserList(context: Context, userList: ArrayList<User>) {
            var userListJsonArray = JSONArray()
            val fileName = "userlist.json"
            for (user in userList) {
                userListJsonArray.put(user.toJsonObject())
            }
            try {
                val outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
                outputStream.write(userListJsonArray.toString().toByteArray())
                outputStream.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}