package learn.apptivitylab.com.petrolnav.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import learn.apptivitylab.com.petrolnav.R

/**
 * Created by apptivitylab on 17/01/2018.
 */
class SearchActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.searchViewgroupContainer,SearchFragment())
                .commit()
    }

    companion object {
        private val TAG = "Search Activity"
    }
}