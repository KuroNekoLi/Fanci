package com.cmoney.kolfanci.model.persistence

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.cmoney.kolfanci.model.Constant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "fanci_preferences"
)

class SettingsDataStore(private val preference_datastore: DataStore<Preferences>) {
    private val IS_TUTORIAL = booleanPreferencesKey("is_tutorial")  //是否新手導引過
    private val hasNotifyAllowNotificationPermissionKey
        get() = booleanPreferencesKey(getHasNotifyAllowNotificationPermissionKey())
    private val IS_SHOW_BUBBLE = booleanPreferencesKey("is_show_bubble")  //建立社團後 是否出現 buble

    val isTutorial: Flow<Boolean> = preference_datastore.data.map {
        it[IS_TUTORIAL] ?: false
    }

    val isShowBubble: Flow<Boolean> = preference_datastore.data.map {
        it[IS_SHOW_BUBBLE] ?: false
    }

    suspend fun onTutorialOpen() {
        preference_datastore.edit { preferences ->
            preferences[IS_TUTORIAL] = true
        }
    }

    /**
     * 取得是否通知過使用者允許通知權限
     *
     * @return true 已經通知過, false 未通知或是未處理
     */
    suspend fun hasNotifyAllowNotificationPermission(): Boolean {
        return preference_datastore.data.first()[hasNotifyAllowNotificationPermissionKey] ?: false
    }

    /**
     * 已經通知過使用者允許通知權限
     */
    suspend fun alreadyNotifyAllowNotificationPermission() {
        preference_datastore.edit { preferences ->
            preferences[hasNotifyAllowNotificationPermissionKey] = true
        }
    }

    private fun getHasNotifyAllowNotificationPermissionKey(): String {
        return "${Constant.MyInfo?.id}_has_notify_allow_notification_permission"
    }

    suspend fun setHomeBubbleShow() {
        preference_datastore.edit { preferences ->
            preferences[IS_SHOW_BUBBLE] = true
        }
    }

    suspend fun alreadyShowHomeBubble() {
        preference_datastore.edit { preferences ->
            preferences[IS_SHOW_BUBBLE] = false
        }
    }
}