package learn.apptivitylab.com.petrolnav.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.Toast
import com.android.volley.NoConnectionError
import com.android.volley.VolleyError
import kotlinx.android.synthetic.main.activity_register.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.api.RestAPIClient
import org.json.JSONObject
import java.net.SocketException

/**
 * Created by apptivitylab on 08/01/2018.
 */

class RegisterActivity : AppCompatActivity() {

    companion object {
        const val REGISTER_PATH = "/identity"

        fun newLaunchIntent(context: Context): Intent {
            return Intent(context, RegisterActivity::class.java)
        }
    }

    private var errorSnackBar: Snackbar? = null
    private var editTextArrayList = ArrayList<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_register)

        with(this.editTextArrayList) {
            add(this@RegisterActivity.nameEditText)
            add(this@RegisterActivity.emailEditText)
            add(this@RegisterActivity.phoneNumberEditText)
            add(this@RegisterActivity.passwordEditText)
            add(this@RegisterActivity.confirmPasswordEditText)
        }

        this.registerButton.setOnClickListener {
            val isValid = this.validateTextInput()
            if (isValid) {
                this.registerAccount()
            } else {
                this.onRegisterFailed()
            }
        }

        this.loginTextView.setOnClickListener {
            this.setResult(Activity.RESULT_CANCELED, intent)
            this.finish()
        }
    }

    override fun onBackPressed() {
        this.setResult(Activity.RESULT_CANCELED, intent)
        super.onBackPressed()
        this.finish()
    }

    private fun registerAccount() {
        this.registerButton.isEnabled = false

        val emailText = emailEditText.text.toString()
        val passwordText = passwordEditText.text.toString()
        val nameText = nameEditText.text.toString()
        val phoneNumberText = phoneNumberEditText.text.toString()

        var jsonRequest = JSONObject()
        jsonRequest.put("identifier", emailText)
        jsonRequest.put("challenge", passwordText)
        jsonRequest.put("type", "userpass")
        jsonRequest.put("name", nameText)
        jsonRequest.put("email", emailText)
        jsonRequest.put("phone", phoneNumberText)

        RestAPIClient.shared(this).postResources(REGISTER_PATH, jsonRequest,
                object : RestAPIClient.PostResponseReceivedListener {
                    override fun onPostResponseReceived(jsonObject: JSONObject?, error: VolleyError?) {
                        if (jsonObject != null) {
                            if (jsonObject.has("success") && jsonObject.optString("success") == "true") {
                                this@RegisterActivity.onRegisterSuccess()
                            }
                        } else {
                            error?.let {
                                when (it) {
                                    is NoConnectionError, is SocketException -> {
                                        this@RegisterActivity.errorSnackBar = Snackbar.make(registerViewgroupContainer, getString(R.string.message_connection_error), Snackbar.LENGTH_INDEFINITE)
                                        this@RegisterActivity.errorSnackBar?.let {
                                            it.show()
                                        }
                                    }
                                    else -> {
                                        this@RegisterActivity.errorSnackBar?.let {
                                            it.dismiss()
                                        }
                                        this@RegisterActivity.emailEditText.error = getString(R.string.message_unavailable_email)
                                    }
                                }
                                this@RegisterActivity.onRegisterFailed()
                            }
                        }
                    }
                })
    }

    fun onRegisterSuccess() {
        Toast.makeText(baseContext, getString(R.string.message_register_success), Toast.LENGTH_LONG).show()
        this.registerButton.isEnabled = true
        this.finish()
    }

    fun onRegisterFailed() {
        Toast.makeText(baseContext, getString(R.string.message_register_failed), Toast.LENGTH_LONG).show()
        this.registerButton.isEnabled = true
    }

    private fun validateTextInput(): Boolean {
        var isValid = true

        val nameText = this.nameEditText.text.toString()
        val phoneNumberText = this.phoneNumberEditText.text.toString()
        val emailText = this.emailEditText.text.toString()
        val passwordText = this.passwordEditText.text.toString()
        val confirmPasswordText = this.confirmPasswordEditText.text.toString()

        this.nameEditText.error = when {
            nameText.isEmpty() || nameText.length < 3 -> getString(R.string.message_invalid_name)
            else -> null
        }

        this.phoneNumberEditText.error = when {
            phoneNumberText.isEmpty() -> getString(R.string.message_invalid_phone_number)
            else -> null
        }

        this.emailEditText.error = when {
            emailText.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches() -> getString(R.string.message_invalid_email_address)
            else -> null
        }

        this.passwordEditText.error = when {
            passwordText.isEmpty() || passwordText.length < 4 -> getString(R.string.message_invalid_length_password)
            else -> null
        }

        this.confirmPasswordEditText.error = when {
            confirmPasswordText.isEmpty() -> getString(R.string.message_invalid_confirm_password)
            confirmPasswordText != passwordText -> getString(R.string.message_mismatch_confirm_password)
            else -> null
        }

        this.editTextArrayList.forEach {
            if (it.error != null) {
                isValid = false
            }
        }
        return isValid
    }
}
