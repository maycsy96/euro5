package learn.apptivitylab.com.petrolnav.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import learn.apptivitylab.com.petrolnav.R
import kotlinx.android.synthetic.main.fragment_preferences.*
import learn.apptivitylab.com.petrolnav.controller.PetrolLoader
import learn.apptivitylab.com.petrolnav.controller.PetrolStationBrandLoader
import learn.apptivitylab.com.petrolnav.controller.UserController
import learn.apptivitylab.com.petrolnav.model.Petrol
import learn.apptivitylab.com.petrolnav.model.PetrolStationBrand
import learn.apptivitylab.com.petrolnav.model.User
import android.content.Context.MODE_PRIVATE
import java.io.*


/**
 * Created by apptivitylab on 12/01/2018.
 */
class PreferencesFragment : Fragment() {

    companion object {
        private val TAG = "PreferencesFragment"

        const val ARG_USER_DETAIL = "user_detail"
        fun newInstance(user: User): PreferencesFragment {
            val fragment = PreferencesFragment()
            val args: Bundle = Bundle()
            args.putParcelable(ARG_USER_DETAIL, user)
            fragment.arguments = args
            return fragment
        }
    }

    private var petrolStationBrandList = ArrayList<PetrolStationBrand>()
    private var petrolList = ArrayList<Petrol>()
    private var user = User()

    private var checkBoxByPetrolStationBrand: LinkedHashMap<PetrolStationBrand, CheckBox> = LinkedHashMap()
    private var radioButtonByPetrol: LinkedHashMap<Petrol, RadioButton> = LinkedHashMap()

    private var preferredPetrolStationBrandList = ArrayList<PetrolStationBrand>()
    private var preferredPetrol = Petrol()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_preferences, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.petrolStationBrandList = PetrolStationBrandLoader.loadJSONPetrolStationBrands(context!!)
        this.petrolList = PetrolLoader.loadJSONPetrols(context!!)

        arguments?.let {
            this.user = it.getParcelable(ARG_USER_DETAIL)
        }

        for (petrolStationBrand in this.petrolStationBrandList) {
            when (petrolStationBrand.petrolStationBrandName) {
                "Shell" -> this.checkBoxByPetrolStationBrand.put(petrolStationBrand, shellCheckbox)
                "Petronas" -> this.checkBoxByPetrolStationBrand.put(petrolStationBrand, petronasCheckbox)
                "BHPetrol" -> this.checkBoxByPetrolStationBrand.put(petrolStationBrand, bhpetrolCheckbox)
                "Caltex" -> this.checkBoxByPetrolStationBrand.put(petrolStationBrand, caltexCheckbox)
            }
        }

        for (petrol in this.petrolList) {
            when (petrol.petrolName) {
                "RON95" -> this.radioButtonByPetrol.put(petrol, ron95RadioButton)
                "RON97" -> this.radioButtonByPetrol.put(petrol, ron97RadioButton)
                "Diesel" -> this.radioButtonByPetrol.put(petrol, dieselRadioButton)
                "EURO5" -> this.radioButtonByPetrol.put(petrol, euro5RadioButton)
            }
        }

        presetPreference(this.user, this.checkBoxByPetrolStationBrand, this.radioButtonByPetrol)

        this.saveButton.setOnClickListener {
            for ((petrolStationBrand, checkBox) in this.checkBoxByPetrolStationBrand) {
                if (checkBox.isChecked) {
                    this.preferredPetrolStationBrandList.add(petrolStationBrand)
                }
            }

            for ((petrol, radioButton) in this.radioButtonByPetrol) {
                if (radioButton.isChecked) {
                    this.preferredPetrol = petrol
                }
            }
            savePreference(this.user, this.preferredPetrolStationBrandList, this.preferredPetrol)
        }
    }

    private fun presetPreference(user: User, checkBoxByPetrolStationBrand: HashMap<PetrolStationBrand, CheckBox>, radioButtonByPetrol: HashMap<Petrol, RadioButton>) {
        var petrolRadioButtonisChecked = false
        for ((petrolStationBrand, checkBox) in checkBoxByPetrolStationBrand) {
            user.userPreferredPetrolStationBrandList?.let {
                for (preferredPetrolStationBrand in it) {
                    if (petrolStationBrand.petrolStationBrandId == preferredPetrolStationBrand.petrolStationBrandId) {
                        checkBox.isChecked = true
                    }
                }
            }
        }
        for ((petrol, radioButton) in radioButtonByPetrol) {
            if (petrol.petrolId == user.userPreferredPetrol?.petrolId) {
                radioButton.isChecked = true
                petrolRadioButtonisChecked = true
                return
            }
        }
        if (petrolRadioButtonisChecked != true) {
            ron95RadioButton.isChecked = true
        }
    }

    private fun savePreference(user: User, preferredPetrolStationBrandList: ArrayList<PetrolStationBrand>, preferredPetrol: Petrol) {
        user.userPreferredPetrolStationBrandList = preferredPetrolStationBrandList
        user.userPreferredPetrol = preferredPetrol

        var userJsonObject = user.toJsonObject()

        try {
            val outputStream = context?.openFileOutput("user.txt", Context.MODE_PRIVATE)
            outputStream?.flush()
            outputStream?.write(userJsonObject.toString().toByteArray())
            outputStream?.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.mainViewgroupContainer, MapDisplayFragment.newInstance(user))
                .commit()
    }


}