package learn.apptivitylab.com.petrolnav.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.cell_petrol_station.view.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.PetrolStation

/**
 * Created by apptivitylab on 17/01/2018.
 */
class SearchAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var petrolStationsList : ArrayList<PetrolStation> = ArrayList()
    private lateinit var petrolStationListener:StationViewHolder.onSelectStationListener


    fun setStationListener(petrolStationListener: StationViewHolder.onSelectStationListener) {
        this.petrolStationListener = petrolStationListener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {

        return StationViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_petrol_station,
                parent, false), petrolStationListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val stationViewHolder : StationViewHolder = holder as StationViewHolder
        val station : PetrolStation = petrolStationsList[position]

        stationViewHolder.setStation(station)
    }

    override fun getItemCount(): Int {
        return petrolStationsList.size
    }

    fun updateDataSet(stations: ArrayList<PetrolStation>) {
        this.petrolStationsList.clear()
        this.petrolStationsList = stations
        this.notifyDataSetChanged()
    }

    class StationViewHolder(itemView: View, handler: onSelectStationListener)
        : RecyclerView.ViewHolder(itemView) {

        interface onSelectStationListener {
            fun onStationSelected(station: PetrolStation)
        }

        private val petrolStationName = itemView.petrolStationNameTextView
        private val petrolStationBrand = itemView.petrolStationBrandTextView
        private val petrolStationAddress = itemView.petrolStationAddressTextView

        private var petrolStation : PetrolStation? = null

        init {
            itemView.setOnClickListener({
                handler.onStationSelected(petrolStation!!)
            })
        }

        fun setStation(station: PetrolStation) {
            this.petrolStation = station

            updateViewHolder()
        }

        private fun updateViewHolder() {
            petrolStationName.text = petrolStation?.petrolStationName
            petrolStationBrand.text = petrolStation?.petrolStationBrand
        }
    }

}