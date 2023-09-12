package com.cmoney.kolfanci.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cmoney.kolfanci.BuildConfig
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.goAppStore
import com.cmoney.kolfanci.extension.startActivity
import com.cmoney.kolfanci.model.notification.NotificationHelper
import com.cmoney.kolfanci.model.notification.Payload
import com.cmoney.kolfanci.model.usecase.DynamicLinkUseCase
import com.cmoney.kolfanci.ui.main.MainActivity
import com.cmoney.remoteconfig_library.model.config.AppStatus
import com.cmoney.xlogin.XLoginHelper
import com.google.firebase.messaging.FirebaseMessaging
import com.socks.library.KLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {
    private val TAG = SplashActivity::class.java.simpleName

    private val notificationHelper by inject<NotificationHelper>()

    private val dynamicLinksUseCase by inject<DynamicLinkUseCase>()

    private val viewModel by inject<SplashViewModel>()

    companion object {
        fun createIntent(context: Context, payload: Payload): Intent {
            return Intent(context, SplashActivity::class.java)
                .putExtra(EXTRA_PUSH_NOTIFICATION_PAYLOAD, payload)
        }

        private const val EXTRA_PUSH_NOTIFICATION_PAYLOAD = "push_notification_payload"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initObserve()
        showDebugInfo()
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
                            startMainActivity()
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
                    startMainActivity()
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
                            startMainActivity()
                        }
                        .create()
                        .show()
                }
            }
        }
    }

    private fun startMainActivity() {
        lifecycleScope.launch {
            delay(1500)

            val backgroundPayload =
                intent.getParcelableExtra<Payload>(EXTRA_PUSH_NOTIFICATION_PAYLOAD)
                    ?: notificationHelper.getPayloadFromBackground(intent)
            if (backgroundPayload != null) {
                MainActivity.start(this@SplashActivity, backgroundPayload)
                finish()
                return@launch
            }

            dynamicLinksUseCase.getDynamicsLinksParam(intent)?.apply {
                MainActivity.start(this@SplashActivity, this)
                clearIntentExtra()
            } ?: kotlin.run {
                startActivity<MainActivity>()
            }
            finish()
        }
    }

    private fun clearIntentExtra() {
        intent.replaceExtras(Bundle())
        intent.action = ""
        intent.data = null
        intent.flags = 0
    }

    private fun showDebugInfo() {
        if (BuildConfig.DEBUG) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                val token = it.result
                KLog.i(TAG, "push token:$token")
            }

            KLog.i(TAG, "memberId:" + XLoginHelper.memberPk)
        }
    }
}