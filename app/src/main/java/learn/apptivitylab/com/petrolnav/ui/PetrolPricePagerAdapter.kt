package learn.apptivitylab.com.petrolnav.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import learn.apptivitylab.com.petrolnav.model.Petrol

/**
 * Created by apptivitylab on 05/02/2018.
 */
class PetrolPricePagerAdapter(fragmentManager: FragmentManager, private val petrolList: ArrayList<Petrol>) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return PetrolPriceFragment.newInstance(petrolList[position])
    }

    override fun getCount(): Int {
        return petrolList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        title = petrolList[position].petrolName
        return title
    }
}