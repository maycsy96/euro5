package learn.apptivitylab.com.petrolnav.controller

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.PetrolStation
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by apptivitylab on 16/01/2018.
 */
class PetrolStationLoader {

    fun loadStations (context: Context) : ArrayList<PetrolStation> {
        val listOfStations: ArrayList<PetrolStation> = ArrayList()

        val inputStream : InputStream = context.resources.openRawResource(R.raw.stations)
        val reader : BufferedReader = BufferedReader(InputStreamReader(inputStream))

        reader.forEachLine {
            val tokens : List<String> = it.split(";")

            val stationID = tokens[0]
            val stationName = tokens[1]
            val stationBrand = tokens[2]
            val stationLatLng = LatLng(tokens[3].toDouble(), tokens[4].toDouble())

            val station : PetrolStation = PetrolStation(stationID, stationName, stationBrand, stationLatLng)
            listOfStations.add(station)
        }

        return listOfStations
    }

}