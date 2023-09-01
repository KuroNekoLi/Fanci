package com.cmoney.kolfanci.model.notification

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.text.isDigitsOnly
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.fromJson
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.socks.library.KLog
import org.json.JSONObject
import java.net.URLDecoder


class NotificationHelper(
    val context: Application,
    val gson: Gson
) {
    private val TAG = NotificationHelper::class.java.simpleName
    private val DEFAULT_CHANNEL_ID = context.getString(R.string.default_notification_channel_id)
    private val DEFAULT_CHANNEL_ID_MESSAGE =
        context.getString(R.string.default_notification_channel_message)
    private val manager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var default = manager.getNotificationChannel(DEFAULT_CHANNEL_ID)
            if (default == null) {
                default = NotificationChannel(
                    DEFAULT_CHANNEL_ID,
                    DEFAULT_CHANNEL_ID_MESSAGE,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                manager.createNotificationChannel(default)
            }
            default.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
    }

    fun getPayloadFromForeground(customNotification: CustomNotification): Payload {
        val commonParameter = customNotification.commonParameter
        val commonTargetType = customNotification.commonTargetType
        val targetType = customNotification.targetType
        val title = customNotification.title
        val message = customNotification.message
        val sn = customNotification.sn
        val analyticsId = customNotification.analyticsId
        val parameter = customNotification.parameter
        val deeplink = customNotification.deeplink

        return Payload(
            sn = sn,
            title = title,
            message = message,
            commonTargetType = commonTargetType,
            commonParameter = commonParameter,
            targetType = targetType,
            parameter = parameter,
            analyticsId = analyticsId,
            deeplink = deeplink
        )
    }

    fun getPayloadFromBackground(intent: Intent): Payload? {
        KLog.d("notification", "getPayloadFromBackground")
        val bundle = intent.extras ?: return null
        val customTargetType =
            bundle.get(CustomNotification.CUSTOM_TARGET_TYPE).toString().toIntOrNull()
                ?: return null

        val sn = bundle.getString(CustomNotification.SN)?.toLongOrNull()
        val title = bundle.getString(CustomNotification.TITLE).orEmpty()
        val message = bundle.getString(CustomNotification.MESSAGE).orEmpty()
        val targetType = if (customTargetType == -1) {
            bundle.getString(CustomNotification.TARGET_TYPE)?.toIntOrNull() ?: 0
        } else {
            customTargetType
        }
        val parameter = bundle.getString(CustomNotification.PARAMETER).orEmpty()
        val commonTargetType =
            bundle.getString(CustomNotification.COMMON_TARGET_TYPE)?.toIntOrNull() ?: 0
        val commonParameter = bundle.getString(CustomNotification.COMMON_PARAMETER).orEmpty()
        val analyticsId = bundle.getString(CustomNotification.ANALYTICS_ID)?.toLongOrNull()
        val deeplink = bundle.getString(CustomNotification.DEEPLINK).orEmpty()

        return Payload(
            sn = sn,
            title = title,
            message = message,
            commonTargetType = commonTargetType,
            commonParameter = commonParameter,
            targetType = targetType,
            parameter = parameter,
            analyticsId = analyticsId,
            deeplink = deeplink
        )
    }

    fun getShareIntentPayload(uri: Uri): Payload {
        val decodeString = URLDecoder.decode(uri.toString(), "UTF-8")
        val decodeUrl = Uri.parse(decodeString)
        val customTargetType =
            decodeUrl.getQueryParameter("commonTargetType")?.toIntOrNull() ?: -1
        val targetType = if (customTargetType == -1) {
            decodeUrl.getQueryParameter("targetType")?.toIntOrNull() ?: 0
        } else {
            customTargetType
        }
        val groupId =
            decodeUrl.getQueryParameter("groupId").orEmpty()

        val deeplink = decodeUrl.getQueryParameter("deeplink").orEmpty()

        return Payload(
            title = "",
            message = "",
            targetType = targetType,
            parameter = Gson().toJson(GroupModel(groupId)),
            analyticsId = null,
            commonParameter = "",
            commonTargetType = targetType,
            sn = null,
            deeplink = deeplink
        )
    }

    private class GroupModel(val groupId: String)

    /**
     * payload.deeplink ex:"https://www.fanci.com.tw/landing?targetType=2&serialNumber=2455&groupId=27444&channelId=31913"
     */
    fun convertPayloadToTargetType(payload: Payload): TargetType? {
        KLog.i(TAG, "convertPayloadToTargetType:" + payload.targetType)
        return when (payload.targetType) {
            0 -> {
                if (payload.deeplink?.isNotEmpty() == true) {
                    val uri = Uri.parse(payload.deeplink)
                    val targetTypeLowCase = uri.getQueryParameter("targetType")
                    val targetTypeHighCase = uri.getQueryParameter("TargetType")
                    val targetTypeStr = targetTypeLowCase ?: targetTypeHighCase

                    if (targetTypeStr?.isDigitsOnly() == false) {
                        return null
                    }

                    val targetType = targetTypeStr?.toInt() ?: 0

                    if (targetType == 0) {
                        return null
                    }

                    val jsonObject = JSONObject()
                    val allKey = uri.queryParameterNames
                    for (key in allKey) {
                        val value = uri.getQueryParameter(key)
                        jsonObject.put(key, value)
                    }

                    val parameter = jsonObject.toString()
                    val payload = payload.copy(
                        targetType = targetType,
                        parameter = parameter
                    )
                    return convertPayloadToTargetType(payload)
                }
                null
            }

            Payload.TYPE_1 -> {
                getParameter<TargetType.InviteGroup>(payload.parameter)
            }

            Payload.TYPE_2 -> {
                getParameter<TargetType.ReceiveMessage>(payload.parameter)
            }

            Payload.TYPE_3 -> {
                getParameter<TargetType.ReceivePostMessage>(payload.parameter)
            }

            Payload.TYPE_4 -> {
                getParameter<TargetType.DissolveGroup>(payload.parameter)
            }

            else -> getParameter<TargetType.InviteGroup>(payload.parameter)
        }
    }

    private inline fun <reified T> getParameter(originalMessage: String): T? {
        return try {
            gson.fromJson<T>(originalMessage)
        } catch (e: JsonSyntaxException) {
            null
        } catch (e: JsonParseException) {
            null
        }
    }

    fun getStyle0(title: String, body: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, DEFAULT_CHANNEL_ID)
            .setSmallIcon(R.drawable.status_icon)
//            .setColor(ContextCompat.getColor(this, R.color.color_ddaf78))
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText(body)
            )
    }
}