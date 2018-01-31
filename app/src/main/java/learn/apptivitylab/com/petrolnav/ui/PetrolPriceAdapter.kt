package learn.apptivitylab.com.petrolnav.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.Petrol

/**
 * Created by apptivitylab on 31/01/2018.
 */
class PetrolPriceAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object {
        private val PRICE_HEADER = 0
        private val PRICE_ITEM = 1
    }

    private var petrolList: ArrayList<Any> = ArrayList()
    private lateinit var petrolListener: PetrolViewHolder.onSelectPetrolListener

    fun setPetrolListener(petrolListener: PetrolViewHolder.onSelectPetrolListener){
        this.petrolListener = petrolListener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder = when (viewType){
            PRICE_ITEM -> {
                PetrolViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_petrol, parent, false), petrolListener)
            }
            else -> {
                HeaderViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_header, parent, false))
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            PRICE_ITEM -> {
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
            return PRICE_ITEM
        } else {
            return PRICE_HEADER
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

    class PetrolViewHolder(itemView: View, handler: onSelectPetrolListener): RecyclerView.ViewHolder(itemView){
        interface onSelectPetrolListener{
            fun onPetrolSelected(petrol: Petrol)
        }

        private var petrol: Petrol?= null

        init{
            itemView.setOnClickListener({
                handler.onPetrolSelected(petrol!!)
            })
        }

        fun setPetrol(petrol: Petrol){

        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerTitle = itemView.headerTextView
        fun setHeaderTitle(header: String) {
            headerTitle.text = header
        }
    }
}

