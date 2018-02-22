package learn.apptivitylab.com.petrolnav.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_petrol_price.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.controller.PetrolLoader
import learn.apptivitylab.com.petrolnav.model.Petrol

/**
 * Created by apptivitylab on 31/01/2018.
 */
class PetrolPriceActivity : AppCompatActivity() {
    companion object {
        fun newLaunchIntent(context: Context): Intent {
            val intent = Intent(context, PetrolPriceActivity::class.java)
            return intent
        }
    }

    private lateinit var pagerAdapter: PetrolPricePagerAdapter
    private var petrolList: ArrayList<Petrol> = ArrayList<Petrol>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_petrol_price)

        this.setSupportActionBar(this.toolbar)
        this.supportActionBar?.title = getString(R.string.petrol_price_history)
        this.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        this.toolbar.setNavigationOnClickListener(View.OnClickListener {
            this.finish()
        })

        this.petrolList = PetrolLoader.petrolList

        this.pagerAdapter = PetrolPricePagerAdapter(this.supportFragmentManager, this.petrolList)
        this.petrolPriceViewPagerContainer.adapter = this.pagerAdapter
        this.petrolPriceTabLayout.setupWithViewPager(this.petrolPriceViewPagerContainer)
    }
}