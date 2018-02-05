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
        const val ARG_USER_DETAIL = "user_detail"

        fun newLaunchIntent(context: Context, user: User): Intent {
            val intent = Intent(context, PetrolPriceActivity::class.java)
            intent.putExtra(ARG_USER_DETAIL, user)
            return intent
        }
    }

    private lateinit var pagerAdapter: PetrolPricePagerAdapter
    private var petrolList: ArrayList<Petrol> = ArrayList<Petrol>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_petrol_price)

        setSupportActionBar(this.toolbar)
        this.supportActionBar?.title = getString(R.string.petrol_price_history)
        this.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        this.toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

        this.petrolList = PetrolLoader.loadJSONPetrols(this)

        val item = intent.getParcelableExtra<User>(ARG_USER_DETAIL)
        this.pagerAdapter = PetrolPricePagerAdapter(this.supportFragmentManager, this.petrolList)
        this.petrolPriceViewPagerContainer.adapter = this.pagerAdapter
        this.petrolPriceTabLayout.setupWithViewPager(this.petrolPriceViewPagerContainer)

    }
}