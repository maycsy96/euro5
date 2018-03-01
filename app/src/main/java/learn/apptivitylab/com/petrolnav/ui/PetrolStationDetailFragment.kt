package learn.apptivitylab.com.petrolnav.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_petrol_station_detail.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.model.PetrolStation
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * Created by apptivitylab on 17/01/2018.
 */
class PetrolStationDetailFragment : Fragment(), AsyncImageListener {

    companion object {
        private val PETROL_STATION_DETAIL = "petrol_station_detail"

        fun newInstance(petrolStation: PetrolStation): PetrolStationDetailFragment {
            val fragment = PetrolStationDetailFragment()
            val args: Bundle = Bundle()
            args.putParcelable(PETROL_STATION_DETAIL, petrolStation)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var petrolStationSelected: PetrolStation

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_petrol_station_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            this.petrolStationSelected = it.getParcelable(PETROL_STATION_DETAIL)
        }

        val url = "http://maps.google.com/maps/api/staticmap?center=" + this.petrolStationSelected.petrolStationLatLng?.latitude + "," + this.petrolStationSelected.petrolStationLatLng?.longitude + "&zoom=15&size=" + this.stationMapImageView.layoutParams.width + "x180&sensor=false"
        DownloadStaticMapTask(this).execute(url)

        this.petrolStationNameTextView.text = this.petrolStationSelected.petrolStationName
        this.petrolStationBrandTextView.text = this.petrolStationSelected.petrolStationBrand
        this.petrolStationBrandTextView.setCompoundDrawables(ResourcesCompat.getDrawable(resources,
                when (this.petrolStationSelected?.petrolStationBrand) {
                    "Shell" -> R.drawable.shell_logo
                    "Petronas" -> R.drawable.petronas_logo
                    "Petron" -> R.drawable.petron_logo
                    "Caltex" -> R.drawable.caltex_logo
                    "BHPetrol" -> R.drawable.bhpetrol_logo
                    else -> R.drawable.app_logo_greyed
                }, null), null, null, null)

        if (this.petrolStationSelected.distanceFromUser != null) {
            this.petrolStationDistanceTextView.text = getString(R.string.distance_value, this.petrolStationSelected.distanceFromUser)
        } else {
            this.petrolStationDistanceTextView.text = context?.getString(R.string.message_unavailable_distance)
        }

        this.petrolTextView.text = this.petrolStationSelected.petrolList
                ?.map { it.petrolName }
                ?.joinToString(" , ")

        this.navigationButton.setOnClickListener {
            this.navigateToPetrolStation(this.petrolStationSelected)
        }
    }

    private fun navigateToPetrolStation(petrolStation: PetrolStation) {
        petrolStation.petrolStationLatLng?.let {
            val locationUri = Uri.parse("geo:0,0?q=${it.latitude},${it.longitude}")
            val navigateIntent = Intent(Intent.ACTION_VIEW, locationUri)
            val packageManager = context!!.packageManager
            val availableApps = packageManager.queryIntentActivities(navigateIntent, PackageManager.MATCH_DEFAULT_ONLY)
            val isIntentSafe = availableApps.size > 0
            val chooser = Intent.createChooser(navigateIntent, getString(R.string.navigate))
            if (isIntentSafe) {
                this.startActivity(chooser)
            } else {
                this.view?.let {
                    Snackbar.make(it, getString(R.string.message_unavailable_app), Snackbar.LENGTH_LONG)
                }
            }
        }
    }

    override fun getBitmap(bitmap: Bitmap?) {
        this.stationMapImageView.setImageBitmap(bitmap)
    }

    inner class DownloadStaticMapTask(private val listener: AsyncImageListener) : AsyncTask<String, Int, Bitmap?>() {
        override fun doInBackground(vararg args: String?): Bitmap? {
            var bitmap: Bitmap? = null

            try {
                val url = URL(args[0])
                val urlConnection = url.openConnection() as HttpURLConnection
                val inputStream = urlConnection.inputStream
                bitmap = BitmapFactory.decodeStream(inputStream)
                return bitmap
            } catch (e: Exception) {
                when (e) {
                    is IOException, is MalformedURLException -> e.printStackTrace()
                    else -> throw e
                }
            }
            return null
        }

        override fun onPostExecute(result: Bitmap?) {
            listener.getBitmap(result)
        }
    }
}