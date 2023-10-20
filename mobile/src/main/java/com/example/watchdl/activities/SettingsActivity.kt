package com.example.watchdl.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.watchdl.R
import com.example.watchdl.databinding.SettingsPageBinding
import com.example.watchdl.helperClasses.FileEditor

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: SettingsPageBinding
    private lateinit var fileEditor: FileEditor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // disable dark mode
        binding = SettingsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileEditor = FileEditor(this)

        setSpinner()
    }

    private fun setSpinner(){
        val newAdapter = ArrayAdapter.createFromResource(this, R.array.refresh_rates, android.R.layout.simple_spinner_item).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerAutoRefresh.adapter = newAdapter
        binding.spinnerAutoRefresh.setSelection(newAdapter.getPosition(fileEditor.getSetting("AutoRefresh")))

        binding.spinnerAutoRefresh.onItemSelectedListener = object: OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                println(selectedItem)
                fileEditor.editSetting("AutoRefresh", selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                parent?.setSelection(0)
            }

        }

    }
}