class MainActivity : ComponentActivity() {

    private val CHANNEL_ID = "step_counter_channel"
    private val NOTIFICATION_ID = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearableApp { triggerNotification() }
        }

        // Create Notification Channel
        createNotificationChannel()

        // Check and request POST_NOTIFICATIONS permission if necessary
        requestNotificationPermission()
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
}

@Composable
fun StepScreen(onButtonClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Step Counter : 300",
                style = MaterialTheme.typography.body1,
//                modifier = Modifier.fillMaxSize().padding(5.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onButtonClick()}) {
                Text("Send")
            }
        }
    }
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
fun WearableApp(onButtonClick: () -> Unit) {
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
                StepScreen(onButtonClick)
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