package com.cmoney.kolfanci.service

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.cmoney.kolfanci.model.notification.CustomNotification
import com.cmoney.kolfanci.model.notification.NotificationHelper
import com.cmoney.kolfanci.model.notification.Payload
import com.cmoney.kolfanci.ui.SplashActivity
import com.cmoney.notify_library.fcm.CMoneyBaseMessagingService
import com.cmoney.notify_library.variable.CommonNotification
import com.google.firebase.messaging.RemoteMessage
import com.socks.library.KLog
import org.koin.android.ext.android.inject

class FcmMessagingService : CMoneyBaseMessagingService() {
    private val TAG = FcmMessagingService::class.java.simpleName
    private val notificationHelper by inject<NotificationHelper>()

    override fun dealWithCommonNotification(
        notification: CommonNotification,
        message: RemoteMessage
    ) {
        KLog.i(TAG, "dealWithCommonNotification:${message.data}")
        val customNotification = CustomNotification(message)
        handleNow(customNotification)
    }

    private fun handleNow(data: CustomNotification) {
        val payload = notificationHelper.getPayloadFromForeground(data)
        val title = payload.title.orEmpty()
        val message = payload.message.orEmpty()
        val sn = System.currentTimeMillis().toString().hashCode()
        createNotification(title, message, sn, payload)
    }

    private fun createNotification(
        title: String,
        message: String,
        sn: Int,
        payload: Payload
    ) {
        val intent = SplashActivity.createIntent(this, payload)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                sn,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        val notification = notificationHelper.getStyle0(title, message)
            .setContentIntent(pendingIntent)
            .build()

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@FcmMessagingService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(sn, notification)
        }
    }
}