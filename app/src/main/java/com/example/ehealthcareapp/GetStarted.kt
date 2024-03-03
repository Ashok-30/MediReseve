package com.example.ehealthcareapp
import com.example.ehealthcareapp.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class GetStarted : AppCompatActivity() {
    private lateinit var startButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        startButton = findViewById(R.id.startButton)
        startButton.setOnClickListener(View.OnClickListener {
            val i = Intent(this@GetStarted, MainActivity::class.java)
            startActivity(i)
            finish()
        })
    }
}