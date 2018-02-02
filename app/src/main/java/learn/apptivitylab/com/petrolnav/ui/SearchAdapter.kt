package learn.apptivitylab.com.petrolnav.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.cell_header_petrol_station.view.*
import kotlinx.android.synthetic.main.cell_petrol_station.view.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.PetrolStation

/**
 * Created by apptivitylab on 17/01/2018.
 */
class SearchAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private val STATION_HEADER = 0
        private val STATION_ITEM = 1
    }

    private var petrolStationsList: ArrayList<Any> = ArrayList()
    private lateinit var petrolStationListener: StationViewHolder.onSelectStationListener

    fun setStationListener(petrolStationListener: StationViewHolder.onSelectStationListener) {
        this.petrolStationListener = petrolStationListener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {

        var viewHolder: RecyclerView.ViewHolder = when (viewType) {
            STATION_ITEM -> {
                StationViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_petrol_station,
                        parent, false), petrolStationListener)
            }
            else -> {
                HeaderViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_header_petrol_station,
                        parent, false))
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            STATION_ITEM -> {
                val stationViewHolder: StationViewHolder = holder as StationViewHolder
                val station: PetrolStation = petrolStationsList[position] as PetrolStation
                stationViewHolder.setStation(station)
            }
            else -> {
                val headerViewHolder: HeaderViewHolder = holder as HeaderViewHolder
                val header = petrolStationsList[position] as String
                headerViewHolder.setHeaderTitle(header)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (petrolStationsList[position] is PetrolStation) {
            return STATION_ITEM
        } else {
            return STATION_HEADER
        }
    }

    override fun getItemCount(): Int {
        return petrolStationsList.size
    }

    fun updateDataSet(stations: ArrayList<Any>) {
        this.petrolStationsList.clear()
        this.petrolStationsList.addAll(stations)
        this.notifyDataSetChanged()
    }

    class StationViewHolder(itemView: View, handler: onSelectStationListener)
        : RecyclerView.ViewHolder(itemView) {

        interface onSelectStationListener {
            fun onStationSelected(petrolStation: PetrolStation)
        }

        private var petrolStation: PetrolStation? = null

        init {
            itemView.setOnClickListener({
                handler.onStationSelected(petrolStation!!)
            })
        }

        fun setStation(station: PetrolStation) {
            this.petrolStation = station
            itemView.petrolStationIdTextView.text = this.petrolStation?.petrolStationId
            itemView.petrolStationNameTextView.text = this.petrolStation?.petrolStationName
            itemView.petrolStationBrandTextView.text = this.petrolStation?.petrolStationBrand
            itemView.petrolStationAddressTextView.text = this.petrolStation?.petrolStationAddress

            if (petrolStation?.distanceFromUser != null) {
                itemView.petrolStationDistanceTextView.text = "%.2f".format(this.petrolStation?.distanceFromUser)
            } else {
                itemView.petrolStationDistanceTextView.text = itemView.context.getString(R.string.message_unavailable_location)
            }
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerTitle = itemView.headerTextView
        fun setHeaderTitle(header: String) {
            headerTitle.text = header
        }
    }
}