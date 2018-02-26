package learn.apptivitylab.com.petrolnav.ui

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.android.volley.VolleyError
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.api.RestAPIClient
import learn.apptivitylab.com.petrolnav.controller.PetrolLoader
import learn.apptivitylab.com.petrolnav.controller.PetrolStationBrandLoader
import learn.apptivitylab.com.petrolnav.controller.PetrolStationLoader

/**
 * Created by apptivitylab on 23/02/2018.
 */
class SplashScreenActivity : AppCompatActivity(), RestAPIClient.ReceiveCompleteDataListener {
    companion object {
        private const val RESOURCE_TOTAL_COUNT = 2
    }

    private var resourcesCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_splash_screen)
        this.loadAllData()
    }

    private fun loadAllData() {
        PetrolLoader.loadJSONPetrols(this, this)
        PetrolStationBrandLoader.loadJSONPetrolStationBrands(this, this)
    }

    override fun onCompleteDataReceived(dataReceived: Boolean, error: VolleyError?) {
        if (dataReceived || error == null) {
            resourcesCount++
            if (resourcesCount == RESOURCE_TOTAL_COUNT) {
                val launchIntent = LoginActivity.newLaunchIntent(this)
                this.startActivity(launchIntent)
                this.finish()
            }
        } else {
            AlertDialog.Builder(this)
                    .setTitle(getString(R.string.title_retrieval_data_fail))
                    .setMessage(getString(R.string.message_retrieval_data_fail))
                    .setPositiveButton(getString(R.string.button_ok), { dialog, which ->
                        this.loadAllData()
                    })
                    .show()
        }
    }
}