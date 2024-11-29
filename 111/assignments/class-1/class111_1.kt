class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var magnetometer: Sensor? = null

    private lateinit var directionTextView: TextView
    private lateinit var rotationTextView: TextView
    private lateinit var motionTextView: TextView

    private var accelerometerValues = FloatArray(3)
    private var magnetometerValues = FloatArray(3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        motionTextView = findViewById(R.id.motionTextView)
        rotationTextView = findViewById(R.id.rotationTextView)
        directionTextView = findViewById(R.id.directionTextView)

        // Initialize sensors
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
        gyroscope?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        magnetometer?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
//        Log.d("Sensor", "Sensor: ${event.sensor}| Values: ${event.values}")
        when (event.sensor.type) {
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                accelerometerValues = event.values.clone()
                handleAccelerometer()
            }
            Sensor.TYPE_GYROSCOPE -> handleGyroscope(event.values)
            Sensor.TYPE_MAGNETIC_FIELD -> magnetometerValues = event.values.clone()
        }

        if (accelerometerValues.isNotEmpty() && magnetometerValues.isNotEmpty()) {
            calculateDirection()
        }
    }

    private fun calculateDirection() {
        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)

        if (SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                accelerometerValues,
                magnetometerValues
            )
        ) {
            // Compute the orientation angles
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            // rotation about the Z-axis (North = 0°)
            // https://en.wikipedia.org/wiki/Azimuth#True_north-based_azimuths
            val rotationRadians = orientationAngles[0]
            val rotationDegrees = Math.toDegrees(rotationRadians.toDouble()).toFloat()

            // Determine the direction
            val direction = when {
                rotationDegrees >= -22.5 && rotationDegrees < 22.5 -> "North"
                rotationDegrees >= 22.5 && rotationDegrees < 67.5 -> "North-East"
                rotationDegrees >= 67.5 && rotationDegrees < 112.5 -> "East"
                rotationDegrees >= 112.5 && rotationDegrees < 157.5 -> "South-East"
                rotationDegrees >= 157.5 || rotationDegrees < -157.5 -> "South"
                rotationDegrees >= -157.5 && rotationDegrees < -112.5 -> "South-West"
                rotationDegrees >= -112.5 && rotationDegrees < -67.5 -> "West"
                rotationDegrees >= -67.5 && rotationDegrees < -22.5 -> "North-West"
                else -> "Unknown"
            }

            // Display the direction
            directionTextView.text = "Direction: $direction ($rotationDegrees°)"
        }
    }

    private fun handleAccelerometer() {
        // Calculate the magnitude of total acceleration
        val magnitude =
            sqrt(accelerometerValues[0] * accelerometerValues[0] +
                    accelerometerValues[1] * accelerometerValues[1] +
                    accelerometerValues[2] * accelerometerValues[2])

//        Log.d("Accelerometer", "Magnitude: ${"%.2f".format(magnitude)}")
        val motionType = when {
            magnitude < 1 -> "Stationary"
            magnitude < 3 -> "Walking"
            else -> "Jogging"
        }

        motionTextView.text = "Motion: $motionType"
    }


    private fun handleGyroscope(values: FloatArray) {
        val pitch = values[0]
        val roll = values[1]
        val yaw = values[2]

        rotationTextView.text =
            "Rotation (Pitch, Roll, Yaw): ${"%.2f".format(pitch)}, ${"%.2f".format(roll)}, ${
                "%.2f".format(yaw)
            }"
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}