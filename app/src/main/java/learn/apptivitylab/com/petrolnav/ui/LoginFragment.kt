package learn.apptivitylab.com.petrolnav.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_login.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.User

/**
 * Created by apptivitylab on 08/02/2018.
 */
class LoginFragment : Fragment() {
    companion object {
        const val ARG_USER_LIST = "user_list"
        fun newInstance(userList: ArrayList<User>): LoginFragment {
            val fragment = LoginFragment()
            val args: Bundle = Bundle()
            args.putParcelableArrayList(ARG_USER_LIST, userList)
            fragment.arguments = args
            return fragment
        }
    }

    private var userList = ArrayList<User>()
    private var user = User()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            this.userList = it.getParcelableArrayList(ARG_USER_LIST)
        }

        this.loginButton.setOnClickListener { this.loginAccount() }
        this.registerTextView.setOnClickListener {
            val launchIntent = RegisterActivity.newLaunchIntent(this.context!!, this.userList)
            this.startActivityForResult(launchIntent, LoginActivity.REQUEST_SIGNUP)
        }

        this.forgotPasswordTextView.setOnClickListener {
            this.activity!!.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.loginViewgroupContainer, ForgotPasswordFragment.newInstance(this.userList))
                    .addToBackStack(null)
                    .commit()
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
        Toast.makeText(this.context, getString(R.string.message_login_success), Toast.LENGTH_LONG).show()
        val launchIntent = MainActivity.newLaunchIntent(this.context!!, this.user)
        this.startActivity(launchIntent)
        this.activity!!.finish()
    }

    private fun onLoginFailed() {
        Toast.makeText(this.context, getString(R.string.message_login_failed), Toast.LENGTH_LONG).show()
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