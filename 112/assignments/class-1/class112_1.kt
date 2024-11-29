class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContent {
            StepCounterApp()
        }

    }
}

@Composable
fun StepCounterApp() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            StepCounterScreen(stepCount = 1234) // Predefined step count
        }
    }
}

@Composable
fun StepCounterScreen(stepCount: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Step Count",
            style = MaterialTheme.typography.body1,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "$stepCount",
            style = MaterialTheme.typography.display1,
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}