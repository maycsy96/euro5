package learn.apptivitylab.com.petrolnav.model

/**
 * Created by apptivitylab on 12/01/2018.
 */
data class User (var id:String,
                 var name: String,
                 var email:String,
                 var password:String,
                 var preferPetrol: Petrol,
                 var preferPetrolStationBrand: List<PetrolStationBrand>?){

}