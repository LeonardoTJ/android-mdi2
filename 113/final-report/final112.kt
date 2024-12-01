// Wearable module
class MainActivity : ComponentActivity() {

    private val CHANNEL_ID = "1"
    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private var sensorEventListener: SensorEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        requestPermissionAndStartSensor()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        setContent {
            WearableApp({ showNotification() })
        }

        // Create Notification Channel
        createNotificationChannel()
        // Check and request POST_NOTIFICATIONS permission if necessary
        requestNotificationPermission()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Fitness Notifications"
            val descriptionText = "Data saved notifications."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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

    @SuppressLint("MissingPermission")
    private fun showNotification() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.splash_icon)
            .setContentTitle("Fitness Data")
            .setContentText("Data successfully saved!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.splash_icon,
                "Open App",
                pendingIntent
            )
            .build()

        with(NotificationManagerCompat.from(this)) {
            Log.d("NotificationManager", "Sending Notification")
            notify(1, notification)
        }
    }

    private fun requestPermissionAndStartSensor() {
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

    private fun startHeartRateSensor(onHeartRateChanged: (Float) -> Unit) {
        if (heartRateSensor == null) {
            Log.e("Sensor", "Heart rate sensor not available on this device")
            return
        }

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

    @Composable
    fun WearableApp(onDataChange: () -> Unit) {
        val context = LocalContext.current
        var stepCount by remember { mutableStateOf("Waiting for data...") }
        var heartRate by remember { mutableStateOf("Waiting for data...") }
        val navController = rememberNavController()

        fun changeData(onDataChange: () -> Unit) {
            stepCount = "${(Math.random() * 1000).toInt()}"
            val dataClient = Wearable.getDataClient(context)
            val dataMapRequest = PutDataMapRequest.create("/step_count").apply {
                dataMap.putString("steps", stepCount)
                dataMap.putString("bpm", heartRate)
            }
            dataClient.putDataItem(dataMapRequest.asPutDataRequest())
            onDataChange()
            Log.d("DataClient", "Data sent: $stepCount | $heartRate")
        }

        // Start listening to sensor updates
        LaunchedEffect(Unit) {
            startHeartRateSensor { newHeartRate ->
                heartRate = "$newHeartRate"
            }
        }

        NavHost(navController = navController, startDestination = "step") {
            composable("step") {
                SwipeToNavigate(
                    onDismiss = { navController.navigate("heartrate") }
                ) {
                    HeartRateScreen(heartRate)
                }
            }
            composable("heartrate") {
                SwipeToNavigate(
                    onDismiss = { navController.navigate("step") }
                ) {
                    StepScreen({ changeData(onDataChange) }, stepCount)
                }
            }
        }
    }

    private @Composable
    fun StepScreen(changeData: () -> Unit, stepCount: String) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Text("Step Count: $stepCount")
                Spacer(modifier = Modifier.padding(15.dp))
                Button(onClick = changeData) { Text("Update") }
            }
        }
    }

    @Composable
    private fun HeartRateScreen(heartRate: String) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Heart Rate Monitor", style = MaterialTheme.typography.title1)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Heart Rate: $heartRate", style = MaterialTheme.typography.body1)
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

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(sensorEventListener, heartRateSensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorEventListener?.let {
            sensorManager.unregisterListener(it)
        }
    }
}
