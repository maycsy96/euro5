package learn.apptivitylab.com.petrolnav.ui

import android.app.Activity
import android.content.Intent
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

        this.loginButton.setOnClickListener {
            val isValid = this.validateTextInput()
            if (isValid) {
                this.loginAccount()
            } else {
                this.onLoginFailed()
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LoginActivity.REQUEST_SIGNUP) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let{
                    this.userList = it.getParcelableArrayListExtra(RegisterActivity.EXTRA_USER_LIST)
                }
            }
        }
    }

    private fun loginAccount() {
        this.loginButton.isEnabled = false

        val emailText = emailEditText.text.toString()
        this.user = this.userList.first { user -> user.userEmail == emailText }

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
        var isValid = true
        val emailText = this.emailEditText.text.toString()
        val passwordText = this.passwordEditText.text.toString()

        if (emailText.isEmpty()) {
            this.emailEditText.error = getString(R.string.message_invalid_email_address)
            isValid = false
        } else {
            this.emailEditText.error = null
        }

        if (passwordText.isEmpty()) {
            this.passwordEditText.error = getString(R.string.message_invalid_password)
            isValid = false
        } else {
            this.passwordEditText.error = null
        }

        if (isValid) {
            isValid = (this.userList.firstOrNull { it.userEmail == emailText && it.userPassword == passwordText }) != null
        }

        return isValid
    }
}