class MainActivity : AppCompatActivity(), SensorEventListener {
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null

    private var yPosition = 0f
    private var xPosition = 0f
    private var ballSpeed = 5f

    private lateinit var ball: View
    private lateinit var gameView: FrameLayout
    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize SensorManager and sensors
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Initialize ball and UI elements
        ball = findViewById(R.id.ball)
        gameView = findViewById(R.id.main)

        // Register sensors
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }

        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }

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
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_GYROSCOPE -> {
                    // Update ball movement based on gyroscope data
                    xPosition -= it.values[1] * ballSpeed
                    yPosition += it.values[0] * ballSpeed
                    updateBallPosition()
                }
                Sensor.TYPE_ACCELEROMETER -> {
                    // Detect shake gesture
                    val acceleration = abs(it.values[0]) + abs(it.values[1]) + abs(it.values[2])
                    if (acceleration > 30) {
                        changeColor()
                    }
                }
            }
        }
    }

    private fun updateBallPosition() {
        // Ensure ball stays within the game view bounds
        val maxWidth = gameView.width - ball.width
        val maxHeight = gameView.height - ball.height

        xPosition = xPosition.coerceIn(0f, maxWidth.toFloat())
        yPosition = yPosition.coerceIn(0f, maxHeight.toFloat())

        ball.x = xPosition
        ball.y = yPosition
    }

    private fun changeColor() {
        ball.background.colorFilter = BlendModeColorFilter(Color.parseColor("#DC143C"), BlendMode.SRC_ATOP)
        // Reset boost after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            ball.background.colorFilter = BlendModeColorFilter(Color.parseColor("#4169E1"), BlendMode.SRC_ATOP)
        }, 3000)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}