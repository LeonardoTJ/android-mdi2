class MainActivity : AppCompatActivity() {

    @Inject
    private lateinit var preferencesDao: UserPreferencesDao
    private lateinit var roomDb: AppDatabase
    private lateinit var fireDb: FirebaseFirestore
    private val userId = "userId1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        roomDb = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "social-db"
        ).build()

        preferencesDao = roomDb.userPreferencesDao()

        fireDb = FirebaseFirestore.getInstance()

        val darkModeSwitch = findViewById<MaterialSwitch>(R.id.darkModeSwitch)
        val saveStatusButton = findViewById<Button>(R.id.saveStatusButton)
        val statusEditText = findViewById<EditText>(R.id.statusEditText)
        val statusTextView = findViewById<TextView>(R.id.statusTextView)

        loadPreferences()

        // Save Dark Mode Preference
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val currentStatusText = statusTextView.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                // Update local Room database
                val currentPreferences = preferencesDao.getPreferences()
                if (currentPreferences != null) {
                    val updatedPreferences = currentPreferences.copy(darkMode = isChecked)
                    preferencesDao.updatePreferences(updatedPreferences)
                }

                // Update Firestore
                fireDb.collection("settings")
                    .document(userId)
                    .update(mapOf("darkMode" to isChecked))
                    .addOnSuccessListener { Log.d("Firestore", "Dark mode updated successfully.") }
                    .addOnFailureListener { e -> Log.e("Firestore", "Error updating dark mode.", e) }
            }
            updateUI(isChecked, currentStatusText)
        }

        // Save User Status
        saveStatusButton.setOnClickListener {
            val statusMessage = statusEditText.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                // Update local Room database
                val currentPreferences = preferencesDao.getPreferences()
                if (currentPreferences != null) {
                    val updatedPreferences = currentPreferences.copy(status = statusMessage)
                    preferencesDao.updatePreferences(updatedPreferences)
                }

                // Update Firestore
                fireDb.collection("settings")
                    .document(userId)
                    .update(mapOf("status" to statusMessage))
                    .addOnSuccessListener { Log.d("Firestore", "Status updated successfully.") }
                    .addOnFailureListener { e -> Log.e("Firestore", "Error updating status.", e) }
            }
            updateUI(darkModeSwitch.isChecked, statusMessage)
        }
    }

    private fun loadPreferences() {
        lifecycleScope.launch(Dispatchers.IO) {
            // Fetch from Room
            val localPreferences = preferencesDao.getPreferences()

            // Fetch from Firestore
            fireDb.collection("settings")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    val remotePreferences = document.toObject<UserPreferences>()
                    val finalPreferences = remotePreferences ?: localPreferences
                    Log.d("Firebase", "Preferences: $finalPreferences")
                    lifecycleScope.launch(Dispatchers.Main) {
                        // Update UI
                        if (finalPreferences != null) {
                            updateUI(finalPreferences.darkMode, finalPreferences.status)

                            // Sync local Room database
                            if (localPreferences == null || localPreferences.darkMode != finalPreferences.darkMode) {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    preferencesDao.insertOrUpdate(UserPreferences(darkMode = finalPreferences.darkMode, status = finalPreferences.status))
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error fetching Firestore preference", e)
                }
        }
    }

    private fun updateUI(darkMode: Boolean, status: String) {
        // Apply preferences to UI
        // Example: Apply dark mode theme and show status
        val statusTextView = findViewById<TextView>(R.id.statusTextView)
        val appTitle = findViewById<TextView>(R.id.appTitle)

        statusTextView.text = status

        if (darkMode) {
            appTitle.setTextColor(Color.WHITE)
            statusTextView.setTextColor(Color.WHITE)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            appTitle.setTextColor(Color.BLACK)
            statusTextView.setTextColor(Color.BLACK)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey val id: Int = 1,
    val darkMode: Boolean = false,
    val status: String = "No Status"
)

@Dao
interface UserPreferencesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(preferences: UserPreferences)

    @Update
    suspend fun updatePreferences(preferences: UserPreferences)

    @Query("SELECT * FROM user_preferences WHERE id = 1")
    suspend fun getPreferences(): UserPreferences?
}

@Database(entities = [UserPreferences::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userPreferencesDao(): UserPreferencesDao
}
