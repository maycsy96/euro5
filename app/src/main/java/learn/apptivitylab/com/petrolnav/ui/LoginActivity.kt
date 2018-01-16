package learn.apptivitylab.com.petrolnav.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatImageView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import learn.apptivitylab.com.petrolnav.R

/**
 * Created by apptivitylab on 08/01/2018.
 */

class LoginActivity : AppCompatActivity() {

    private var imageLogo: AppCompatImageView? = null
    private var emailEditText: TextInputEditText? = null
    private var passwordEditText: TextInputEditText? = null
    private var registerLink: TextView? = null
    private var loginButton: AppCompatButton? = null
    private var contentViewGroup: ViewGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        this.imageLogo = findViewById<View>(R.id.activity_login_logo_petrolnav) as AppCompatImageView
        this.emailEditText = findViewById<View>(R.id.activity_login_et_email) as TextInputEditText
        this.passwordEditText = findViewById<View>(R.id.activity_login_et_password) as TextInputEditText
        this.registerLink = findViewById<View>(R.id.activity_login_textview_register) as TextView
        this.loginButton = findViewById<View>(R.id.activity_login_btn_login) as AppCompatButton
        this.contentViewGroup = findViewById<View>(R.id.activity_login_vg_container) as ViewGroup

        this.loginButton!!.setOnClickListener { login() }
        this.registerLink!!.setOnClickListener {
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
        this.loginButton!!.isEnabled = false

        //show loading
        val email = emailEditText!!.text.toString()
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
        this.loginButton!!.isEnabled = true
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivityForResult(intent, REQUEST_LOGIN)
    }

    fun onLoginFailed() {
        Toast.makeText(baseContext, "Login Failed", Toast.LENGTH_LONG).show()
        this.loginButton!!.isEnabled = true
    }

    fun validate(): Boolean {
        var valid = true
        val email = this.emailEditText!!.text.toString()
        val password = this.passwordEditText!!.text.toString()

        if (email.isEmpty()) {
            this.emailEditText!!.error = "Please enter username"
            valid = false
        } else {
            this.emailEditText!!.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            this.passwordEditText!!.error = "Please enter password"
            valid = false
        } else {
            this.passwordEditText!!.error = null
        }
        return valid
    }

    companion object {
        private val TAG = "LoginActivity"
        private val REQUEST_SIGNUP = 0
        private val REQUEST_LOGIN = 0
    }
}
