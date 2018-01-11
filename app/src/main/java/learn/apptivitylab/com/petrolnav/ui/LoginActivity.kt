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

    private var lImageLogo: AppCompatImageView? = null
    private var lEmailEditText: TextInputEditText? = null
    private var lPasswordEditText: TextInputEditText? = null
    private var lRegisterLink: TextView? = null
    private var lLoginButton: AppCompatButton? = null
    private var lContentViewGroup: ViewGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        lImageLogo = findViewById<View>(R.id.activity_login_logo_petrolnav) as AppCompatImageView
        lEmailEditText = findViewById<View>(R.id.activity_login_et_email) as TextInputEditText
        lPasswordEditText = findViewById<View>(R.id.activity_login_et_password) as TextInputEditText
        lRegisterLink = findViewById<View>(R.id.activity_login_textview_register) as TextView
        lLoginButton = findViewById<View>(R.id.activity_login_btn_login) as AppCompatButton
        lContentViewGroup = findViewById<View>(R.id.activity_login_vg_container) as ViewGroup

        lLoginButton!!.setOnClickListener { login() }
        lRegisterLink!!.setOnClickListener {
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
        lLoginButton!!.isEnabled = false

        //show loading
        val email = lEmailEditText!!.text.toString()
        val password = lPasswordEditText!!.text.toString()

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
        lLoginButton!!.isEnabled = true
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivityForResult(intent, REQUEST_LOGIN)
    }

    fun onLoginFailed() {
        Toast.makeText(baseContext, "Login Failed", Toast.LENGTH_LONG).show()
        lLoginButton!!.isEnabled = true
    }

    fun validate(): Boolean {
        var valid = true
        val email = lEmailEditText!!.text.toString()
        val password = lPasswordEditText!!.text.toString()

        if (email.isEmpty()) {
            lEmailEditText!!.error = "Please enter username"
            valid = false
        } else {
            lEmailEditText!!.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            lPasswordEditText!!.error = "Please enter password"
            valid = false
        } else {
            lPasswordEditText!!.error = null
        }
        return valid
    }

    companion object {
        private val TAG = "LoginActivity"
        private val REQUEST_SIGNUP = 0
        private val REQUEST_LOGIN = 0
    }
}
