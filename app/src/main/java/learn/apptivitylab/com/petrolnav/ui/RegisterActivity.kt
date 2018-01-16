package learn.apptivitylab.com.petrolnav.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
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

class RegisterActivity : AppCompatActivity() {

    private var imageLogo: AppCompatImageView? = null
    private var nameEditText: AppCompatEditText? = null
    private var emailEditText: AppCompatEditText? = null
    private var passwordEditText: AppCompatEditText? = null
    private var loginLink: TextView? = null
    private var registerButton: AppCompatButton? = null
    private var contentViewGroup: ViewGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        this.imageLogo = findViewById<View>(R.id.activity_register_logo_petrolnav) as AppCompatImageView
        this.nameEditText = findViewById<View>(R.id.activity_register_et_name) as AppCompatEditText
        this.emailEditText = findViewById<View>(R.id.activity_register_et_email) as AppCompatEditText
        this.passwordEditText = findViewById<View>(R.id.activity_register_et_password) as AppCompatEditText
        this.loginLink = findViewById<View>(R.id.activity_register_textview_login) as TextView
        this.registerButton = findViewById<View>(R.id.activity_register_btn_register) as AppCompatButton
        this.contentViewGroup = findViewById<View>(R.id.activity_register_vg_container) as ViewGroup

        this.registerButton!!.setOnClickListener { register() }
        this.loginLink!!.setOnClickListener {
            //finish this registration screen and return to login activity
            finish()
        }
    }

    fun register() {
        Log.d(TAG, "Register")
        if (!validate()) {
            onRegisterFailed()
            return
        }
        this.registerButton!!.isEnabled = false
        val name = this.nameEditText!!.text.toString()
        val email = this.emailEditText!!.text.toString()
        val password = this.passwordEditText!!.text.toString()

        //registration logic

        onRegisterSuccess()
    }

    fun onRegisterSuccess() {
        this.registerButton!!.isEnabled = true
        /*
        setResult(RESULT_OK);
        asd//should be finish this activity and return to login screen
        */
        finish()
    }

    fun onRegisterFailed() {
        Toast.makeText(baseContext, "Register failed", Toast.LENGTH_LONG).show()

        this.registerButton!!.isEnabled = true
    }

    fun validate(): Boolean {
        var valid = true

        val name = this.nameEditText!!.text.toString()
        val email = this.emailEditText!!.text.toString()
        val password = this.passwordEditText!!.text.toString()

        if (name.isEmpty() || name.length < 3) {
            this.nameEditText!!.error = "at least 3 characters"
            valid = false
        } else {
            this.nameEditText!!.error = null
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.emailEditText!!.error = "enter a valid email address"
            valid = false
        } else {
            this.emailEditText!!.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            this.passwordEditText!!.error = "between 4 and 10 alphanumeric characters"
            valid = false
        } else {
            this.passwordEditText!!.error = null
        }

        return valid
    }

    companion object {
        private val TAG = "RegisterActivity"
    }
}
