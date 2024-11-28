class MainActivity : AppCompatActivity(), SensorEventListener {
    private var gyroscope: Sensor? = null
    private var yPosition = 0f
    private var xPosition = 0f

    private lateinit var ball: View
    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize SensorManager and gyroscope
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        // Initialize ball
        ball = findViewById(R.id.ball)

        // Set initial position
        xPosition = ball.x
        yPosition = ball.y
    }

    override fun onResume() {
        super.onResume()
        gyroscope?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            // Gyroscope data
            val deltaX = event.values[0] * -10 // Invert X-axis for natural movement
            val deltaY = event.values[1] * 10

            // Update ball position
            xPosition += deltaX
            yPosition += deltaY

            // Boundaries check
            val layout = ball.parent as FrameLayout
            xPosition = xPosition.coerceIn(0f, layout.width - ball.width.toFloat())
            yPosition = yPosition.coerceIn(0f, layout.height - ball.height.toFloat())

            ball.x = xPosition
            ball.y = yPosition
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}
