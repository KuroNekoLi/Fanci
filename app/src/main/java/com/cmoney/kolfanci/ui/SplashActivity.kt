package com.cmoney.kolfanci.ui

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.goAppStore
import com.cmoney.kolfanci.extension.startActivity
import com.cmoney.kolfanci.ui.main.MainActivity
import com.cmoney.remoteconfig_library.model.config.AppStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {

    private val viewModel by inject<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initObserve()
    }

    private fun initObserve() {
        viewModel.appConfig.observe(this) { appConfig ->
            when (appConfig.appStatus) {
                is AppStatus.SuggestUpdate -> {
                    AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle(getString(R.string.text_remote_config_update_title))
                        .setMessage(appConfig.appStatus.announcement)
                        .setPositiveButton(getString(R.string.update_immediately)) { _, _ ->
                            this@SplashActivity.goAppStore()
                        }
                        .setNegativeButton(getString(R.string.later)) { _, _ ->
                            viewModel.checkDynamicLink(intent)
                        }
                        .create()
                        .show()
                }

                is AppStatus.NeedUpdate -> {
                    AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle(getString(R.string.text_remote_config_update_title))
                        .setMessage(appConfig.appStatus.announcement)
                        .setPositiveButton(getString(R.string.update_immediately)) { _, _ ->
                            this@SplashActivity.goAppStore()
                        }
                        .create()
                        .show()
                }

                is AppStatus.CanUse -> {
                    viewModel.checkDynamicLink(intent)
                }

                is AppStatus.IsInMaintain -> {
                    AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle(getString(R.string.text_remote_config_under_maintain_title))
                        .setMessage(appConfig.appStatus.announcement)
                        .setPositiveButton(getString(R.string.text_remote_config_under_maintain_confirm_button)) { _, _ ->
                            finish()
                        }
                        .create()
                        .show()
                }

                is AppStatus.IsUnderReview -> {
                    AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle(getString(R.string.text_remote_config_under_review_title))
                        .setMessage(getString(R.string.text_remote_config_under_review_message))
                        .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                            finish()
                        }
                        .create()
                        .show()
                }
            }
        }

        viewModel.intentPayload.observe(this) { payLoad ->
            lifecycleScope.launch {
                payLoad?.let {
                    MainActivity.start(this@SplashActivity, it)
                } ?: kotlin.run {
                    delay(1500)
                    startActivity<MainActivity>()
                }
                finish()
            }
        }
    }

}