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
        val ARG_USER_LIST = "user_list"

        fun newLaunchIntent(context: Context, userList: ArrayList<User>): Intent {
            val intent = Intent(context, RegisterActivity::class.java)
            intent.putExtra(ARG_USER_LIST, userList)
            return intent
        }
    }

    private var userList = ArrayList<User>()
    private var user = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_register)

        this.userList = intent.getParcelableArrayListExtra(ARG_USER_LIST)

        this.registerButton.setOnClickListener { this.registerAccount() }

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
        if (!this.validateTextInput()) {
            this.onRegisterFailed()
            return
        }
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
        setResult(Activity.RESULT_OK, intent.putExtra(ARG_USER_LIST, this.userList))
        finish()
    }

    fun onRegisterFailed() {
        Toast.makeText(baseContext, getString(R.string.message_register_failed), Toast.LENGTH_LONG).show()
        this.registerButton.isEnabled = true
    }

    fun validateTextInput(): Boolean {
        var valid = true

        val name = this.nameEditText.text.toString()
        val email = this.emailEditText.text.toString()
        val password = this.passwordEditText.text.toString()
        var confirmPassword = this.confirmPasswordEditText.text.toString()

        if (name.isEmpty() || name.length < 3) {
            this.nameEditText.error = getString(R.string.message_invalid_name)
            valid = false
        } else {
            this.nameEditText.error = null
        }

        var userEmail = this.userList.firstOrNull { it.userEmail == email }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.emailEditText.error = getString(R.string.message_invalid_email_address)
            valid = false
        } else if (userEmail != null) {
            this.emailEditText.error = getString(R.string.message_unavailable_email)
            valid = false
        } else {
            this.emailEditText.error = null
        }

        if (password.isEmpty() || password.length < 4) {
            this.passwordEditText.error = getString(R.string.message_invalid_length_password)
            valid = false
        } else {
            this.passwordEditText.error = null
        }

        when {
            confirmPassword.isEmpty() -> this.confirmPasswordEditText.error = getString(R.string.message_invalid_confirm_password)
            confirmPassword != password -> this.confirmPasswordEditText.error = getString(R.string.message_mismatch_confirm_password)
            else -> this.confirmPasswordEditText.error = null
        }

        return valid
    }
}
