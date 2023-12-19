package com.sumeyra.instagramkotlin.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.sumeyra.instagramkotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var email : String
    private lateinit var password : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize Firebase Auth
        auth = Firebase.auth

        //currentUser
        val currentUser= auth.currentUser
        if (currentUser !=null){
            val intent = Intent(this@MainActivity, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun signUp(view: View){
        email= binding.userNameText.text.toString()
        password = binding.passwordText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            //email şifre varsa -> kayıt yap
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                //success
                //intent yap feed e geç
                val intent= Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish() // kullanıcı bir daha buraya girmeye gerek duymacak kapatabiliriz
            }.addOnFailureListener {
                //failure
                Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }else{
            //error message
            Toast.makeText(this@MainActivity,"Enter E-mail and Password !",Toast.LENGTH_LONG).show()
        }

    }


    fun signIn(view: View) {

        email = binding.userNameText.text.toString()
        password = binding.passwordText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            //email şifre varsa -> kayıt yap
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                //success
                //intent yap feed e geç
                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish() // kullanıcı bir daha buraya girmeye gerek duymacak kapatabiliriz
            }.addOnFailureListener {
                //failure
                Toast.makeText(this@MainActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            //error message
            Toast.makeText(this@MainActivity, "Enter E-mail and Password !", Toast.LENGTH_LONG)
                .show()
        }

    }
}



