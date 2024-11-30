class PhoneMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PhoneApp()
        }
    }
}

@Composable
fun wearableDataClient(): DataClient {
    val context = LocalContext.current
    return remember { Wearable.getDataClient(context) }
}

@Composable
fun PhoneApp() {
    var stepCount by remember { mutableStateOf("No data yet") }
    val dataClient = wearableDataClient()

    LaunchedEffect(Unit) {
        dataClient.addListener(object : DataClient.OnDataChangedListener {
            override fun onDataChanged(dataEvents: DataEventBuffer) {
                for (event in dataEvents) {
                    if (event.type == DataEvent.TYPE_CHANGED) {
                        val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                        stepCount = dataMap.getString("steps", "No data received")
                    }
                }
            }
        })
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BasicText("Step Count: $stepCount")
    }
}

class WearableMainActivity : ComponentActivity() {
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
fun wearableDataClient(): DataClient {
    val context = LocalContext.current
    return remember { Wearable.getDataClient(context) }
}

@Composable
fun WearableApp() {
    var stepCount by remember { mutableStateOf("1234") }
    val dataClient = wearableDataClient()

    LaunchedEffect(Unit) {
        val dataMapRequest = PutDataMapRequest.create("/step_count").apply {
            dataMap.putString("steps", stepCount)
        }
        dataClient.putDataItem(dataMapRequest.asPutDataRequest())
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Step Count: $stepCount")
    }
}