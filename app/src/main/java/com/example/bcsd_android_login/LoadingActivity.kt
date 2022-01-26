package com.example.bcsd_android_login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.bcsd_android_login.databinding.ActivityLoadingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoadingBinding
    private val DELAY_LOADING: Long = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_loading)

        startLoading()
    }

    private fun startLoading() {
        val intent = Intent(this, LoginActivity::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            delay(DELAY_LOADING)
            startActivity(intent)
            finish()
        }
    }

}