package com.cmoney.kolfanci.model.analytics

import android.util.Log
import com.cmoney.analytics.user.model.event.UserEvent
import com.cmoney.application_user_behavior.AnalyticsAgent
import com.cmoney.application_user_behavior.model.event.logPageSwitch
import com.cmoney.application_user_behavior.model.event.logPageViewed
import com.cmoney.kolfanci.BuildConfig
import com.cmoney.kolfanci.model.Constant
import com.cmoney.fancylog.model.data.Page
import com.cmoney.xlogin.XLoginHelper
import com.flurry.android.FlurryAgent
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppUserLogger : KoinComponent {
    private val mixpanel: MixpanelAPI by inject()

    companion object {
        fun getInstance(): AppUserLogger {
            return AppUserLogger()
        }
    }

    fun log(event: UserEvent) {
        logFlurry(event)
        logMixpanel(event)
        debugLog(event.name, event.getParameters())
    }

    fun log(page: Page) {
        val event = PageUserEvent(page = page.eventName)
//        logFlurry(event)
//        logMixpanel(event)
        logCM(page)
        debugLog(event.name, event.getParameters())
    }

    class PageUserEvent(val page: String) : UserEvent() {
        override val name: String
            get() = page
    }

    /**
     * Mixpanel 紀錄使用者 info
     */
    fun recordUserInfo() {
        Constant.MyInfo?.apply {
            mixpanel.identify(this.id)
            mixpanel.people.identify(this.id)
            mixpanel.people.set("\$name", this.name)
        }

        mixpanel.people.set("\$email", XLoginHelper.mail)
        mixpanel.people.set("\$memberPk", XLoginHelper.memberPk)
    }

    /**
     * log to CMoney
     */
    private fun logCM(page: Page) {
        AnalyticsAgent.getInstance().logPageViewed(page.eventName)
    }

    /**
     * 紀錄Flurry
     * limit:500
     */
    private fun logFlurry(event: UserEvent) {
        val parameters = event.getParameters()
        val parameterMap = mutableMapOf<String, String>()
        parameters.associateTo(parameterMap) { parameter ->
            parameter.key to parameter.value
        }
        FlurryAgent.logEvent(event.name, parameterMap)
    }

    /**
     * 記錄到 mixPanel
     */
    private fun logMixpanel(event: UserEvent) {
        val map = HashMap<String, Any>()
        try {
            val params = event.getParameters()
            params.forEach {
                map[it.key] = it.value
            }
        } catch (e: JSONException) {
        }
        // log到Mixpanel
        if (map.isEmpty()) {
            mixpanel.track(event.name)
        } else {
            mixpanel.trackMap(event.name, map)
        }
    }

    /**
     * 用來進行Debug時的事件紀錄輸出
     */
    private fun debugLog(
        eventName: String,
        eventParams: Set<UserEvent.Parameter>
    ) {
        if (!BuildConfig.DEBUG) {
            return
        }
        var message = eventName
        if (eventParams.isNotEmpty()) {
            val stringBuild = StringBuilder()
            eventParams.forEach { pair ->
                stringBuild.append(pair.key + ":")
                stringBuild.append(pair.value)
            }
            message = "$eventName, $stringBuild"
        }
        Log.d("LogEventListener", message)
    }
}