package com.cmoney.kolfanci.model.notification

import android.net.Uri
import com.cmoney.kolfanci.extension.fromJson
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.socks.library.KLog
import org.json.JSONObject
import java.net.URLDecoder

class NotificationHelper(
    val gson: Gson
) {

    private val TAG = NotificationHelper::class.java.simpleName

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

        return Payload(
            title = "",
            message = "",
            targetType = targetType,
            parameter = Gson().toJson(GroupModel(groupId)),
            analyticsId = null,
            commonParameter = "",
            commonTargetType = targetType,
            sn = null
        )
    }

    private class GroupModel(val groupId: String)

    fun convertPayloadToTargetType(payload: Payload): TargetType? {
        KLog.i(TAG, "convertPayloadToTargetType:" + payload.targetType)
        return when (payload.targetType) {
            0 -> {
                if (payload.parameter.isNotEmpty()) {
                    val jsonObject = JSONObject(payload.parameter)
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
}