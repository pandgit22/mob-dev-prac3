package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var saveButton: Button
    private lateinit var goToSecondButton: Button
    private lateinit var themeSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editText)
        saveButton = findViewById(R.id.saveButton)
        goToSecondButton = findViewById(R.id.goToSecondButton)
        themeSpinner = findViewById(R.id.themeSpinner)

        // Setup theme spinner
        val themeOptions = arrayOf(
            getString(R.string.default_theme),
            getString(R.string.dark_theme),
            getString(R.string.light_theme)
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, themeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        themeSpinner.adapter = adapter

        // Load saved data from DataStore
        lifecycleScope.launch {
            val savedText = readTextFromDataStore()
            editText.setText(savedText)
        }

        saveButton.setOnClickListener {
            val textToSave = editText.text.toString()
            lifecycleScope.launch {  // Launch a coroutine
                saveTextToDataStore(textToSave)
            }
        }

        goToSecondButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SecondActivity::class.java)
            startActivity(intent)
        }

        themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (parent.getItemAtPosition(position).toString()) {
                    getString(R.string.dark_theme) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    getString(R.string.light_theme) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private suspend fun saveTextToDataStore(text: String) {
        dataStore.edit { preferences ->
            preferences[USER_TEXT_KEY] = text
        }
    }

    private suspend fun readTextFromDataStore(): String {
        val preferences = dataStore.data.first()
        return preferences[USER_TEXT_KEY] ?: ""
    }

    companion object {
        private val USER_TEXT_KEY = stringPreferencesKey("user_text")
    }
}