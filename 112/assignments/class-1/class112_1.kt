class MainActivity : ComponentActivity() {
    private var steps: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        steps = 30
        val stepCountTextView: TextView = findViewById(R.id.step_count_text)
        stepCountTextView.text = "Steps: $steps."
    }

}