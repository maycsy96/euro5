package learn.apptivitylab.com.petrolnav.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_reset_password.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.User

/**
 * Created by apptivitylab on 07/02/2018.
 */
class ResetPasswordFragment : Fragment() {
    companion object {
        const val ARG_USER_DETAIL = "user_detail"
        fun newInstance(user: User): ResetPasswordFragment {
            val fragment = ResetPasswordFragment()
            val args: Bundle = Bundle()
            args.putParcelable(ARG_USER_DETAIL, user)
            fragment.arguments = args
            return fragment
        }
    }

    private var user = User()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reset_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            this.user = it.getParcelable(ARG_USER_DETAIL)
        }

        this.resetButton.setOnClickListener {
            val isValid = this.validateTextInput()
            if (isValid) {
                this.resetPassword()
            }
        }
    }

    private fun validateTextInput(): Boolean {
        var isValid = true
        var passwordText = this.passwordEditText.text.toString()
        var confirmPasswordText = this.confirmPasswordEditText.text.toString()

        if (passwordText.isEmpty() || passwordText.length < 4) {
            this.passwordEditText.error = getString(R.string.message_invalid_length_password)
        } else {
            this.passwordEditText.error = null
        }

        this.confirmPasswordEditText.error = when {
            confirmPasswordText.isEmpty() -> getString(R.string.message_invalid_confirm_password)
            confirmPasswordText != passwordText -> getString(R.string.message_mismatch_confirm_password)
            else -> null
        }

        if (this.confirmPasswordEditText.error != null) {
            isValid = false
        }

        return isValid
    }

    private fun resetPassword() {
        this.resetPasswordSuccess()
    }

    private fun resetPasswordSuccess() {
        Toast.makeText(this.context, getString(R.string.message_reset_password_success), Toast.LENGTH_LONG).show()
        this.activity!!.supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
    }
}

