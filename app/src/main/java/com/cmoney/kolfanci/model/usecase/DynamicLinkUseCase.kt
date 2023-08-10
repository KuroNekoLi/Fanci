package com.cmoney.kolfanci.model.usecase

import android.content.Intent
import android.net.Uri
import com.cmoney.kolfanci.BuildConfig
import com.cmoney.kolfanci.model.notification.NotificationHelper
import com.cmoney.kolfanci.model.notification.Payload
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.socks.library.KLog
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DynamicLinkUseCase(private val notificationHelper: NotificationHelper) {
    private val TAG = DynamicLinkUseCase::class.java.simpleName

    suspend fun createInviteGroupLink(groupId: String): String? = suspendCoroutine { cont ->
        val baseLink =
            "https://www.fanclub.com.tw/topic?targetType=1&groupId=$groupId"
        Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
            link = Uri.parse(baseLink)
            domainUriPrefix = "https://invite.fanci.com.tw/invite"
            androidParameters(BuildConfig.APPLICATION_ID) {
            }
            iosParameters("CMoney.KOLfanci") {
            }
        }.addOnSuccessListener {
            KLog.i(TAG, "createInviteTeamLink:" + it.shortLink)
            cont.resume(it.shortLink.toString())
        }.addOnFailureListener {
            cont.resume(null)
        }
    }

    suspend fun getDynamicsLinksParam(intent: Intent): Payload? = suspendCoroutine { cont ->
        var intentParam: Payload? = null
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result?.link?.let { deepLink ->
                        intentParam = notificationHelper.getShareIntentPayload(deepLink)
                    }
                }
                cont.resume(intentParam)
            }
    }

}