package com.cmoney.kolfanci.model.analytics

import android.util.Log
import com.cmoney.analytics.user.model.event.UserEvent
import com.cmoney.application_user_behavior.AnalyticsAgent
import com.cmoney.application_user_behavior.model.event.logClicked
import com.cmoney.application_user_behavior.model.event.logPageViewed
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.BuildConfig
import com.cmoney.kolfanci.model.Constant
import com.cmoney.xlogin.XLoginHelper
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppUserLogger : KoinComponent {
    private val mixpanel: MixpanelAPI by inject()

    companion object {
        private val _instance by lazy {
            AppUserLogger()
        }

        fun getInstance(): AppUserLogger {
            return _instance
        }
    }

    fun log(event: UserEvent) {
        logMixpanel(event)
        debugLog(event.name, event.getParameters())
    }

    fun log(page: Page, from: From? = null) {
        val descriptions = from?.asParameters()
        val event = PageUserEvent(page = page.eventName, parameters = descriptions)
//        logMixpanel(event)
        logCM(page)
        debugLog(event.name, event.getParameters())
    }

    fun log(clicked: Clicked, from: From? = null) {
        val descriptions = from?.asParameters()
        val event = ClickedUserEvent(clicked = clicked.eventName, parameters = descriptions)
        logCM(clicked = clicked, descriptions = descriptions)
        debugLog(event.name, event.getParameters())
    }

    fun log(eventName: String, parameters: Map<String, Any>? = null) {
        val event = OtherEvent(
            eventName, parameters
        )
        logCM(eventName = eventName, descriptions = parameters)
        debugLog(event.name, event.getParameters())
    }

    class PageUserEvent(val page: String, parameters: Map<String, Any>? = null) : UserEvent() {
        override val name: String
            get() = page

        init {
            parameters?.let {
                val ps = it.toList().map { (key, value) ->
                    key to value.toString()
                }.toTypedArray()
                setParameters(*ps)
            }
        }
    }

    class ClickedUserEvent(private val clicked: String, parameters: Map<String, Any>? = null) :
        UserEvent() {
        override val name: String
            get() = clicked

        init {
            parameters?.let {
                val ps = it.toList().map { (key, value) ->
                    key to value.toString()
                }.toTypedArray()
                setParameters(*ps)
            }
        }
    }

    class OtherEvent(private val eventName: String, parameters: Map<String, Any>? = null) :
        UserEvent() {
        override val name: String
            get() = eventName

        init {
            parameters?.let {
                val ps = it.toList().map { (key, value) ->
                    key to value.toString()
                }.toTypedArray()
                setParameters(*ps)
            }
        }
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
     * Log to CMoney
     */
    private fun logCM(clicked: Clicked, descriptions: Map<String, Any>? = null) {
        AnalyticsAgent.getInstance().logClicked(
            name = clicked.eventName,
            descriptions = descriptions
        )
    }

    /**
     * Log to CMoney
     */
    private fun logCM(eventName: String, descriptions: Map<String, Any>? = null) {
        AnalyticsAgent.getInstance().logEvent(
            name = eventName,
            descriptions = descriptions
        )
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