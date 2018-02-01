package learn.apptivitylab.com.petrolnav.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_petrol_price.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.User

/**
 * Created by apptivitylab on 31/01/2018.
 */
class PetrolPriceActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_USER_DETAIL = "user detail"

        fun newLaunchIntent(context: Context, user: User): Intent {
            val intent = Intent(context, PetrolPriceActivity::class.java)
            intent.putExtra(EXTRA_USER_DETAIL, user)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_petrol_price)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.petrol_price_history)

        val item = intent.getParcelableExtra<User>(EXTRA_USER_DETAIL)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.petrolPriceMainViewgroupContainer, PetrolPriceFragment.newInstance(item))
                .commit()
    }
}