package learn.apptivitylab.com.petrolnav.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_reset_password.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.controller.UserController
import learn.apptivitylab.com.petrolnav.model.User

/**
 * Created by apptivitylab on 07/02/2018.
 */
class ResetPasswordFragment : Fragment() {
    companion object {
        const val ARG_USER_DETAIL = "user_detail"
        const val ARG_USER_LIST = "user_list"
        fun newInstance(user: User, userList: ArrayList<User>): ResetPasswordFragment {
            val fragment = ResetPasswordFragment()
            val args: Bundle = Bundle()
            args.putParcelable(ARG_USER_DETAIL, user)
            args.putParcelableArrayList(ARG_USER_LIST, userList)
            fragment.arguments = args
            return fragment
        }
    }

    private var userList = ArrayList<User>()
    private var user = User()
    private lateinit var userListListener: onUserListListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            this.userListListener = context as onUserListListener
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateUserList(userList: ArrayList<User>) {
        this.userListListener.onUpdateUserList(userList)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reset_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            this.user = it.getParcelable(ARG_USER_DETAIL)
            this.userList = it.getParcelableArrayList(ARG_USER_LIST)
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
        this.user.userPassword = this.passwordEditText.text.toString()

        this.userList.forEach { user ->
            if (user.userEmail == this.user.userEmail) {
                user.userPassword = this.user.userPassword
            }
        }
        UserController.writeToJSONUserList(this.activity!!.applicationContext, this.userList)
        this.updateUserList(this.userList)
        this.resetPasswordSuccess()
    }

    private fun resetPasswordSuccess() {
        Toast.makeText(this.context, getString(R.string.message_reset_password_success), Toast.LENGTH_LONG).show()
        this.activity!!.supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
    }
}

