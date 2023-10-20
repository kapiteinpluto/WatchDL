package com.example.watchdl.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchdl.adapters.StopAdapter
import com.example.watchdl.databinding.ActivityMainBinding
import com.example.watchdl.helperClasses.DataFetcher
import com.example.watchdl.helperClasses.FileEditor
import com.example.watchdl.helperClasses.FirebaseHelper
import com.example.watchdl.objects.BusStop
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.io.FileNotFoundException
import java.net.UnknownHostException

class MainActivity : AppCompatActivity() {
    private var dataFetcher: DataFetcher = DataFetcher()
    private lateinit var fileEditor: FileEditor
    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private var context: Context = this
    private var stops: MutableList<BusStop> = mutableListOf()
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // disable dark mode
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database("https://watchdl-default-rtdb.europe-west1.firebasedatabase.app")
        auth = Firebase.auth
        fileEditor = FileEditor(this)
        firebaseHelper = FirebaseHelper(this)
//        fileEditor.getFile().delete()

//        auth.signOut()
        checkLoggedIn()

        stops = fileEditor.fileToStops() as MutableList<BusStop>

        binding.recyclerview.adapter = StopAdapter(stops)
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        setEventListeners()
    }

    fun saveDataOnline(){
        val myRef = database.getReference(auth.uid.toString())
        val fileText = fileEditor.getFile().readText()
        myRef.setValue(fileText)
    }

    fun checkLoggedIn(){
        val currentUser = auth.currentUser
        Log.d("check logged in", currentUser.toString())
        if(currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        firebaseHelper.saveDataOnline()
    }

//    override fun onStop() {
//        super.onStop()
//        saveDataOnline()
//    }

    private fun setEventListeners(){
        binding.stopInput.setOnEditorActionListener { _, actionId, _ ->

            if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO){
                onClickAddStop()
                true
            }
            else {
                false
            }
        }
        binding.buttonAddStop.setOnClickListener {
            onClickAddStop()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun onClickAddStop(){
        binding.recyclerview.requestFocus()
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        val newId = binding.stopInput.text.toString().toInt()
        binding.stopInput.setText("")
        var isNew = true
        for(stop in stops){
            if(stop.id == newId) isNew = false
        }
        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }

        if(isNew){
            GlobalScope.launch(coroutineExceptionHandler) {
                try{
                    val stop = dataFetcher.fetchStop(newId)
                    println(stop)
                    stops.add(stop)
                    fileEditor.addStop(stop)
                    withContext(Dispatchers.Main){
                        binding.recyclerview.adapter?.notifyItemInserted(stops.size-1)
                    }
                } catch (e: FileNotFoundException){
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Stop doesn't exist", Toast.LENGTH_LONG).show()
                    }
                } catch (e: UnknownHostException){
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "No internet", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        else{
            Toast.makeText(this, "Stop already added", Toast.LENGTH_SHORT).show()
        }
    }
}