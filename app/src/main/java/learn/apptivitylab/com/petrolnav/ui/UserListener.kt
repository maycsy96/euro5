package learn.apptivitylab.com.petrolnav.ui

import learn.apptivitylab.com.petrolnav.model.User

/**
 * Created by apptivitylab on 09/02/2018.
 */
interface UserListener {
    fun onUpdateUser(user: User)
}