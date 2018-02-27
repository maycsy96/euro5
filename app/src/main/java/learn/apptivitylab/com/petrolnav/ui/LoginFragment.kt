package learn.apptivitylab.com.petrolnav.ui

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.NoConnectionError
import com.android.volley.VolleyError
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.progress_bar_dialog.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.api.RestAPIClient
import learn.apptivitylab.com.petrolnav.model.Petrol
import learn.apptivitylab.com.petrolnav.model.PetrolStationBrand
import learn.apptivitylab.com.petrolnav.model.User
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.net.SocketException

/**
 * Created by apptivitylab on 08/02/2018.
 */
class LoginFragment : Fragment() {
    companion object {
        const val VERIFY_PATH = "/identity/session"
        fun newInstance(): LoginFragment {
            val fragment = LoginFragment()
            val args: Bundle = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var user = User()
    private var progressBarDialog: Dialog? = null
    private var errorSnackBar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.setupProgressBarDialog()
        this.loginButton.setOnClickListener {
            val isValid = this.validateTextInput()
            if (isValid) {
                this.loginAccount()
            } else {
                this.onLoginFailed()
            }
        }
        this.registerTextView.setOnClickListener {
            val launchIntent = RegisterActivity.newLaunchIntent(this.context!!)
            this.startActivityForResult(launchIntent, LoginActivity.REQUEST_SIGNUP)
        }

        this.forgotPasswordTextView.setOnClickListener {
            this.activity!!.supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_right_exit, R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit)
                    .replace(R.id.loginViewgroupContainer, ForgotPasswordFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
        }
    }

    private fun loginAccount() {
        this.loginButton.isEnabled = false
        this.progressBarDialog?.let {
            it.show()
        }
        val emailText = emailEditText.text.toString()
        val passwordText = passwordEditText.text.toString()
        this.verifyUserAccount(emailText, passwordText)
    }

    private fun verifyUserAccount(emailText: String, passwordText: String) {
        var jsonRequest = JSONObject()
        jsonRequest.put("identifier", emailText)
        jsonRequest.put("challenge", passwordText)
        jsonRequest.put("type", "userpass")

        RestAPIClient.shared(this.context!!).postResources(VERIFY_PATH, jsonRequest,
                object : RestAPIClient.PostResponseReceivedListener {
                    override fun onPostResponseReceived(jsonObject: JSONObject?, error: VolleyError?) {
                        this@LoginFragment.progressBarDialog?.let {
                            it.dismiss()
                        }
                        if (jsonObject != null) {
                            if (jsonObject.has("success") && jsonObject.optString("success") == "true") {
                                this@LoginFragment.user = User(jsonObject.optJSONObject("profile"))
                                this@LoginFragment.onLoginSuccess()
                            }
                        } else {
                            error?.let {
                                when (it) {
                                    is NoConnectionError, is SocketException -> {
                                        view?.let {
                                            this@LoginFragment.errorSnackBar = Snackbar.make(it, getString(R.string.message_connection_error), Snackbar.LENGTH_INDEFINITE)
                                            this@LoginFragment.errorSnackBar?.let {
                                                it.show()
                                            }
                                        }
                                    }
                                    else -> {
                                        this@LoginFragment.errorSnackBar?.let {
                                            it.dismiss()
                                        }
                                    }
                                }
                                this@LoginFragment.onLoginFailed()
                            }
                        }
                    }
                })
    }

    private fun onLoginSuccess() {
        this.loginButton.isEnabled = true
        Toast.makeText(this.context, getString(R.string.message_login_success), Toast.LENGTH_LONG).show()

        this.user.userPreferredPetrolStationBrandList?.let {
            val userPreference = this.loadJSONPreference(this.user)
            if (userPreference != null) {
                this.user.userPreferredPetrol = userPreference.userPreferredPetrol
                this.user.userPreferredPetrolStationBrandList = userPreference.userPreferredPetrolStationBrandList
            }
            if (this.user.userPreferredPetrol == null && it.isEmpty()) {
                this.showWelcomeDialog()
                this.activity!!.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.loginViewgroupContainer, PreferencesFragment.newInstance(this.user))
                        .addToBackStack(null)
                        .commit()
            } else {
                val launchIntent = MainActivity.newLaunchIntent(this.context!!, this.user)
                this.startActivity(launchIntent)
                this.activity!!.finish()
            }
        }
    }

    private fun onLoginFailed() {
        Toast.makeText(this.context, getString(R.string.message_login_failed), Toast.LENGTH_LONG).show()
        this.loginButton.isEnabled = true
    }

    private fun validateTextInput(): Boolean {
        var isValid = true
        val emailText = this.emailEditText.text.toString()
        val passwordText = this.passwordEditText.text.toString()

        if (emailText.isEmpty()) {
            this.emailEditText.error = getString(R.string.message_invalid_email_address)
            isValid = false
        } else {
            this.emailEditText.error = null
        }

        if (passwordText.isEmpty()) {
            this.passwordEditText.error = getString(R.string.message_invalid_password)
            isValid = false
        } else {
            this.passwordEditText.error = null
        }
        return isValid
    }

    private fun loadJSONPreference(user: User): User? {
        val fileName = String.format("%s.json", user.userName)
        try {
            val inputStream = BufferedInputStream(context!!.openFileInput(fileName))
            val data = ByteArray(inputStream.available())
            inputStream.read(data)
            inputStream.close()


            val user = User()
            val jsonObjectString = String(data)
            val jsonObject = JSONObject(jsonObjectString)

            user.userPreferredPetrol = Petrol(jsonObject.optJSONObject("petrol"))

            var petrolStationBrand: PetrolStationBrand
            val petrolStationBrandListJsonArray = jsonObject.optJSONArray("petrol_station_brands")
            user.userPreferredPetrolStationBrandList = ArrayList()
            if (petrolStationBrandListJsonArray != null) {
                for (i in 0 until petrolStationBrandListJsonArray.length()) {
                    try {
                        petrolStationBrand = PetrolStationBrand(petrolStationBrandListJsonArray.getJSONObject(i))
                        user.userPreferredPetrolStationBrandList?.add(petrolStationBrand)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

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

    private fun showWelcomeDialog() {
        AlertDialog.Builder(this.context!!)
                .setTitle(getString(R.string.title_welcome_to_petrolnav))
                .setMessage(getString(R.string.message_welcome_to_petrolnav))
                .setNeutralButton(getString(R.string.button_ok), null)
                .show()
    }

    private fun setupProgressBarDialog() {
        this.progressBarDialog = Dialog(this.activity)
        this.progressBarDialog?.let {
            it.setContentView(R.layout.progress_bar_dialog)
            it.window.setBackgroundDrawableResource(android.R.color.transparent)
            it.progressBarTextView.text = getString(R.string.message_authenticating)
        }
    }
}