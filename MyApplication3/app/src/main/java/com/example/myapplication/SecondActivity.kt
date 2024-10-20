package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.compose.ui.semantics.text
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SecondActivity : AppCompatActivity() {

    private lateinit var textField: EditText
    private lateinit var readStorageButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        textField = findViewById(R.id.textField)
        readStorageButton = findViewById(R.id.readStorageButton)
        backButton = findViewById(R.id.backButton)

        textField.hint = getString(R.string.text_from_datastore)
        readStorageButton.text = getString(R.string.read_storage)
        backButton.text = getString(R.string.back)

        readStorageButton.setOnClickListener {
            lifecycleScope.launch {
                val savedText = readTextFromDataStore()
                if (savedText.isNotEmpty()) {
                    textField.setText(savedText)
                } else {
                    Toast.makeText(this@SecondActivity, getString(R.string.nothing_found), Toast.LENGTH_SHORT).show()
                }
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
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