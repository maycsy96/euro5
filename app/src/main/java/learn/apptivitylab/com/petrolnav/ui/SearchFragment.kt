package learn.apptivitylab.com.petrolnav.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import learn.apptivitylab.com.petrolnav.R
import kotlinx.android.synthetic.main.fragment_search.*
import learn.apptivitylab.com.petrolnav.model.PetrolStation

/**
 * Created by apptivitylab on 09/01/2018.
 */

class SearchFragment : Fragment(), SearchAdapter.StationViewHolder.onSelectStationListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(this.activity,LinearLayoutManager.VERTICAL,false)
        petrolStationListRecyclerView.layoutManager = layoutManager

        val petrolStationListAdapter = SearchAdapter()
        petrolStationListAdapter.setStationListener(this)
        petrolStationListRecyclerView.adapter = petrolStationListAdapter
        //petrolStationListAdapter.updateDataSet(MockDataLoader.loadStations(context!!))
    }

    override fun onStationSelected(petrolStation: PetrolStation) {
        val intent = Intent(context, PetrolStationDetailActivity::class.java)
        intent.putExtra("Selected Station", petrolStation)
        startActivity(intent)
    }

    companion object {
        private val TAG = "SearchFragment"
    }
}
