package learn.apptivitylab.com.petrolnav.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.controller.UserController
import learn.apptivitylab.com.petrolnav.model.User

/**
 * Created by apptivitylab on 08/01/2018.
 */

class LoginActivity : AppCompatActivity(), onUserListListener {

    companion object {
        val REQUEST_SIGNUP = 0
    }

    private var userList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_login)

        UserController.loadJSONUserList(this)?.let {
            this.userList = it
        }

        this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.loginViewgroupContainer, LoginFragment.newInstance(this.userList))
                .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == Activity.RESULT_OK) {
                this.userList = data.getParcelableArrayListExtra(RegisterActivity.EXTRA_USER_LIST)
            }
        }
    }

    override fun onPassUserList(userList: ArrayList<User>) {
        this.userList = userList
    }
}


