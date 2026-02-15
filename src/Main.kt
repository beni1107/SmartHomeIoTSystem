abstract class SmartDevice (
   open val name:String,
    open val room: String,
    open var isConnected:Boolean
) {
   open val logs: List<String> = listOf("System Boot")
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

data class SmartLight(
   override val name: String,      // No val! Just a parameter to pass up
    override val room: String,      // No val!
    override var isConnected: Boolean, // No var!
    val brightness: Int // Keep val! This belongs to SmartLight only
) : SmartDevice(name, room, isConnected), Dimmable {

    override val logs = listOf<String>("Dimming","Full Light")

    override fun performAction() {
        println("Light $name is at $brightness%")
    }
    
    override fun adjustBrightness(level: Int) {

    }


    
}

data class SmartThermostat(
    override val name:String,
    override val room:String,
    override var isConnected: Boolean,
                     var currentTemp: Double)
    : SmartDevice(name, room,isConnected),
    Adjustable{

    override val logs = listOf<String>("Starting..","Temperature check")
    override fun setTemperature(temp: Double) {
        currentTemp = temp
    }
    override fun performAction() {
        println("The termost at $currentTemp")
    }
}

data class SecurityCamera(
    override val name:String,
    override val room:String,
    override var isConnected: Boolean,
                     var isMotionDetected:Boolean)
    : SmartDevice(name, room,isConnected), Recordable {
    override val logs = listOf<String>("Motion Sensor Initialized", "Lens Clean")
    override fun startRecording() {

    }
    override fun performAction() {
        println("Camera $name started. Motion detected : $isMotionDetected")
    }
}

sealed class DeviceResult {
    data class Success(val device : SmartDevice) : DeviceResult()
    data class Failure (val message: String) : DeviceResult()
}

sealed class TempResult {
    data class Success( val termostat: SmartThermostat)
    data class Failure (val message: String)
}

fun main() {
    val smartDevices: List<SmartDevice> = listOf(
        SmartLight("Bathroom Light", room = "Bathroom", isConnected = true, brightness = 45),
        SmartLight("Attic Light", room = "Attic", isConnected = false, brightness = 45),
        SecurityCamera("Front Camera", room = "Front", isConnected = true, isMotionDetected = true),
        SecurityCamera("Attic Camera", room = "Attic", isConnected = true, isMotionDetected = false),
        SmartThermostat(name = "Attic Termostat", room = "Attic", isConnected = true, currentTemp = 23.0),
        SmartThermostat(name = "Front Door Termostat", room = "Front Door", isConnected = true, currentTemp = 15.0),
        SecurityCamera("Garage Camera", room = "Garage", isConnected = true, isMotionDetected = false),
        SmartThermostat(name = "Basement Termostat", room = "Basement", isConnected = true, currentTemp = 17.0),
        SmartThermostat(name = "Bedroom Termostat", room = "Bedroom", isConnected = true, currentTemp = 17.0),
        SmartLight("Bedroom Light", room = "Bedroom", isConnected = true, brightness = 0),
        SecurityCamera("Backyard Camera", room = "Backyard", isConnected = true, isMotionDetected = true),
        SmartThermostat(name = "Living Room Termostat", room = "Living Room", isConnected = true, currentTemp = 21.0),
        SmartThermostat(name = "Kitchen Termostat", room = "Kitchen", isConnected = true, currentTemp = 21.0),
        SmartLight("Kitchen Light", room = "Kitchen", isConnected = false, brightness = 70),
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
    smartDevices.filterIsInstance<SecurityCamera>()
        .find { device -> device.name == "Backyard" }
        ?.isConnected = false ?: false

    val downDevices = smartDevices.filter { device -> !device.isConnected }.map { device -> device.name }
    println("Down devices: $downDevices")
    println()
    println()
    /**
     * Now that you've added the logs to the abstract class, try to write out the chain that does this:
     * Filters for only the devices that are isConnected.
     * Flattens all their logs into one list.
     * Removes duplicates (so you don't see "System Boot" 9 times).
     * Saves it to a variable called houseHistory.
     */
    val allLogs = smartDevices.flatMap { device -> device.logs }
    println("All logs: $allLogs")
    println()
    val houseHistory = smartDevices.filter { device -> device.isConnected }
        .flatMap { device -> device.logs }.distinct()
    println("House history: $houseHistory")
    println()

    val intruderDected = smartDevices.filterIsInstance<SecurityCamera>().any { device -> device.isMotionDetected }
    val intruderNames = smartDevices.filterIsInstance<SecurityCamera>()
        .filter { device -> device.isMotionDetected }
        .map { device -> device.name }

    smartDevices.filterIsInstance<SecurityCamera>()
        .find { device -> device.name == "Backyard" }
        ?.let { device -> device.isConnected = false } ?: "Device not found"

    val check = smartDevices.filterIsInstance<SecurityCamera>()
        .find { device -> device.name == "Backyard" }?.isConnected ?: "Device not found"
    println(check)
    println()
    println()

    /**
     * Your house has devices in the Kitchen, Attic, Bedroom, etc.
     * I want you to create a "Map" where the Key is the name of the room and the Value is the number of devices in that room.
     */

    val roomDevice = smartDevices.groupBy { device -> device.name }
    roomDevice.forEach { (room, devices) ->
        println("Room: $room")
        devices.forEach { device ->
            println("  - ${device.name}")
        }
    }
    println()
    println()
    val roomDevice2 = smartDevices.groupingBy { device -> device.name }.eachCount()
    roomDevice2.forEach { (room, status) ->
        println("Room :$room number of devices: $status")

    }

    /**
     *Calculate the average temperature of all SmartTermostat devices located in the "Attic" that are currently isConnected
     */

    val avgTempAttic = smartDevices.filterIsInstance<SmartThermostat>()
        .filter { room -> room.room == "Attic" }
        .filter { connection -> connection.isConnected }
        .map { temperature -> temperature.currentTemp }.average()

    println("the average temp for connected smarttemostats is : $avgTempAttic")

    println()
    println()

    /*
     * Goal: Get a single list of unique log messages from every device
     *  in the house, but I want the final list to be sorted alphabetically.
     */
    val sortedLogs = smartDevices.flatMap { device -> device.logs }.distinct().sorted()
    println("Sorted log messages from devices are : $sortedLogs")
    sortedLogs.forEach { log -> println(log) }

    val xx = smartDevices.filterIsInstance<SmartThermostat>()
        .groupBy { room -> room.room }
        .map { device -> device.value.maxOf { it.currentTemp } }


    /**
     * Group all your devices by room, but then transform the results to
     * show how many connected vs. disconnected devices are in each room
     */

    val report = smartDevices.groupBy { room -> room.room }
        .mapValues { entry ->
            val (on, off) = entry.value.partition { it.isConnected }
            "Online: ${on.size}, Offline: ${off.size}"
        }
    report.forEach { string, string1 ->
        println("key: $string  value : $string1")
    }

    println()
    println()
    println()

    /**
     * Changed to data classses and i will try new functions
     */

    /**
     * find Kitchen light and change is conneted to false
     */


    val repairedlight2 = (smartDevices.find { device ->
        device.room == "Kitchen" && !device.isConnected
    } as? SmartLight)?.let { it.copy(isConnected = true, brightness = 100) }

    val status = if (repairedlight2 != null) {
        DeviceResult.Success(repairedlight2)
    } else {
        DeviceResult.Failure("Device not found")
    }

    when (status) {
        is DeviceResult.Success -> println("Fixed: ${status.device}")
        is DeviceResult.Failure -> println("Error: ${status.message}")
    }
    println()
    println()

    val x = smartDevices.filterIsInstance<SmartThermostat>().find { device ->
        device.room == "Attic"
    }?.let { thermostat -> thermostatCheck(thermostat, 20.0) }

    val camTmp = smartDevices.filterIsInstance<SecurityCamera>()
        .find { camera -> camera.name == "Front Camera" }?.
            let { camera -> cameraCheck(camera) }

    when(camTmp) {
        is DeviceResult.Success -> {
            val tmp = camTmp.device as SecurityCamera
            println("Camera name ${tmp.name} Camera connection ${tmp.isConnected}")
        }
        is DeviceResult.Failure -> println(camTmp.message)
         null -> println("To je null")
    }

}

/**
 * Goal: Practice copy() and basic sealed class wrapping.
 * The Scenario: You need to update a SmartThermostat. However, if someone tries to set the temperature above 30°C or below 10°C, it’s considered a "Safety Violation."
 * The Task:
 * Create a sealed class TempResult.
 * Success: Holds the updated thermostat.
 * Error: Holds a message ("Temperature too high!" or "Too low!").
 * Create a function or logic block that takes an existing SmartThermostat and a newTemp: Double.
 * Logic: * If the temp is valid, return a .copy() of the thermostat in the Success box.
 * If invalid, return the Error box.
 * Practice: Try to update your "Attic Thermostat" to 35.0°C and handle the result with a when block.
 */
fun thermostatCheck(thermostat:SmartThermostat, newTemp: Double): DeviceResult {

    if (newTemp > 30 || newTemp < 10) {
        return DeviceResult.Failure("Wrong temperature")
    }else {
        return DeviceResult.Success(thermostat.copy(currentTemp = newTemp))
    }

}

fun cameraCheck( camera: SecurityCamera, ): DeviceResult {
    return if(camera.isMotionDetected ) {
         DeviceResult.Success(camera.copy(isConnected = true))
    }else  {
         DeviceResult.Failure("Camera not connected")
    }
}



