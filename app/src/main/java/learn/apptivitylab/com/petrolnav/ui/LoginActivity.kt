package learn.apptivitylab.com.petrolnav.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.controller.UserController
import learn.apptivitylab.com.petrolnav.model.User

/**
 * Created by apptivitylab on 08/01/2018.
 */

class LoginActivity : AppCompatActivity() {

    private var user = User()
    private var userList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        this.loginButton.setOnClickListener { login() }
        this.registerTextView.setOnClickListener {

            UserController.loadJSONUserList(this)?.let {
                this.userList = it
            }

            val launchIntent = RegisterActivity.newLaunchIntent(this, userList)
            startActivityForResult(launchIntent, REQUEST_SIGNUP)
        }
    }

    fun login() {
        Log.d(TAG, "Login")
        if (!validate()) {
            onLoginFailed()
            return
        }

        this.loginButton.isEnabled = false

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        //TODO Authentication Logic

        android.os.Handler().postDelayed(
                {
                    onLoginSuccess()
                }, 3000
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == Activity.RESULT_OK) {
                this.userList = data.getParcelableArrayListExtra(RegisterActivity.ARG_USER_LIST)
            }
        }
    }


    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    fun onLoginSuccess() {
        this.loginButton.isEnabled = true

        val launchIntent = MainActivity.newLaunchIntent(this, this.user)
        startActivityForResult(launchIntent, REQUEST_LOGIN)
        finish()
    }

    fun onLoginFailed() {
        Toast.makeText(baseContext, "Login Failed", Toast.LENGTH_LONG).show()
        this.loginButton.isEnabled = true
    }

    fun validate(): Boolean {
        var valid = true
        val email = this.emailEditText.text.toString()
        val password = this.passwordEditText.text.toString()

        if (email.isEmpty()) {
            this.emailEditText.error = "Please enter username"
            valid = false
        } else {
            this.emailEditText.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            this.passwordEditText.error = "Please enter password"
            valid = false
        } else {
            this.passwordEditText.error = null
        }
        return valid
    }

    companion object {
        private val TAG = "LoginActivity"
        private val REQUEST_SIGNUP = 0
        private val REQUEST_LOGIN = 0
    }
}


