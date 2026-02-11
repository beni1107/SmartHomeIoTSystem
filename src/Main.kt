abstract class SmartDevice (
    val name:String,
    val room: String,
    var isConnected:Boolean
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

class SmartLight(name:String,
                 room:String,
                 isConnected: Boolean,
                 private var brightness:Int,)
    :SmartDevice( name, room ,isConnected)
    , Dimmable{

    override val logs = listOf<String>("Dimming","Full Light")

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
                     room:String,
                     isConnected: Boolean,
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

class SecurityCamera(name:String,
                     room:String,
                     isConnected: Boolean,
                     var isMotionDetected:Boolean)
    : SmartDevice(name, room,isConnected), Recordable {
    override val logs = listOf<String>("Motion Sensor Initialized", "Lens Clean")
    override fun startRecording() {

    }
    override fun performAction() {
        println("Camera $name started. Motion detected : $isMotionDetected")
    }
}

fun main() {
    val smartDevices: List<SmartDevice> = listOf(
        SmartLight("Bathroom Light",room ="Bathroom", isConnected = true, brightness = 45),
        SmartLight("Attic Light", room = "Attic",isConnected = false, brightness = 45),
        SecurityCamera("Front Camera", room="Front",isConnected = true, isMotionDetected = true),
        SecurityCamera("Attic Camera", room="Attic",isConnected = true, isMotionDetected = false),
        SmartTermostat(name = "Attic Termostat",room="Attic", isConnected = true, currentTemp = 23.0),
        SmartTermostat(name = "Front Door Termostat",room="Front Door", isConnected = true, currentTemp = 15.0),
        SecurityCamera("Garage Camera",room="Garage", isConnected = true, isMotionDetected = false),
        SmartTermostat(name = "Basement Termostat",room="Basement", isConnected = true, currentTemp = 17.0),
        SmartTermostat(name = "Bedroom Termostat",room="Bedroom", isConnected = true, currentTemp = 17.0),
        SmartLight("Bedroom Light", room="Bedroom",isConnected = true, brightness = 0),
        SecurityCamera("Backyard Camera",room="Backyard", isConnected = true, isMotionDetected = true),
        SmartTermostat(name = "Living Room Termostat",room="Living Room", isConnected = true, currentTemp = 21.0),
        SmartTermostat(name = "Kitchen Termostat",room="Kitchen", isConnected = true, currentTemp = 21.0),
        SmartLight("Kitchen Light",room="Kitchen", isConnected = false, brightness = 70),
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
    val houseHistory = smartDevices.filter { device -> device.isConnected}
        .flatMap { device -> device.logs}.distinct()
    println("House history: $houseHistory")
    println()

    val intruderDected = smartDevices.filterIsInstance<SecurityCamera>().any{device -> device.isMotionDetected}
    val intruderNames = smartDevices.filterIsInstance<SecurityCamera>()
        .filter { device -> device.isMotionDetected }
        .map{device -> device.name}

    smartDevices.filterIsInstance<SecurityCamera>()
        .find { device -> device.name == "Backyard" }
        ?.let { device -> device.isConnected=false } ?: "Device not found"

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

    val avgTempAttic = smartDevices.filterIsInstance<SmartTermostat>()
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

    val xx = smartDevices.filterIsInstance<SmartTermostat>()
        .groupBy { room -> room.room }
        .map { device -> device.value.maxOf { it.currentTemp } }
}



