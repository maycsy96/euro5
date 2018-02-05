package learn.apptivitylab.com.petrolnav.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.User
import org.json.JSONArray
import java.io.FileNotFoundException
import java.io.IOException

/**
 * Created by apptivitylab on 08/01/2018.
 */

class RegisterActivity : AppCompatActivity() {

    companion object {
        private val TAG = "RegisterActivity"
        private val ARG_USER_LIST = "user_list"

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
        setContentView(R.layout.activity_register)

        this.userList = intent.getParcelableArrayListExtra(ARG_USER_LIST)

        this.registerButton.setOnClickListener { register() }

        this.loginTextView.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
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
        finish()
    }

    fun onRegisterFailed() {
        Toast.makeText(baseContext, "Register failed", Toast.LENGTH_LONG).show()

        this.registerButton.isEnabled = true
    }

    fun validate(): Boolean {
        var valid = true

        val name = this.nameEditText.text.toString()
        val email = this.emailEditText.text.toString()
        val password = this.passwordEditText.text.toString()
        var confirmPassword = this.confirmPasswordEditText.text.toString()

        if (name.isEmpty() || name.length < 3) {
            this.nameEditText.error = "at least 3 characters"
            valid = false
        } else {
            this.nameEditText.error = null
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.emailEditText.error = "enter a valid email address"
            valid = false
        } else {
            this.emailEditText.error = null
        }

        if (password.isEmpty() || password.length < 4) {
            this.passwordEditText.error = "at least 4 alphanumeric characters"
            valid = false
        } else {
            this.confirmPasswordEditText.error = null
        }

        if (confirmPassword.isEmpty()) {
            this.confirmPasswordEditText.error = "enter password to confirm"
        } else if (confirmPassword != password) {
            this.confirmPasswordEditText.error = "It is not same as the password you input"
        } else {
            this.confirmPasswordEditText.error = null
        }

        var userEmail = this.userList.firstOrNull{ it.userEmail == email}
        if(userEmail != null){
            this.emailEditText.error = "the email had been used. Please use other email"
            valid = false
        }

        return valid
    }
}
