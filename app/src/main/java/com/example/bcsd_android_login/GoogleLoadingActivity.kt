package com.example.bcsd_android_login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.bcsd_android_login.databinding.ActivityGoogleLoadingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GoogleLoadingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityGoogleLoadingBinding
    private val DELAY_LOADING: Long = 2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_google_loading)
        val nickname = intent.getStringExtra("nickname")
        val photoUrl = intent.getStringExtra("photoUrl")

        binding.userNickname.text = nickname
        Glide.with(this)
            .load(photoUrl)
            .override(400).centerInside()
            .into(binding.userDisplay)

        startLoading()
    }

    private fun startLoading() {
        val intent = Intent(this, MainActivity::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            delay(DELAY_LOADING)
            startActivity(intent)
            finish()
        }
    }
}

