package com.cmoney.kolfanci.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.startActivity
import com.cmoney.kolfanci.model.usecase.DynamicLinkUseCase
import com.cmoney.kolfanci.ui.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {
    private val dynamicLinkUseCase by inject<DynamicLinkUseCase>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            dynamicLinkUseCase.getDynamicsLinksParam(intent)?.let {
                MainActivity.start(this@SplashActivity, it)
            } ?: kotlin.run {
                delay(1500)
                startActivity<MainActivity>()
            }
            finish()
        }
    }
}