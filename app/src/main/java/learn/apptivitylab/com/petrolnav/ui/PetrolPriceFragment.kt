package learn.apptivitylab.com.petrolnav.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_petrol_price.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.controller.PetrolLoader
import learn.apptivitylab.com.petrolnav.model.Petrol
import learn.apptivitylab.com.petrolnav.model.User

/**
 * Created by apptivitylab on 12/01/2018.
 */
class PetrolPriceFragment : Fragment() {

    companion object {
        private val ARG_USER_DETAIL = "user_detail"

        fun newInstance(user: User): PetrolPriceFragment {
            val fragment = PetrolPriceFragment()
            val args: Bundle = Bundle()
            args.putParcelable(ARG_USER_DETAIL, user)
            fragment.arguments = args
            return fragment
        }
    }

    private var petrolList: ArrayList<Petrol>? = null
    private var user = User()
    val petrolListAdapter = PetrolPriceAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_petrol_price, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            user = it.getParcelable(ARG_USER_DETAIL)
        }

        val layoutManager = LinearLayoutManager(this.activity, LinearLayoutManager.VERTICAL, false)
        this.petrolListRecyclerView.layoutManager = layoutManager

        this.petrolListRecyclerView.adapter = petrolListAdapter
        this.petrolList = PetrolLoader.loadJSONPetrols(this.context!!)
        this.petrolListAdapter.updateDataSet(this.petrolList as ArrayList<Petrol>)
    }
}