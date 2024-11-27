package com.example.sharedpref108

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.android.material.materialswitch.MaterialSwitch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var preferencesDao: UserPreferencesDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "social-db"
        ).build()

        preferencesDao = db.userPreferencesDao()

        loadPreferences()

        val darkModeSwitch = findViewById<MaterialSwitch>(R.id.darkModeSwitch)
        val saveStatusButton = findViewById<Button>(R.id.saveStatusButton)
        val statusEditText = findViewById<EditText>(R.id.statusEditText)
        val statusTextView = findViewById<TextView>(R.id.statusTextView)

        // Save Dark Mode Preference
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateUI(isChecked, statusTextView.text.toString())
            savePreferences(isChecked, statusTextView.text.toString())
        }

        // Save User Status
        saveStatusButton.setOnClickListener {
            val statusMessage = statusEditText.text.toString()
            updateUI(darkModeSwitch.isChecked, statusMessage)
            savePreferences(darkModeSwitch.isChecked, statusMessage)
            Toast.makeText(this, "Status saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadPreferences() {
        lifecycleScope.launch {
            val preferences = preferencesDao.getPreferences()

            if (preferences != null) {
                // Update the UI based on saved preferences
                updateUI(preferences.darkMode, preferences.status)
            } else {
                // Default preferences
                savePreferences(darkMode = false, status = "No status")
            }
        }
    }

    private fun updateUI(darkMode: Boolean, status: String) {
        // Apply preferences to UI
        // Example: Apply dark mode theme and show status
        val statusTextView = findViewById<TextView>(R.id.statusTextView)
        val appTitle = findViewById<TextView>(R.id.appTitle)
        val darkModeSwitch = findViewById<MaterialSwitch>(R.id.darkModeSwitch)

        statusTextView.text = status

        if (darkMode) {
            darkModeSwitch.isChecked = true
            appTitle.setTextColor(Color.WHITE)
            statusTextView.setTextColor(Color.WHITE)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            darkModeSwitch.isChecked = false
            appTitle.setTextColor(Color.BLACK)
            statusTextView.setTextColor(Color.BLACK)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun savePreferences(darkMode: Boolean, status: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            preferencesDao.insertOrUpdate(UserPreferences(darkMode = darkMode, status = status))
        }
    }
}

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey val id: Int = 1,
    val darkMode: Boolean,
    val status: String
)

@Dao
interface UserPreferencesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(preferences: UserPreferences)

    @Query("SELECT * FROM user_preferences WHERE id = 1")
    suspend fun getPreferences(): UserPreferences?
}

@Database(entities = [UserPreferences::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userPreferencesDao(): UserPreferencesDao
}

