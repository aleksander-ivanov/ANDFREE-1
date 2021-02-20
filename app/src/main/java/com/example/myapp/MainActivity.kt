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

        setSupportActionBar(findViewById(R.id.topAppBar))

        Log.d(MY_APP, "End of Create function")
    }
}