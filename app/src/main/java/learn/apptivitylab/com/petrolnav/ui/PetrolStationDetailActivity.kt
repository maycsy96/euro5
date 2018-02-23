package learn.apptivitylab.com.petrolnav.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_petrol_station_detail.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.PetrolStation

/**
 * Created by apptivitylab on 18/01/2018.
 */
class PetrolStationDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PETROL_STATION = "PETROL STATION"

        fun newLaunchIntent(context: Context, petrolStation: PetrolStation): Intent {
            val intent = Intent(context, PetrolStationDetailActivity::class.java)
            intent.putExtra(EXTRA_PETROL_STATION, petrolStation)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_petrol_station_detail)

        val item = intent.getParcelableExtra<PetrolStation>(EXTRA_PETROL_STATION)

        this.setSupportActionBar(this.toolbar)
        this.supportActionBar?.title = getString(R.string.petrol_price_history)
        this.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        this.toolbar.setNavigationOnClickListener(View.OnClickListener {
            this.finish()
        })

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.petrolStationDetailViewgroupContainer, PetrolStationDetailFragment.newInstance(item))
                .commit()
    }

}