package learn.apptivitylab.com.petrolnav.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.User

/**
 * Created by apptivitylab on 07/02/2018.
 */
class ForgotPasswordFragment : Fragment() {
    companion object {
        const val ARG_USER_LIST = "user_list"
        fun newInstance(userList: ArrayList<User>): ForgotPasswordFragment {
            val fragment = ForgotPasswordFragment()
            val args: Bundle = Bundle()
            args.putParcelableArrayList(ARG_USER_LIST, userList)
            fragment.arguments = args
            return fragment
        }
    }

    private var userList = ArrayList<User>()
    private var user = User()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            this.userList = it.getParcelableArrayList(ARG_USER_LIST)
        }

        this.sendEmailButton.setOnClickListener {
            val isValid = this.validateTextInput()
            if (isValid) {
                this.sendEmail()
            }
        }

        this.cancelButton.setOnClickListener {
            this.activity!!.supportFragmentManager
                    .popBackStackImmediate()
        }
    }

    private fun validateTextInput(): Boolean {
        var isValid = true
        val emailText = this.emailEditText.text.toString()

        if (emailText.isEmpty()) {
            this.emailEditText.error = getString(R.string.message_invalid_email_address)
            isValid = false
        } else if ((this.userList.firstOrNull { it.userEmail == emailText }) == null) {
            this.emailEditText.error = getString(R.string.message_email_do_not_exist)
            isValid = false
        } else {
            this.emailEditText.error = null
        }

        return isValid
    }

    private fun sendEmail() {
        val emailText = this.emailEditText.text.toString()
        this.user = this.userList.first { it.userEmail == emailText }

        this.onSendEmailSuccess()
    }

    private fun onSendEmailSuccess() {
        Toast.makeText(this.context, getString(R.string.message_send_email_success), Toast.LENGTH_LONG).show()
        this.activity!!.supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_right_exit, R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit)
                .replace(R.id.forgotPasswordViewGroupContainer, ResetPasswordFragment.newInstance(this.user, this.userList))
                .addToBackStack(null)
                .commit()
    }
}