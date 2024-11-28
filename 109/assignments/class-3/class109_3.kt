class MainActivity : AppCompatActivity() {
    private lateinit var humidityTextView: TextView
    private lateinit var windTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var tempTextView: TextView
    private lateinit var cityEditText: EditText
    private lateinit var fetchWeatherButton: Button
    private lateinit var commentEditText: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var submitButton: Button

    private lateinit var weatherApi: WeatherApi
    private lateinit var feedbackApi: FeedbackApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val weatherRetrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApi = weatherRetrofit.create(WeatherApi::class.java)

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

        val feedbackRetrofit = Retrofit.Builder()
            .baseUrl("")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        feedbackApi = feedbackRetrofit.create(FeedbackApi::class.java)

        ratingBar = findViewById(R.id.ratingBar)
        commentEditText = findViewById(R.id.commentEditText)
        submitButton = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            val rating = ratingBar.rating.toInt()
            val comment = commentEditText.text.toString()
            val feedback = Feedback(rating, comment)

            val call = feedbackApi.submitFeedback(feedback)
            call.enqueue(object : Callback<FeedbackResponse> {
                override fun onResponse(call: Call<FeedbackResponse>, response: Response<FeedbackResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MainActivity, "Feedback submitted!", Toast.LENGTH_SHORT).show()
                        Log.d("Retrofit Feedback", "Feedback submitted: ${response.body()}")
                    } else {
                        Log.e("Retrofit Feedback", "Error submitting feedback: ${response.raw()}")
                        Toast.makeText(this@MainActivity, "Error submitting feedback", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<FeedbackResponse>, t: Throwable) {
                    Log.e("Retrofit Feedback", "Network error submitting feedback", t)
                    Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
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

interface FeedbackApi {
    @POST("feedback")
    fun submitFeedback(@Body feedback: Feedback): Call<FeedbackResponse>
}

data class Feedback(val rating: Int, val comment: String)
data class FeedbackResponse(val success: Boolean)