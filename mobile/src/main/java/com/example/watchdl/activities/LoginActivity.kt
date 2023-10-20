package com.example.watchdl.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.watchdl.databinding.LoginPageBinding
import com.example.watchdl.helperClasses.FileEditor
import com.example.watchdl.helperClasses.FirebaseHelper
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var binding: LoginPageBinding
    private lateinit var firebaseHelper: FirebaseHelper

    private var isLoginPage: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseHelper = FirebaseHelper(this)

        setEventListeners()
    }


    private fun setEventListeners(){
        binding.passwordInput.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO){
                val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(View(this).windowToken, 0)

                true
            }
            else {
                false
            }
        }
        binding.buttonEnter.setOnClickListener {
            if(isLoginPage) login() else register()
        }
        binding.buttonChangePage.setOnClickListener {
            isLoginPage = !isLoginPage
            binding.buttonEnter.text = if(isLoginPage) "Login" else "Register"
            binding.buttonChangePage.text = if(isLoginPage) "Go to register" else "Go to login"
            binding.pageTitle.text = if(isLoginPage) "Login" else "Register"
        }
    }

    private fun login(){
        Firebase.auth.signInWithEmailAndPassword(binding.emailInput.text.toString(), binding.passwordInput.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Login", "signInWithEmail:success")
                    firebaseHelper.loadDataOnline()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Login", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loadDataOnline(){
        database.getReference(Firebase.auth.uid.toString()).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val value = dataSnapshot.getValue<String>()
                    Log.d("loadData", "Value is: $value")
                    if (value != null) {
                        FileEditor(applicationContext).getFile().writeText(value)
                    }
                    else{
                        FileEditor(applicationContext).getFile().delete()
                    }
                    startActivity(Intent(applicationContext, MainActivity::class.java), null)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("loadData", "Failed to read value.", error.toException())
                }
            }
        )

    }

    private fun register(){
        Firebase.auth.createUserWithEmailAndPassword(binding.emailInput.text.toString(), binding.passwordInput.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Register", "createUserWithEmail:success")
                    Toast.makeText(this,"Register succes", Toast.LENGTH_LONG).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Register", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}