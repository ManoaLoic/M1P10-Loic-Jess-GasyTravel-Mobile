package com.example.gasytravel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var themeViewModel: ThemeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkMode = sharedPref.getBoolean("Dark mode", false)
        val themeResId = if (isDarkMode) R.style.AppTheme_Dark else R.style.AppTheme_Light
        setTheme(themeResId)

        setContentView(R.layout.settings_activity)
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Création et initialisation du ViewModel
        themeViewModel = ViewModelProvider(this).get(ThemeViewModel::class.java)
        themeViewModel.setDarkMode(isDarkMode)
    }

    override fun onResume() {
        super.onResume()

        val newDarkMode = sharedPref.getBoolean("Dark mode", false)
        themeViewModel.setDarkMode(newDarkMode)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() ,
        SharedPreferences.OnSharedPreferenceChangeListener{
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onResume() {
            super.onResume()
            preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            if (key == "Recevoir notification") {
                val receiveNotification = sharedPreferences?.getBoolean(key, true)
                if (receiveNotification == true) {
                    val editor = sharedPreferences?.edit()
                    editor?.putBoolean(key, true)
                    editor?.apply()
                } else {
                    val editor = sharedPreferences?.edit()
                    editor?.putBoolean(key, false)
                    editor?.apply()
                }
            }
        }
    }
}

class ThemeViewModel : ViewModel() {
    private val _isDarkMode = MutableLiveData<Boolean>()
    val isDarkMode: LiveData<Boolean> = _isDarkMode

    fun setDarkMode(isDarkMode: Boolean) {
        _isDarkMode.value = isDarkMode
    }
}
