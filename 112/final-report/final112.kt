class MainActivity : ComponentActivity() {

    private var sensorEventListener: SensorEventListener? = null
    private var heartRateSensor: Sensor? = null
    private lateinit var sensorManager: SensorManager

    private val CHANNEL_ID = "step_counter_channel"
    private val NOTIFICATION_ID = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setupSensorPermissions()
        // Initialize sensors
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        setContent {
            WearableApp()
        }

        // Create Notification Channel
        createNotificationChannel()
    }

    private fun setupSensorPermissions() {
        // Check and request POST_NOTIFICATIONS permission if necessary
        requestNotificationPermission()
        // Check and request BODY_SENSORS permission if necessary
        requestBodySensorsPermission()
    }

    private fun requestBodySensorsPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    Log.d("Permission", "BODY_SENSORS permission granted")
                } else {
                    Log.e("Permission", "BODY_SENSORS permission denied")
                }
            }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("Permission", "BODY_SENSORS already granted")
        } else {
            requestPermissionLauncher.launch(Manifest.permission.BODY_SENSORS)
        }
    }

    private fun setupHeartRateSensor(onHeartRateChanged: (Float) -> Unit) {
        if (heartRateSensor == null) {
            Log.e("Sensor", "Heart rate sensor not available on this device")
            return
        }
        Log.d("Sensors", "Heart rate sensor is available")

        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && event.sensor.type == Sensor.TYPE_HEART_RATE) {
                    val heartRate = event.values[0]
                    onHeartRateChanged(heartRate)
                    Log.d("HeartRate", "Heart Rate: $heartRate bpm")
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Handle accuracy changes if necessary
            }
        }

        sensorManager.registerListener(
            sensorEventListener,
            heartRateSensor,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    @SuppressLint("MissingPermission")
    private fun triggerNotification() {
        println("triggerNotification called") // Debug log
        // Build the notification
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Step Count Achieved!")
            .setContentText("You’ve triggered a step notification!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Show the notification
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Step Counter Notifications"
            val descriptionText = "Notifications for step counter milestones"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(sensorEventListener, heartRateSensor, SensorManager.SENSOR_DELAY_UI)
    }
    override fun onPause() {
        super.onPause()
        // Unregister sensors
        sensorEventListener?.let {
            sensorManager.unregisterListener(it)
        }
    }

    @Composable
    fun StepScreen() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Step Counter : 300",
                    style = MaterialTheme.typography.body1,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { triggerNotification()}) {
                    Text("Send")
                }
            }
        }
    }

    @Composable
    fun HeartRateScreen() {
        var heartRate by remember { mutableStateOf(0f) }

        Text(
            text = "Heart Rate: ${heartRate.toInt()} BPM",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.fillMaxSize().padding(50.dp)
        )

        // Start listening to sensor updates
        LaunchedEffect(Unit) {
            setupHeartRateSensor { newHeartRate ->
                heartRate = newHeartRate
            }
        }
    }

    @Composable
    fun WearableApp() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "step") {
            composable("step") {
                SwipeToNavigate(
                    onDismiss = { navController.navigate("heartrate") }
                ) {
                    HeartRateScreen()
                }
            }
            composable("heartrate") {
                SwipeToNavigate(
                    onDismiss = { navController.navigate("step") }
                ) {
                    StepScreen()
                }
            }
        }
    }

    @Composable
    fun SwipeToNavigate(onDismiss: () -> Unit, content: @Composable () -> Unit) {
        val swipeState = rememberSwipeToDismissBoxState()
        SwipeToDismissBox(
            state = swipeState,
            onDismissed = { onDismiss() }
        ) {
            content()
        }
    }
}