package learn.apptivitylab.com.petrolnav.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import learn.apptivitylab.com.petrolnav.R

/**
 * Created by apptivitylab on 18/01/2018.
 */
class PetrolStationDetailActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_petrol_station_detail)

        val item = intent.getParcelableExtra<PetrolStation>(getString(R.string.selected_station))
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.petrolStationDetailViewgroupContainer,PetrolStationDetailFragment())
                .commit()
    }

}