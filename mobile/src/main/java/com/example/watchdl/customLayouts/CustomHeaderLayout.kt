package com.example.watchdl.customLayouts

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat.animate
import com.example.watchdl.R
import com.example.watchdl.activities.MainActivity
import com.example.watchdl.activities.SettingsActivity
import com.example.watchdl.databinding.HeaderBinding
import com.example.watchdl.helperClasses.FileEditor
import com.example.watchdl.helperClasses.FileSender
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CustomHeaderLayout (
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {
    init {
        inflate(context, R.layout.header, this)

        val customAttributesStyle = context.obtainStyledAttributes(attrs,
            R.styleable.CustomHeaderLayout, 0, 0)

        val title = findViewById<TextView>(R.id.title)


        try {
            title.text = customAttributesStyle.getString(R.styleable.CustomHeaderLayout_titleText)
            setMenu()
            setNavigation()
        } finally {
            customAttributesStyle.recycle()
        }
    }

    lateinit var binding: HeaderBinding

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = HeaderBinding.bind(this)
    }

    fun setTitle(title: String){
        binding.title.text = title
    }

    private fun setMenu(){
        val menuButton = findViewById<Button>(R.id.buttonMenu)
        val menu = findViewById<ConstraintLayout>(R.id.menu)
        val content = findViewById<ConstraintLayout>(R.id.content)

        menu.animate().duration = 150.toLong()
        menuButton.setOnClickListener {
            menu.visibility = VISIBLE
            menu.translationX = -menu.width.toFloat()
            menu.animate().translationX(0F).withEndAction {
                content.visibility = VISIBLE
            }
        }
        content.setOnClickListener {
            content.visibility = INVISIBLE
            menu.animate().translationX(-menu.width.toFloat()).withEndAction {
                menu.visibility = INVISIBLE
            }
        }
        menu.setOnClickListener {} //blocks buttons behind this
    }

    private fun setNavigation(){
        val buttonHome = findViewById<Button>(R.id.buttonHome)
        val buttonSettings = findViewById<Button>(R.id.buttonSettings)
        val buttonLogout = findViewById<Button>(R.id.buttonLogout)
        val buttonSaveToWatch = findViewById<Button>(R.id.buttonSaveToWatch)

        buttonHome.setOnClickListener {
            ContextCompat.startActivity(context, Intent(context, MainActivity::class.java), null)
        }
        buttonSettings.setOnClickListener {
            ContextCompat.startActivity(context, Intent(context, SettingsActivity::class.java), null)
        }
        buttonLogout.setOnClickListener {
            Firebase.auth.signOut()
            ContextCompat.startActivity(context, Intent(context, MainActivity::class.java), null)
        }
        buttonSaveToWatch.setOnClickListener {
            FileSender(context).sendFilesToWatch()
        }
    }

}