package learn.apptivitylab.com.petrolnav.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.cell_header_petrol.view.*
import kotlinx.android.synthetic.main.cell_price_history.view.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.Petrol
import learn.apptivitylab.com.petrolnav.model.PriceHistory
import java.text.SimpleDateFormat


/**
 * Created by apptivitylab on 31/01/2018.
 */
class PetrolPriceAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private val HEADER = 0
        private val ITEM = 1
    }

    private var petrolList: ArrayList<Petrol> = ArrayList()
    private var petrolStates: HashMap<Petrol, Boolean> = HashMap()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER -> {
                PetrolViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_header_petrol, parent, false))
            }
            else -> {
                PriceHistoryViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_price_history, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (item) {
            is Petrol -> {
                (holder as PetrolViewHolder).itemView.tag = holder
                (holder as PetrolViewHolder).setPetrol(item)
                holder.itemView.setOnClickListener(this)
            }
            is PriceHistory -> {
                (holder as PriceHistoryViewHolder).itemView.tag = holder
                (holder as PriceHistoryViewHolder).setPriceHistory(item)
            }
        }
    }

    }

    override fun getItemCount(): Int {
        var historyCount = 0
        this.petrolList.forEach {
            historyCount += it.petrolPriceHistoryList.count()
        }
        return petrolList.size + historyCount
    }

    override fun getItemViewType(position: Int): Int {
        if (getItem(position) is Petrol) {
            return HEADER
        } else {
            return ITEM
        }
    }

    fun updateDataSet(petrolList: ArrayList<Petrol>) {
        this.petrolList.clear()
        this.petrolList.addAll(petrolList)
        petrolList.forEach {
            if (!this.petrolStates.containsKey(it)) {
                this.petrolStates.put(it, false)
            }
        }
        this.notifyDataSetChanged()
    }

    class PetrolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var petrol: Petrol? = null

        fun setPetrol(petrol: Petrol) {
            this.petrol = petrol
            itemView.petrolIdTextView.text = this.petrol?.petrolId
            itemView.petrolNameTextView.text = this.petrol?.petrolName
            itemView.petrolPriceTextView.text = itemView.context?.getString(R.string.petrol_price_value, this.petrol?.petrolPrice)
        }
    }

    class PriceHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var priceHistory: PriceHistory? = null
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")

        fun setPriceHistory(priceHistory: PriceHistory) {
            this.priceHistory = priceHistory
            itemView.dateTextView.text = itemView.context?.getString(R.string.petrol_price_date, dateFormatter.format(this.priceHistory?.dateFrom), dateFormatter.format(this.priceHistory?.dateTo))
            itemView.priceTextView.text = itemView.context?.getString(R.string.petrol_price_value, this.priceHistory?.price)
        }
    }
}

