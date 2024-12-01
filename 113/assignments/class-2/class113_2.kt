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

        LaunchedEffect(Unit) {
            val dataClient = Wearable.getDataClient(context)
            dataClient.addListener { dataEvents ->
                for (event in dataEvents) {
                    if (event.type == DataEvent.TYPE_CHANGED) {
                        val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                        stepCount = dataMap.getString("steps", "No data received")
                        Log.d("DataClient", "Step count received: $stepCount")
                        val steps = hashMapOf("steps" to stepCount)
                        db.collection("users")
                            .document("user1")
                            .set(steps)
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
            Text(text = "Step Count: $stepCount")
        }
    }
}


// Wearable module
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearableApp()
        }
    }
}

@Composable
fun WearableApp() {
    val context = LocalContext.current
    var stepCount by remember { mutableStateOf("1234") }

    fun changeData() {
        stepCount = "${(Math.random() * 1000).toInt()}"
        val dataClient = Wearable.getDataClient(context)
        val dataMapRequest = PutDataMapRequest.create("/step_count").apply {
            dataMap.putString("steps", stepCount)
        }
        dataClient.putDataItem(dataMapRequest.asPutDataRequest())
        Log.d("DataClient", "Step count sent: $stepCount")
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text("Step Count: $stepCount")
            Spacer(modifier = Modifier.padding(15.dp))
            Button(onClick = ::changeData) { Text("Update") }
        }
    }
}