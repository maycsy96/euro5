package learn.apptivitylab.com.petrolnav

/**
 * Created by apptivitylab on 11/01/2018.
 */
class TestClass(var someVariable: Int?) {

    var id: Int = 0

    constructor(someString: String) : this(someString.toInt()) {
    }

    constructor(someDouble: Double) : this(someDouble.toInt()){
    }

    init {
        someVariable?.let { whatever ->
            someVariable = whatever / 1000
        }
    }
}