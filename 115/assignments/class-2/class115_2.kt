import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var welcomeMessage: TextView
    private lateinit var dateTimeDisplay: TextView
    private lateinit var numberAndCurrencyDisplay: TextView
    private lateinit var localeSelector: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        // Initialize Views
        welcomeMessage = findViewById(R.id.welcomeMessage)
        dateTimeDisplay = findViewById(R.id.dateTimeDisplay)
        numberAndCurrencyDisplay = findViewById(R.id.numberAndCurrencyDisplay)
        localeSelector = findViewById(R.id.localeSelector)
        val updateButton = findViewById<Button>(R.id.updateButton)


        // Populate Spinner with Locales
        val locales = arrayOf("en_US", "fr_FR", "de_DE", "ja_JP")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locales)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        localeSelector.setAdapter(adapter)

        // Set Default Display
        updateDisplay(Locale.getDefault())

        // Handle Button Click to Update Locale
        updateButton.setOnClickListener {
            val selectedLocale = localeSelector.selectedItem as String
            val localeParts = selectedLocale.split("_")
            val locale = Locale(localeParts[0], localeParts[1])
            updateDisplay(locale)
        }
    }

    private fun updateDisplay(locale: Locale) {
        // Update Welcome Message
        welcomeMessage.text = getString(R.string.welcome_message, locale.displayLanguage)

        // Update Date and Time
        val dateFormat: DateFormat =
            DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale)
        val currentDateTime: String = dateFormat.format(Date())
        dateTimeDisplay.text = getString(R.string.date_time_display, currentDateTime)

        // Update Number and Currency
        val numberFormat = NumberFormat.getInstance(locale)
        val currencyFormat = NumberFormat.getCurrencyInstance(locale)
        val number: String = numberFormat.format(12345.67)
        val currency: String = currencyFormat.format(12345.67)
        numberAndCurrencyDisplay.text =
            getString(R.string.number_currency_display, number, currency)
    }
}