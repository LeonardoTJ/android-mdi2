class MainActivity : AppCompatActivity() {
    private lateinit var humidityTextView: TextView
    private lateinit var windTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var tempTextView: TextView
    private lateinit var cityEditText: EditText
    private lateinit var fetchWeatherButton: Button
    private lateinit var weatherApi: WeatherApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApi = retrofit.create(WeatherApi::class.java)

        cityEditText = findViewById(R.id.cityEditText)
        fetchWeatherButton = findViewById(R.id.fetchWeatherButton)

        fetchWeatherButton.setOnClickListener {
            val city = cityEditText.text.toString()
            if (city.isNotEmpty()) {
                fetchWeather(city)
            } else {
                Toast.makeText(this@MainActivity, "Please enter a city name.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fetchWeather(city: String) {
        val apiKey = ""
        val call = weatherApi.getWeather(city, apiKey)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weather = response.body()
                    displayWeather(weather)
                } else {
                    Log.e("Retrofit", "Error calling OpenWeatherMap API: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("Retrofit", "Network error", t)
            }
        })
    }

    private fun displayWeather(weather: WeatherResponse?) {
        tempTextView = findViewById(R.id.tempTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        windTextView = findViewById(R.id.windTextView)
        humidityTextView = findViewById(R.id.humidityTextView)

        weather?.let {
            tempTextView.text = "Temp: ${"%.2f".format(it.main.temp-273.15)}°C"
            descriptionTextView.text = "Weather: ${it.weather.first().description}"
            windTextView.text = "Wind Speed: ${it.wind.speed} m/s"
            humidityTextView.text = "Humidity: ${it.main.humidity}%"
        }
    }
}

interface WeatherApi {
    @GET("weather")
    fun getWeather(@Query("q") city: String, @Query("appid") apiKey: String): Call<WeatherResponse>
}

data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)

data class Main(val temp: Double, val humidity: Int)
data class Weather(val description: String)
data class Wind(val speed: Double)