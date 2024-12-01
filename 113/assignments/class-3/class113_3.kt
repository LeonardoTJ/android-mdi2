// Phone module
class MainActivity : ComponentActivity() {
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PhoneApp()
        }
    }


    @Composable
    fun PhoneApp() {
        val context = LocalContext.current
        var stepCount by remember { mutableStateOf("No data yet") }
        var heartRate by remember { mutableStateOf("No data yet") }

        LaunchedEffect(Unit) {
            val dataClient = Wearable.getDataClient(context)
            dataClient.addListener { dataEvents ->
                for (event in dataEvents) {
                    if (event.type == DataEvent.TYPE_CHANGED) {
                        val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                        stepCount = dataMap.getString("steps", "No data received")
                        heartRate = dataMap.getString("bpm", "No data received")
                        Log.d("DataClient", "Data received | Step count : $stepCount, Heart rate: $heartRate")
                        val data = mapOf("steps" to stepCount, "bpm" to heartRate)
                        db.collection("users")
                            .document("user1")
                            .update(data)
                            .addOnSuccessListener {
                                Log.d("DataClient", "Successfully stored in Firestore")
                                Toast.makeText(context, "Successfully stored in Firestore", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e -> Log.e("DataClient", "Error writing document", e) }
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Step Count: $stepCount steps",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Heart rate: $heartRate BPM",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}



// Wearable module
class MainActivity : ComponentActivity() {

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
            WearableApp()
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
    fun WearableApp() {
        val context = LocalContext.current
        var stepCount by remember { mutableStateOf("Waiting for data...") }
        var heartRate by remember { mutableStateOf("Waiting for data...") }
        val navController = rememberNavController()

        fun changeData() {
            stepCount = "${(Math.random() * 1000).toInt()}"
            val dataClient = Wearable.getDataClient(context)
            val dataMapRequest = PutDataMapRequest.create("/step_count").apply {
                dataMap.putString("steps", stepCount)
                dataMap.putString("bpm", heartRate)
            }
            dataClient.putDataItem(dataMapRequest.asPutDataRequest())
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
                    StepScreen({ changeData() }, stepCount)
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