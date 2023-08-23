package com.cmoney.kolfanci.model.notification

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.fromJson
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.socks.library.KLog
import org.json.JSONObject
import java.net.URLDecoder

class NotificationHelper(
    application: Application,
    val gson: Gson
) : ContextWrapper(application) {

    private val TAG = NotificationHelper::class.java.simpleName
    private val DEFAULT_CHANNEL_ID = getString(R.string.default_notification_channel_id)
    private val DEFAULT_CHANNEL_ID_MESSAGE =
        getString(R.string.default_notification_channel_message)
    private val manager: NotificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
        val customData = customNotification.customData
        val deeplink = customNotification.deeplink

        return Payload(
            sn = sn,
            title = title,
            message = message,
            commonTargetType = commonTargetType,
            commonParameter = commonParameter,
            targetType = targetType,
            parameter = customData,
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
        val customData = bundle.getString(CustomNotification.CUSTOM_DATA_PARAMETER).orEmpty()
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
            parameter = customData,
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

    fun convertPayloadToTargetType(payload: Payload): TargetType? {
        KLog.i(TAG, "convertPayloadToTargetType:" + payload.targetType)
        return when (payload.targetType) {
            0 -> {
                if (payload.deeplink?.isNotEmpty() == true) {
                    val jsonObject = JSONObject(payload.deeplink)
                    val targetTypeLowCase = jsonObject.optInt("targetType", 0)
                    jsonObject.remove("targetType")
                    val targetTypeHighCase = jsonObject.optInt("TargetType", 0)
                    jsonObject.remove("TargetType")
                    val targetType =
                        if (targetTypeLowCase == 0) targetTypeHighCase else targetTypeLowCase

                    if (targetType == 0) {
                        return null
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
        return NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
            .setSmallIcon(R.drawable.all_member)
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