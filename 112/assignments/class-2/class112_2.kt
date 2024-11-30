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
fun StepScreen() {
    Text(
        text = "Step Counter : 300",
        style = MaterialTheme.typography.body1,
        modifier = Modifier.fillMaxSize().padding(50.dp)
    )
}

@Composable
fun HeartRateScreen() {
    Text(
        text = "Heart Rate: 95",
        style = MaterialTheme.typography.body1,
        modifier = Modifier.fillMaxSize().padding(50.dp)
    )
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