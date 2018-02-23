package learn.apptivitylab.com.petrolnav.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.controller.PetrolLoader
import learn.apptivitylab.com.petrolnav.controller.PetrolStationBrandLoader

/**
 * Created by apptivitylab on 08/01/2018.
 */

class LoginActivity : AppCompatActivity() {

    companion object {
        val REQUEST_SIGNUP = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_login)

        PetrolLoader.loadJSONPetrols(this)
        PetrolStationBrandLoader.loadJSONPetrolStationBrands(this)

        this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.loginViewgroupContainer, LoginFragment.newInstance())
                .commit()
    }
}


