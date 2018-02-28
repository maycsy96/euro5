package learn.apptivitylab.com.petrolnav.ui

import android.content.Context
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_preferences.*
import learn.apptivitylab.com.petrolnav.R
import learn.apptivitylab.com.petrolnav.controller.PetrolLoader
import learn.apptivitylab.com.petrolnav.controller.PetrolStationBrandLoader
import learn.apptivitylab.com.petrolnav.model.Petrol
import learn.apptivitylab.com.petrolnav.model.PetrolStationBrand
import learn.apptivitylab.com.petrolnav.model.User
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.IOException


/**
 * Created by apptivitylab on 12/01/2018.
 */
class PreferencesFragment : Fragment() {
    companion object {
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

    private lateinit var userListener: UserListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            this.userListener = context as UserListener
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateUser(user: User) {
        this.userListener.onUpdateUser(user)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_preferences, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.petrolStationBrandList = PetrolStationBrandLoader.petrolStationBrandList
        this.petrolList = PetrolLoader.petrolList

        arguments?.let {
            this.user = it.getParcelable(ARG_USER_DETAIL)
        }

        for (petrolStationBrand in this.petrolStationBrandList) {
            when (petrolStationBrand.petrolStationBrandName) {
                "Shell" -> this.checkBoxByPetrolStationBrand.put(petrolStationBrand, this.shellCheckbox)
                "Petronas" -> this.checkBoxByPetrolStationBrand.put(petrolStationBrand, this.petronasCheckbox)
                "BHPetrol" -> this.checkBoxByPetrolStationBrand.put(petrolStationBrand, this.bhpetrolCheckbox)
                "Caltex" -> this.checkBoxByPetrolStationBrand.put(petrolStationBrand, this.caltexCheckbox)
                "Petron" -> this.checkBoxByPetrolStationBrand.put(petrolStationBrand, this.petronCheckbox)
            }
        }

        for (petrol in this.petrolList) {
            when (petrol.petrolName) {
                "RON95" -> this.radioButtonByPetrol.put(petrol, this.ron95RadioButton)
                "RON97" -> this.radioButtonByPetrol.put(petrol, this.ron97RadioButton)
                "Diesel" -> this.radioButtonByPetrol.put(petrol, this.dieselRadioButton)
                "EURO5" -> this.radioButtonByPetrol.put(petrol, this.euro5RadioButton)
            }
        }

        this.presetPreference(this.user, this.checkBoxByPetrolStationBrand, this.radioButtonByPetrol)

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
            this.savePreference(this.user, this.preferredPetrolStationBrandList, this.preferredPetrol)
        }

        this.selectAllCheckBox.setOnClickListener {
            if (this.selectAllCheckBox.isChecked) {
                for (checkBox in this.checkBoxByPetrolStationBrand.values) {
                    checkBox.isChecked = true
                    this.selectAllCheckBox.text = getString(R.string.checkbox_deselect_all)
                }
            } else {
                for (checkBox in this.checkBoxByPetrolStationBrand.values) {
                    checkBox.isChecked = false
                    this.selectAllCheckBox.text = getString(R.string.checkbox_select_all)
                }
            }
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
            this.ron95RadioButton.isChecked = true
        }
    }

    private fun savePreference(user: User, preferredPetrolStationBrandList: ArrayList<PetrolStationBrand>, preferredPetrol: Petrol) {
        user.userPreferredPetrol = preferredPetrol
        user.userPreferredPetrolStationBrandList = preferredPetrolStationBrandList
        this.writeJSONPreference(user)

        if (this.activity is LoginActivity) {
            val launchIntent = MainActivity.newLaunchIntent(this.context!!, user)
            this.startActivity(launchIntent)
            this.activity!!.finish()
        } else {
            this.updateUser(user)
            this.activity!!.supportFragmentManager
                    .popBackStack(null, POP_BACK_STACK_INCLUSIVE)
            (this.activity!!.mainViewgroupContainer.layoutParams as CoordinatorLayout.LayoutParams).behavior = null
            this.activity!!.locationSearchAutoComplete.visibility = View.VISIBLE
            this.activity!!.toolbar.title = ""
        }
    }

    private fun writeJSONPreference(user: User) {
        val fileName = String.format("%s.json", user.userName)
        var jsonObjectPreference = JSONObject()

        var jsonObjectPetrol = JSONObject()
        with(jsonObjectPetrol) {
            put("uuid", user.userPreferredPetrol?.petrolId)
            put("name", user.userPreferredPetrol?.petrolName)
        }
        jsonObjectPreference.put("petrol", jsonObjectPetrol)

        var jsonObjectPetrolStationBrands = JSONArray()
        user.userPreferredPetrolStationBrandList?.forEach {
            var jsonObjectBrand = JSONObject()
            with(jsonObjectBrand) {
                put("uuid", it.petrolStationBrandId)
                put("name", it.petrolStationBrandName)
            }
            jsonObjectPetrolStationBrands.put(jsonObjectBrand)
        }
        jsonObjectPreference.putOpt("petrol_station_brands", jsonObjectPetrolStationBrands)

        try {
            val outputStream = context?.openFileOutput(fileName, Context.MODE_PRIVATE)
            outputStream?.write(jsonObjectPreference.toString().toByteArray())
            outputStream?.close()
        } catch (e: Exception) {
            when (e) {
                is FileNotFoundException, is IOException -> e.printStackTrace()
                else -> throw e
            }
        }
    }
}