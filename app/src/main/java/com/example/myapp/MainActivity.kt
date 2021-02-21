package com.example.myapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.wolfram.alpha.WAEngine
import com.wolfram.alpha.WAPlainText

class MainActivity : AppCompatActivity() {

    private val MY_APP: String = "MyApp"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(MY_APP, "starting...")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.topAppBar))

        val questionInput = findViewById<TextView>(R.id.question_input)
        val searchButton = findViewById<Button>(R.id.search_button)

        searchButton.setOnClickListener {
            try {
                askWolfram(questionInput.text.toString())
            }
            catch (e: Exception){
                Log.e("wolfram error", e.message.toString())

            }
        }

        Log.d(MY_APP, "End of Create function")
    }

    fun askWolfram(question: String) {
        val wolframAppId = "56LEX7-VU7RG6LGP7"

        val engine = WAEngine()
        engine.appID = wolframAppId
        engine.addFormat("plaintext")

        val query = engine.createQuery()
        query.input = question

        val answerText = findViewById<TextView>(R.id.answer_output)
        answerText.text = "Let me think..."

        Thread(Runnable {
            val queryResult = engine.performQuery(query)
            var answer = ""

            if (queryResult.isError) {
                Log.e("wolfram error", queryResult.errorMessage)
                answer = queryResult.errorMessage
            } else if (!queryResult.isSuccess) {
                Log.e("wolfram error", "Sorry, I don't understand, can you rephrase?")
                answer = "Sorry, I don't understand, can you rephrase?"
            } else {
                for (pod in queryResult.pods) {
                    if (!pod.isError) {
                        for (subpod in pod.subpods) {
                            for (element in subpod.contents) {
                                if (element is WAPlainText) {
                                    Log.d("wolfram", element.text)
                                    answer += element.text
                                }
                            }
                        }
                    }
                }
            }

            answerText.post {
                answerText.text = answer
            }
        }).start()
    }
}