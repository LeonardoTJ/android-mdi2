import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var greetingMessage: TextView
    private lateinit var destinationName: TextView
    private lateinit var localeSelector: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize views
        greetingMessage = findViewById(R.id.greetingMessage)
        destinationName = findViewById(R.id.destinationName)
        localeSelector = findViewById(R.id.localeSelector)

        // Populate spinner with locales
        val locales = listOf("ar_SA", "iw")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locales)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        localeSelector.adapter = adapter

        // Update UI based on the selected locale
        localeSelector.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedLocale = locales[position]
                updateUI(selectedLocale)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do Nothing
            }
        }

        // Default UI
        updateUI("en_US")
    }

    private fun updateUI(locale: String) {
        // Create a new configuration with the selected locale
        val configuration = resources.configuration
        val localeParts = locale.split("_")
        val newLocale = if (localeParts.size > 1) {
            Locale(localeParts[0], localeParts[1])
        } else {
            Locale(localeParts[0])
        }

        Locale.setDefault(newLocale)
        configuration.setLocale(newLocale)

        val context = createConfigurationContext(configuration)
        val localizedResources = context.resources

        // Update the UI with localized strings and images
        greetingMessage.text = localizedResources.getString(R.string.greeting_message)
        destinationName.text = localizedResources.getString(R.string.destination_name)
    }
}