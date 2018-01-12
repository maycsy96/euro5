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

    private var rImageLogo: AppCompatImageView? = null
    private var rNameEditText: AppCompatEditText? = null
    private var rEmailEditText: AppCompatEditText? = null
    private var rPasswordEditText: AppCompatEditText? = null
    private var rLoginLink: TextView? = null
    private var rRegisterButton: AppCompatButton? = null
    private var rContentViewGroup: ViewGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        rImageLogo = findViewById<View>(R.id.activity_register_logo_petrolnav) as AppCompatImageView
        rNameEditText = findViewById<View>(R.id.activity_register_et_name) as AppCompatEditText
        rEmailEditText = findViewById<View>(R.id.activity_register_et_email) as AppCompatEditText
        rPasswordEditText = findViewById<View>(R.id.activity_register_et_password) as AppCompatEditText
        rLoginLink = findViewById<View>(R.id.activity_register_textview_login) as TextView
        rRegisterButton = findViewById<View>(R.id.activity_register_btn_register) as AppCompatButton
        rContentViewGroup = findViewById<View>(R.id.activity_register_vg_container) as ViewGroup

        rRegisterButton!!.setOnClickListener { register() }
        rLoginLink!!.setOnClickListener {
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
        rRegisterButton!!.isEnabled = false
        val name = rNameEditText!!.text.toString()
        val email = rEmailEditText!!.text.toString()
        val password = rPasswordEditText!!.text.toString()

        //registration logic

        onRegisterSuccess()
    }

    fun onRegisterSuccess() {
        rRegisterButton!!.isEnabled = true
        /*
        setResult(RESULT_OK);
        asd//should be finish this activity and return to login screen
        */
        finish()
    }

    fun onRegisterFailed() {
        Toast.makeText(baseContext, "Register failed", Toast.LENGTH_LONG).show()

        rRegisterButton!!.isEnabled = true
    }

    fun validate(): Boolean {
        var valid = true

        val name = rNameEditText!!.text.toString()
        val email = rEmailEditText!!.text.toString()
        val password = rPasswordEditText!!.text.toString()

        if (name.isEmpty() || name.length < 3) {
            rNameEditText!!.error = "at least 3 characters"
            valid = false
        } else {
            rNameEditText!!.error = null
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            rEmailEditText!!.error = "enter a valid email address"
            valid = false
        } else {
            rEmailEditText!!.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            rPasswordEditText!!.error = "between 4 and 10 alphanumeric characters"
            valid = false
        } else {
            rPasswordEditText!!.error = null
        }

        return valid
    }

    companion object {
        private val TAG = "RegisterActivity"
    }
}
