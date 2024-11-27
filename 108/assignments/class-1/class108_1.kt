class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val darkModeSwitch = findViewById<MaterialSwitch>(R.id.darkModeSwitch)
        val saveStatusButton = findViewById<Button>(R.id.saveStatusButton)
        val statusEditText = findViewById<EditText>(R.id.statusEditText)
        val statusTextView = findViewById<TextView>(R.id.statusTextView)

        // Save Dark Mode Preference
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            applyTheme(isChecked, editor)
        }

        // Save User Status
        saveStatusButton.setOnClickListener {
            val statusMessage = statusEditText.text.toString()
            editor.putString("user_status", statusMessage)
            editor.apply() // Save changes
            Toast.makeText(this, "Status saved!", Toast.LENGTH_SHORT).show()
            setStatusText(statusTextView, statusMessage)
        }

        // Retrieve Dark Mode Preference
        val isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false)
        darkModeSwitch.isChecked = isDarkModeEnabled
        applyTheme(isDarkModeEnabled, editor)


        // Retrieve User Status
        val savedStatus = sharedPreferences.getString("user_status", "No Status")
        setStatusText(statusTextView, savedStatus)
    }

    private fun setStatusText(statusTextView: TextView, status: String?) {
        statusTextView.text = "Status: $status"
    }

    private fun applyTheme(isChecked: Boolean, editor: SharedPreferences.Editor) {
        val appTitle = findViewById<TextView>(R.id.appTitle)
        val statusTextView = findViewById<TextView>(R.id.statusTextView)

        if (isChecked) {
            appTitle.setTextColor(Color.WHITE)
            statusTextView.setTextColor(Color.WHITE)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            appTitle.setTextColor(Color.BLACK)
            statusTextView.setTextColor(Color.BLACK)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        editor.putBoolean("dark_mode", isChecked)
        editor.apply() // Save changes
    }
}