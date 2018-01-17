package learn.apptivitylab.com.petrolnav.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import learn.apptivitylab.com.petrolnav.R

/**
 * Created by apptivitylab on 08/01/2018.
 */

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        this.loginButton.setOnClickListener { login() }
        this.registerTextView.setOnClickListener {
            //start the register Activity
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivityForResult(intent, REQUEST_SIGNUP)
        }
    }

    fun login() {
        Log.d(TAG, "Login")
        //validation of login information (making sure username and password are filled)
        if (!validate()) {
            //fail validation
            onLoginFailed()
            return
        }

        //validation successful
        this.loginButton.isEnabled = false

        //show loading
        val email = emailEditText.text.toString()
        val password = passwordEditText!!.text.toString()

        //Authentication logic

        android.os.Handler().postDelayed(
                {
                    //On complete call either onLoginSuccess or onLoginFailed
                    onLoginSuccess()
                }, 3000
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SIGNUP && resultCode == Activity.RESULT_OK) {
            //implement signup logic here
            this.finish()
        }
    }

    override fun onBackPressed() {
        //disable going back to the Main Activity
        moveTaskToBack(true)
    }

    fun onLoginSuccess() {
        this.loginButton.isEnabled = true
        //this.btn_login?.let {
        //it.isEnabled = true

        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivityForResult(intent, REQUEST_LOGIN)
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


