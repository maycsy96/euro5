package learn.apptivitylab.com.petrolnav.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_petrol_price.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.Petrol

/**
 * Created by apptivitylab on 12/01/2018.
 */
class PetrolPriceFragment : Fragment() {

    companion object {
        private val ARG_PETROL_DETAIL = "petrol"

        fun newInstance(petrol: Petrol): PetrolPriceFragment {
            val fragment = PetrolPriceFragment()
            val args: Bundle = Bundle()
            args.putParcelable(ARG_PETROL_DETAIL, petrol)
            fragment.arguments = args
            return fragment
        }
    }

    private var petrol = Petrol()
    val petrolAdapter = PetrolPriceRecyclerAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_petrol_price, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            petrol = it.getParcelable(ARG_PETROL_DETAIL)
        }

        val layoutManager = LinearLayoutManager(this.activity, LinearLayoutManager.VERTICAL, false)
        this.petrolListRecyclerView.layoutManager = layoutManager
        this.petrolListRecyclerView.adapter = petrolAdapter

        setPetrolPriceChange(this.petrol)
        this.petrolAdapter.updateDataSet(this.petrol)
    }

    fun setPetrolPriceChange(petrol: Petrol) {
        var previousPrice = petrol.petrolPriceHistoryList.first().price

        previousPrice?.let {
            petrol.petrolPriceChange = petrol.petrolPrice?.minus(it)
        }

    }
}