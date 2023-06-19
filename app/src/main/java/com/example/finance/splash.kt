package com.example.finance

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide

class splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val splashImg: ImageView = findViewById(R.id.splashImg)
        Glide.with(this)
            .load(R.drawable.hand)
            .into(splashImg)
        val continueBtn: Button = findViewById(R.id.continueBtn)
        continueBtn.setOnClickListener {
            val intent = Intent(this@splash, MainActivity::class.java)
            intent.putExtra("passed", true)
            startActivity(intent)
        }
    }
}