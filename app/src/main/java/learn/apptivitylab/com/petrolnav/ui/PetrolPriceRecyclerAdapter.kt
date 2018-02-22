package learn.apptivitylab.com.petrolnav.ui

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.cell_price_history.view.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.PriceHistory
import java.text.SimpleDateFormat


/**
 * Created by apptivitylab on 31/01/2018.
 */
class PetrolPriceRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var priceHistoryList = ArrayList<PriceHistory>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return PriceHistoryViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_price_history, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val priceHistoryHolder = holder as PriceHistoryViewHolder
        val priceHistory = this.priceHistoryList[position]
        priceHistoryHolder.setPriceHistory(priceHistory, this.priceHistoryList)
    }

    override fun getItemCount(): Int {
        return this.priceHistoryList.size
    }

    fun updateDataSet(priceHistoryList: ArrayList<PriceHistory>) {
        this.priceHistoryList.clear()
        this.priceHistoryList.addAll(priceHistoryList)
        this.notifyDataSetChanged()
    }

    class PriceHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var priceHistory: PriceHistory? = null
        private val dateFormatter = SimpleDateFormat("dd/MM/yyyy")

        fun setPriceHistory(priceHistory: PriceHistory, priceHistoryList: ArrayList<PriceHistory>) {
            this.priceHistory = priceHistory
            this.itemView.dateTextView.text = dateFormatter.format(this.priceHistory?.dateCreated)
            this.itemView.priceTextView.text = itemView.context?.getString(R.string.petrol_price_value, this.priceHistory?.price)
            this.itemView.priceChangeTextView.text

            val position = priceHistoryList.indexOf(priceHistory)
            val previousPrice = priceHistoryList[position].price
            previousPrice?.let {
                val priceChange = priceHistoryList[position + 1].price?.minus(it)
                priceChange?.let {
                    if (it != null) {
                        this.itemView.priceChangeTextView.text = String.format("%.2f", it)
                    } else {
                        this.itemView.priceChangeTextView.text = itemView.context.getString(R.string.message_unavailable_price_change)
                    }
                    this.itemView.priceChangeTextView.setTextColor(when {
                        it < 0.0f -> Color.RED
                        it > 0.0f -> Color.GREEN
                        else -> Color.BLACK
                    })
                }
            }
        }
    }
}

