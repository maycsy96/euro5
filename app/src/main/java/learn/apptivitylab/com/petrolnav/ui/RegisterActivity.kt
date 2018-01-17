package learn.apptivitylab.com.petrolnav.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*
import learn.apptivitylab.com.petrolnav.R

/**
 * Created by apptivitylab on 08/01/2018.
 */

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        this.registerButton.setOnClickListener { register() }
        this.loginTextView.setOnClickListener {
            finish()
        }
    }

    fun register() {
        Log.d(TAG, "Register")
        if (!validate()) {
            onRegisterFailed()
            return
        }
        this.registerButton.isEnabled = false
        val name = this.registerNameEditText.text.toString()
        val email = this.registerEmailEditText.text.toString()
        val password = this.registerPasswordEditText.text.toString()

        //registration logic

        onRegisterSuccess()
    }

    fun onRegisterSuccess() {
        this.registerButton.isEnabled = true
        /*
        setResult(RESULT_OK);
        //should be finish this activity and return to login screen
        */
        finish()
    }

    fun onRegisterFailed() {
        Toast.makeText(baseContext, "Register failed", Toast.LENGTH_LONG).show()

        this.registerButton.isEnabled = true
    }

    fun validate(): Boolean {
        var valid = true

        val name = this.registerNameEditText.text.toString()
        val email = this.registerEmailEditText.text.toString()
        val password = this.registerPasswordEditText.text.toString()

        if (name.isEmpty() || name.length < 3) {
            this.registerNameEditText.error = "at least 3 characters"
            valid = false
        } else {
            this.registerNameEditText.error = null
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.registerEmailEditText.error = "enter a valid email address"
            valid = false
        } else {
            this.registerEmailEditText.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            this.registerPasswordEditText.error = "between 4 and 10 alphanumeric characters"
            valid = false
        } else {
            this.registerPasswordEditText.error = null
        }

        return valid
    }

    companion object {
        private val TAG = "RegisterActivity"
    }
}
