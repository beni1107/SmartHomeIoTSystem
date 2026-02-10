abstract class SmartDevice (
    val name:String,
    val isConnected:Boolean
) {
    abstract fun performAction()
}

interface Dimmable {
    fun adjustBrightness(level: Int)
}
interface Recordable {
    fun startRecording()
}
interface Adjustable {
    fun setTemperature(temp : Double)
}

class SmartLight(name:String,
                 isConnected: Boolean,
                 private var brightness:Int,)
    :SmartDevice(name, isConnected)
    , Dimmable{

    override fun performAction() {
        println("The light $name is shinning at $brightness")
    }
    override fun adjustBrightness(level: Int) {
        brightness = level.coerceIn(0,100)
    }


    fun getBrightness():Int{
        return brightness
    }
}

class SmartTermostat(name:String,
                     isConnected: Boolean,
                     var currentTemp: Double)
    : SmartDevice(name, isConnected),
    Adjustable{

    override fun setTemperature(temp: Double) {
        currentTemp = temp
    }
    override fun performAction() {
        println("The termost at $currentTemp")
    }
}

class SecurityCamera(name:String,
                     isConnected: Boolean,
                     var isMotionDetected:Boolean)
    : SmartDevice(name, isConnected), Recordable {
    override fun startRecording() {

    }
    override fun performAction() {
        println("Camera $name started. Motion detected : $isMotionDetected")
    }
}

fun main() {
    val smartDevices: List<SmartDevice> = listOf(
        SmartLight("Bathroom", isConnected = true, brightness = 45),
        SecurityCamera("Front Door", isConnected = true, isMotionDetected = true),
        SmartTermostat(name = "Attic", isConnected = true, currentTemp = 23.0),
        SecurityCamera("Garage", isConnected = true, isMotionDetected = false),
        SmartTermostat(name = "Basement", isConnected = true, currentTemp = 17.0),
        SmartLight("Bedroom", isConnected = true, brightness = 0),
        SecurityCamera("Backyard", isConnected = true, isMotionDetected = true),
        SmartTermostat(name = "Living Room", isConnected = true, currentTemp = 21.0),
        SmartLight("Kitchen", isConnected = true, brightness = 70),
    )

    /**
     * We want to know if there is an intruder.
     * Instead of checking every single device, we only care about the cameras.
     * Print names of the cameras where motion is detected
     */

    val intruder = smartDevices.filterIsInstance<SecurityCamera>()
        .any { device -> device.isMotionDetected }
    val intruderCamera = smartDevices.filterIsInstance<SecurityCamera>()
        .filter { device -> device.isMotionDetected }.map { device -> device.name }
    println("Intruder: $intruder")
    println("Devices name where Intruder was detected : $intruderCamera")
    println()
    println()

    /**
     * Imagine you manually change the "Backyard" camera to isConnected = false.
     * You want to print a list of names for everything that is down.
     */
    /*smartDevices.filterIsInstance<SecurityCamera>()
        .find { device -> device.name == "Backyard" }
        ?.let { device -> device.isConnected =false } ?: " No Backyard camera found"
 */
     val downDevices = smartDevices.filter { device -> !device.isConnected }.map { device -> device.name }
    println("Down devices: $downDevices")
}



