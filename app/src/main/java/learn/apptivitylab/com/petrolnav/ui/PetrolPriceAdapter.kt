package learn.apptivitylab.com.petrolnav.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.cell_petrol.view.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.Petrol

/**
 * Created by apptivitylab on 31/01/2018.
 */
class PetrolPriceAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var petrolList: ArrayList<Petrol> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return PetrolViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_petrol, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val petrolViewHolder: PetrolViewHolder = holder as PetrolViewHolder
        val petrol: Petrol = petrolList[position]
        petrolViewHolder.setPetrol(petrol)
    }

    override fun getItemCount(): Int {
        return petrolList.size
    }

    fun updateDataSet(petrolList: ArrayList<Petrol>) {
        this.petrolList.clear()
        this.petrolList.addAll(petrolList)
        this.notifyDataSetChanged()
    }

    class PetrolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var petrol: Petrol? = null

        fun setPetrol(petrol: Petrol) {
            this.petrol = petrol
            itemView.petrolIdTextView.text = this.petrol?.petrolId
            itemView.petrolNameTextView.text = this.petrol?.petrolName
            itemView.petrolPriceTextView.text = "%.2f".format(this.petrol?.petrolPrice)
        }
    }
}

