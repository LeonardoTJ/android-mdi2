import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var magnetometer: Sensor? = null

    private lateinit var directionTextView: TextView
    private lateinit var rotationTextView: TextView
    private lateinit var motionTextView: TextView
    private lateinit var calibrateButton: Button
    private var isCalibrating = false
    private var accelerometerBaseline: FloatArray = floatArrayOf(0f, 0f, 0f)

    private var accelerometerValues = FloatArray(3)
    private var magnetometerValues = FloatArray(3)

    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        barChart = findViewById(R.id.barChart)
        barChart.description.text = "Step Count per Hour"
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

//         Example: Refresh button to update charts with new data
        findViewById<Button>(R.id.refreshButton).setOnClickListener {
            updateBarChart()
        }

        motionTextView = findViewById(R.id.motionTextView)
        rotationTextView = findViewById(R.id.rotationTextView)
        directionTextView = findViewById(R.id.directionTextView)

        // Initialize sensors
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        calibrateButton = findViewById(R.id.calibrateButton)
        calibrateButton.setOnClickListener {
            isCalibrating = true
            Toast.makeText(this, "Calibration started. Hold the device steady.", Toast.LENGTH_SHORT).show()
        }

        updateBarChart()
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
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

    private fun updateBarChart() {
        val entries = listOf(
            BarEntry(1f, 1000f),  // Hour 1: 1000 steps
            BarEntry(2f, 1200f),
            BarEntry(3f, 800f)
        )
        val dataSet = BarDataSet(entries, "Steps")
        val data = BarData(dataSet)
        barChart.data = data
        barChart.invalidate()  // Refresh the chart
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
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
        // Calibrate
        if (isCalibrating) {
            accelerometerBaseline[0] = accelerometerValues[0]
            accelerometerBaseline[1] = accelerometerValues[1]
            accelerometerBaseline[2] = accelerometerValues[2]
            isCalibrating = false
            Toast.makeText(this, "Calibration complete!", Toast.LENGTH_SHORT).show()
            return
        }
        // Correct values from calibration
        accelerometerValues[0] -= accelerometerBaseline[0]
        accelerometerValues[1] -= accelerometerBaseline[1]
        accelerometerValues[2] -= accelerometerBaseline[2]

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
        if (accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Toast.makeText(this, "Sensor accuracy is low. Please recalibrate.", Toast.LENGTH_SHORT).show()
        }
    }
}