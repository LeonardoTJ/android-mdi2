class MainActivity : AppCompatActivity() {
    private lateinit var weatherTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val cityEditText = findViewById<EditText>(R.id.cityEditText)
        val fetchWeatherButton = findViewById<Button>(R.id.fetchWeatherButton)
        weatherTextView = findViewById(R.id.weatherTextView)

        fetchWeatherButton.setOnClickListener {
            val city = cityEditText.text.toString()
            if (city.isNotEmpty()) {
                fetchWeather(city)
            } else {
                weatherTextView.text = "Please enter a city name."
            }
        }
    }

    private fun fetchWeather(city: String) {
        val apiKey = ""
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&APPID=$apiKey"

        // Use a coroutine for network operation
        CoroutineScope(Dispatchers.IO).launch {
            val result = makeHttpRequest(url)
            val weatherInfo = parseWeatherData(result)

            // Update the UI on the main thread
            withContext(Dispatchers.Main) {
                weatherTextView.text = weatherInfo
            }
        }
    }

    private fun parseWeatherData(jsonResponse: String): String {
        val jsonObject = JSONObject(jsonResponse)
        val weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")
        val tempKelvin = jsonObject.getJSONObject("main").getDouble("temp")
        val tempCelsius = tempKelvin - 273.15

        return "Weather: $weather\nTemperature: ${"%.2f".format(tempCelsius)}°C"
    }

    private fun makeHttpRequest(urlString: String): String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        return connection.inputStream.bufferedReader().readText()
    }
}