package com.example.myapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val MY_APP: String = "MyApp"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(MY_APP, "starting...")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myText = "Hello, Danila!"
        val myNumber = 42
        val myFloatNumber = 3.14
        val outputText = "$myText $myNumber $myFloatNumber"

        val textView = findViewById<TextView>(R.id.text_output)
        textView.text = outputText

        Log.d(MY_APP, "End of Create function")
    }
}