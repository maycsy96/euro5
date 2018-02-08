package learn.apptivitylab.com.petrolnav.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.controller.UserController
import learn.apptivitylab.com.petrolnav.model.User

/**
 * Created by apptivitylab on 08/01/2018.
 */

class RegisterActivity : AppCompatActivity() {

    companion object {
        val EXTRA_USER_LIST = "user_list"

        fun newLaunchIntent(context: Context, userList: ArrayList<User>): Intent {
            val intent = Intent(context, RegisterActivity::class.java)
            intent.putExtra(EXTRA_USER_LIST, userList)
            return intent
        }
    }

    private var userList = ArrayList<User>()
    private var user = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_register)

        this.userList = intent.getParcelableArrayListExtra(EXTRA_USER_LIST)

        this.registerButton.setOnClickListener {
            val isValid = this.validateTextInput()
            if(isValid){
                this.registerAccount()
            }else{
                this.onRegisterFailed()
            }
        }

        this.loginTextView.setOnClickListener {
            this.setResult(Activity.RESULT_CANCELED, intent)
            this.finish()
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, intent)
        super.onBackPressed()
        finish()
    }

    fun registerAccount() {
        this.registerButton.isEnabled = false
        this.user.userName = this.nameEditText.text.toString()
        this.user.userEmail = this.emailEditText.text.toString()
        this.user.userPassword = this.passwordEditText.text.toString()

        this.userList.add(this.user)

        this.userList.forEachIndexed { id, user ->
            if (user.userId == null) {
                user.userId = id.toString()
            }
        }

        UserController.writeToJSONUserList(this, this.userList)
        this.onRegisterSuccess()
    }

    fun onRegisterSuccess() {
        Toast.makeText(baseContext, getString(R.string.message_register_success), Toast.LENGTH_LONG).show()
        this.registerButton.isEnabled = true
        setResult(Activity.RESULT_OK, intent.putExtra(EXTRA_USER_LIST, this.userList))
        finish()
    }

    fun onRegisterFailed() {
        Toast.makeText(baseContext, getString(R.string.message_register_failed), Toast.LENGTH_LONG).show()
        this.registerButton.isEnabled = true
    }

    fun validateTextInput(): Boolean {
        var isValid = true

        val nameText = this.nameEditText.text.toString()
        val emailText = this.emailEditText.text.toString()
        val passwordText = this.passwordEditText.text.toString()
        val confirmPasswordText = this.confirmPasswordEditText.text.toString()

        if (nameText.isEmpty() || nameText.length < 3) {
            this.nameEditText.error = getString(R.string.message_invalid_name)
            isValid = false
        } else {
            this.nameEditText.error = null
        }

        val userEmail = this.userList.firstOrNull { it.userEmail == emailText }
        if (emailText.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            this.emailEditText.error = getString(R.string.message_invalid_email_address)
            isValid = false
        } else if (userEmail != null) {
            this.emailEditText.error = getString(R.string.message_unavailable_email)
            isValid = false
        } else {
            this.emailEditText.error = null
        }

        if (passwordText.isEmpty() || passwordText.length < 4) {
            this.passwordEditText.error = getString(R.string.message_invalid_length_password)
            isValid = false
        } else {
            this.passwordEditText.error = null
        }

        this.confirmPasswordEditText.error = when {
            confirmPasswordText.isEmpty() -> getString(R.string.message_invalid_confirm_password)
            confirmPasswordText != passwordText -> getString(R.string.message_mismatch_confirm_password)
            else -> null
        }

        if (this.confirmPasswordEditText.error != null) {
            isValid = false
        }

        return isValid
    }
}
