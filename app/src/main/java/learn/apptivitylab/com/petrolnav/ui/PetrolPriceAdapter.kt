package learn.apptivitylab.com.petrolnav.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.cell_header.view.*
import kotlinx.android.synthetic.main.cell_petrol.view.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.Petrol

/**
 * Created by apptivitylab on 31/01/2018.
 */
class PetrolPriceAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object {
        private val PETROL_HEADER = 0
        private val PETROL_ITEM = 1
    }

    private var petrolList: ArrayList<Any> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder = when (viewType){
            PETROL_ITEM -> {
                PetrolViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_petrol, parent, false))
            }
            else -> {
                HeaderViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_header, parent, false))
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            PETROL_ITEM -> {
                val petrolViewHolder: PetrolViewHolder = holder as PetrolViewHolder
                val petrol: Petrol = petrolList[position] as Petrol
                petrolViewHolder.setPetrol(petrol)
            }
            else -> {
                val headerViewHolder: HeaderViewHolder = holder as HeaderViewHolder
                val header = petrolList[position] as String
                headerViewHolder.setHeaderTitle(header)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (petrolList[position] is Petrol) {
            return PETROL_ITEM
        } else {
            return PETROL_HEADER
        }
    }

    override fun getItemCount(): Int {
        return petrolList.size
    }

    fun updateDataSet(petrolList: ArrayList<Any>){
        this.petrolList.clear()
        this.petrolList.addAll(petrolList)
        this.notifyDataSetChanged()
    }

    class PetrolViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        private var petrol: Petrol?= null

        fun setPetrol(petrol: Petrol){
            this.petrol = petrol
            itemView.petrolIdTextView.text = this.petrol?.petrolId
            itemView.petrolNameTextView.text = this.petrol?.petrolName
            itemView.petrolPriceTextView.text = "%.2f".format(this.petrol?.petrolPrice)
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerTitle = itemView.headerTextView
        fun setHeaderTitle(header: String) {
            headerTitle.text = header
        }
    }
}

