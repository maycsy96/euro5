package learn.apptivitylab.com.petrolnav.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.controller.UserController
import learn.apptivitylab.com.petrolnav.model.User

/**
 * Created by apptivitylab on 08/01/2018.
 */

class LoginActivity : AppCompatActivity() {

    companion object {
        private val REQUEST_SIGNUP = 0
    }

    private var user = User()
    private var userList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        UserController.loadJSONUserList(this)?.let {
            this.userList = it
        }

        this.loginButton.setOnClickListener { this.loginAccount() }
        this.registerTextView.setOnClickListener {
            val launchIntent = RegisterActivity.newLaunchIntent(this, this.userList)
            this.startActivityForResult(launchIntent, REQUEST_SIGNUP)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == Activity.RESULT_OK) {
                this.userList = data.getParcelableArrayListExtra(RegisterActivity.ARG_USER_LIST)
            }
        }
    }

    private fun loginAccount() {
        if (!this.validateTextInput()) {
            this.onLoginFailed()
            return
        }

        this.loginButton.isEnabled = false

        val email = emailEditText.text.toString()
        this.user = this.userList.first { user -> user.userEmail == email }

        this.onLoginSuccess()
    }

    private fun onLoginSuccess() {
        this.loginButton.isEnabled = true
        Toast.makeText(baseContext, getString(R.string.message_login_success), Toast.LENGTH_LONG).show()
        val launchIntent = MainActivity.newLaunchIntent(this, this.user)
        this.startActivity(launchIntent)
        this.finish()
    }

    private fun onLoginFailed() {
        Toast.makeText(baseContext, getString(R.string.message_login_failed), Toast.LENGTH_LONG).show()
        this.loginButton.isEnabled = true
    }

    private fun validateTextInput(): Boolean {
        var valid = true
        val email = this.emailEditText.text.toString()
        val password = this.passwordEditText.text.toString()

        if (email.isEmpty()) {
            this.emailEditText.error = getString(R.string.message_invalid_email_address)
            valid = false
        } else {
            this.emailEditText.error = null
        }

        if (password.isEmpty()) {
            this.passwordEditText.error = getString(R.string.message_invalid_password)
            valid = false
        } else {
            this.passwordEditText.error = null
        }

        if (valid) {
            valid = (this.userList.firstOrNull { it.userEmail == email && it.userPassword == password }) != null
        }
        
        return valid
    }
}


