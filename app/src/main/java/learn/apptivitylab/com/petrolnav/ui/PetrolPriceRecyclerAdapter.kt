package learn.apptivitylab.com.petrolnav.ui

import android.graphics.Color
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
class PetrolPriceRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private val HEADER = 0
        private val ITEM = 1
    }

    private var petrol: Petrol? = null

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

        when (holder) {
            is PetrolViewHolder -> {
                holder.itemView.tag = holder
                holder.setPetrol(item as Petrol)
            }

            is PriceHistoryViewHolder -> {
                holder.itemView.tag = holder
                holder.setPriceHistory(item as PriceHistory)
            }
        }
    }

    fun getItem(position: Int): Any {
        val petrolDetailList = ArrayList<Any>()
        val petrol = this.petrol
        petrolDetailList.add(petrol as Petrol)
        petrolDetailList.addAll(petrol.petrolPriceHistoryList)

        return petrolDetailList[position]
    }

    override fun getItemCount(): Int {
        var historyCount = 0
        this.petrol?.let {
            historyCount = it.petrolPriceHistoryList.count()
        }
        return 1 + historyCount
    }

    override fun getItemViewType(position: Int): Int {
        if (getItem(position) is Petrol) {
            return HEADER
        } else {
            return ITEM
        }
    }

    fun updateDataSet(petrol: Petrol) {
        this.petrol = petrol
        this.notifyDataSetChanged()
    }

    class PetrolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var petrol: Petrol? = null

        fun setPetrol(petrol: Petrol) {
            this.petrol = petrol
            itemView.petrolIdTextView.text = this.petrol?.petrolId
            itemView.petrolNameTextView.text = this.petrol?.petrolName
            itemView.petrolPriceTextView.text = itemView.context?.getString(R.string.petrol_price_value, this.petrol?.petrolPrice)

            petrol.petrolPriceChange?.let {
                if (it != null) {
                    itemView.petrolPriceChangeTextView.text = String.format("%.2f", it)
                } else {
                    itemView.petrolPriceChangeTextView.text = itemView.context.getString(R.string.message_unavailable_price_change)
                }


                if (it < 0.0f) {
                    itemView.petrolPriceChangeTextView.setTextColor(Color.RED)
                } else if (it > 0.0f) {
                    itemView.petrolPriceChangeTextView.setTextColor(Color.GREEN)
                } else {
                    itemView.petrolPriceChangeTextView.setTextColor(Color.BLACK)
                }
            }
        }
    }

    class PriceHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var priceHistory: PriceHistory? = null
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")

        fun setPriceHistory(priceHistory: PriceHistory) {
            this.priceHistory = priceHistory
            itemView.dateTextView.text = dateFormatter.format(this.priceHistory?.dateCreated)
            itemView.priceTextView.text = itemView.context?.getString(R.string.petrol_price_value, this.priceHistory?.price)
        }
    }
}

