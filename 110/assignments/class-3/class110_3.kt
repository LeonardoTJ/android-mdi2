class MainActivity : AppCompatActivity(), SensorEventListener {
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null

    private var yPosition = 0f
    private var xPosition = 0f
    private var ballSpeed = 5f
    private val goalDistance = 3

    private lateinit var ball: View
    private lateinit var gameView: FrameLayout
    private lateinit var sensorManager: SensorManager
    private lateinit var locationTextView: TextView

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        locationTextView = findViewById(R.id.locationTextView)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Request location permission
        requestLocationPermission()

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

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    startLocationUpdates()
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000 // 5 seconds
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        Log.d("Location", "Location request started")

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    Log.d("Location", "Location result received")
                    updateLocation(location)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
        }
    }

    private fun updateLocation(location: Location) {
        val currentLatitude = location.latitude
        val currentLongitude = location.longitude

        val locationString = "Lat: $currentLatitude, Lon: $currentLongitude"
        locationTextView.text = locationString
        Log.d("Location", locationString)

        // Check if the user has moved at least 5 meters
        if (lastLocation != null) {
            val distance = FloatArray(1)
            Location.distanceBetween(
                lastLocation!!.latitude,
                lastLocation!!.longitude,
                currentLatitude,
                currentLongitude,
                distance
            )
            if (distance[0] > goalDistance) {
                Toast.makeText(this, "You've moved ${goalDistance}m!", Toast.LENGTH_SHORT).show()
            }
        }
        // Update last known location
        lastLocation = location
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
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}