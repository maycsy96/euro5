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
        fun newInstance(): ForgotPasswordFragment {
            val fragment = ForgotPasswordFragment()
            val args: Bundle = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var user = User()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        } else {
            this.emailEditText.error = null
        }

        return isValid
    }

    private fun sendEmail() {
        val emailText = this.emailEditText.text.toString()

        this.onSendEmailSuccess()
    }

    private fun onSendEmailSuccess() {
        Toast.makeText(this.context, getString(R.string.message_send_email_success), Toast.LENGTH_LONG).show()
        this.activity!!.supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_right_exit, R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit)
                .replace(R.id.forgotPasswordViewGroupContainer, ResetPasswordFragment.newInstance(this.user))
                .addToBackStack(null)
                .commit()
    }
}