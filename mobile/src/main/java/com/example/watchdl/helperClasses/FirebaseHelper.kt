package com.example.watchdl.helperClasses

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.example.watchdl.activities.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class FirebaseHelper(val context: Context) {
    private var auth = Firebase.auth
    private var database = Firebase.database("https://watchdl-default-rtdb.europe-west1.firebasedatabase.app")
    private var fileEditor = FileEditor(context)

    fun loadDataOnline(){
        database.getReference(auth.uid.toString()).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val value = dataSnapshot.getValue<String>()
                    Log.d("loadData", "Value is: $value")
                    if (value != null) {
                        fileEditor.getFile().writeText(value)
                    }
                    else{
                        fileEditor.getFile().delete()
                    }
                    startActivity(context,Intent(context, MainActivity::class.java), null)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("loadData", "Failed to read value.", error.toException())
                }
            }
        )
    }
    fun saveDataOnline(){
        val myRef = database.getReference(auth.uid.toString())
        val fileText = fileEditor.getFile().readText()
        myRef.setValue(fileText)
    }
}