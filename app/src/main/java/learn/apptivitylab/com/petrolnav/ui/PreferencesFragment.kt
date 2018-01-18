package learn.apptivitylab.com.petrolnav.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import learn.apptivitylab.com.petrolnav.R
import kotlinx.android.synthetic.main.fragment_preferences.*

/**
 * Created by apptivitylab on 12/01/2018.
 */
class PreferencesFragment: Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_preferences, container, false)

        return view
    }


}