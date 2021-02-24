package com.example.myapp

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wolfram.alpha.WAEngine
import com.wolfram.alpha.WAPlainText
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val MY_APP: String = "MyApp"
    private val speechToTextRequestCode = 123
    lateinit var textToSpeech: TextToSpeech
    var speechRequest = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(MY_APP, "starting...")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.topAppBar))

        val questionInput = findViewById<TextView>(R.id.question_input)
        val searchButton = findViewById<Button>(R.id.search_button)

        searchButton.setOnClickListener {
            textToSpeech.stop()
            try {
                askWolfram(questionInput.text.toString())
            }
            catch (e: Exception){
                Log.e("wolfram error", e.message.toString())
            }
        }

        val speakButton = findViewById<Button>(R.id.speak_button)
        speakButton.setOnClickListener{
            textToSpeech.stop()
            
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "What would you like to know?")

            try {
                startActivityForResult(intent, speechToTextRequestCode)
            }catch (e: ActivityNotFoundException){
                Log.e("wolfram error", e.message.toString())
                Toast.makeText(
                    applicationContext,
                    "Sorry, your device is not supported",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val answerOutput = findViewById<TextView>(R.id.answer_output)
        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener {  })
        textToSpeech.language = Locale.US

        findViewById<FloatingActionButton>(R.id.read_answer).setOnClickListener{
            val answer = answerOutput.text.toString()
            textToSpeech.speak(answer, TextToSpeech.QUEUE_ADD, null, speechRequest.toString())
            speechRequest +=1
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
            //val view = findViewById<TextView>(R.id.answer_output).text.toString()
            textToSpeech.speak(answer, TextToSpeech.QUEUE_ADD, null, speechRequest.toString())
        }).start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == speechToTextRequestCode){
            if(resultCode == RESULT_OK && data != null){
                val result: ArrayList<String>? = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val question: String? = result?.get(0)

                if(!question.isNullOrEmpty()){
                    findViewById<TextView>(R.id.question_input).text = question

                    askWolfram(question)
                }
            }
        }
    }
}