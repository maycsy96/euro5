package learn.apptivitylab.com.petrolnav.ui

import learn.apptivitylab.com.petrolnav.model.User

/**
 * Created by apptivitylab on 08/02/2018.
 */
interface onUserListListener {
    fun onUpdateUserList(userList: ArrayList<User>)
}