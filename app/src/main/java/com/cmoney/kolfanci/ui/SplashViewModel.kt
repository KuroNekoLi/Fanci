package com.cmoney.kolfanci.ui

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.kolfanci.BuildConfig
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.notification.Payload
import com.cmoney.kolfanci.model.usecase.DynamicLinkUseCase
import com.cmoney.remoteconfig_library.IRemoteConfig
import com.cmoney.remoteconfig_library.RemoteConfigSettingImpl
import com.cmoney.remoteconfig_library.model.config.AppConfig
import com.socks.library.KLog
import kotlinx.coroutines.launch

class SplashViewModel(
    private val remoteConfig: IRemoteConfig,
    private val dynamicLinkUseCase: DynamicLinkUseCase
) : ViewModel() {
    private val TAG = SplashViewModel::class.java.simpleName

    private val _appConfig = MutableLiveData<AppConfig>()
    val appConfig: LiveData<AppConfig> = _appConfig

    private val _intentPayload = MutableLiveData<Payload?>()
    val intentPayload: LiveData<Payload?> = _intentPayload

    init {
        fetchRemoteConfig()
    }

    private fun fetchRemoteConfig() {
        viewModelScope.launch {
            val remoteConfigSetting =
                RemoteConfigSettingImpl(BuildConfig.VERSION_CODE, R.xml.remote_config_defaults)
            val updateResult = remoteConfig.updateAppConfig(remoteConfigSetting)
            updateResult.fold({ fetchAndActivateResult ->
                KLog.i(TAG, fetchAndActivateResult)
                _appConfig.value = fetchAndActivateResult.appConfig
            }, { exception ->
                KLog.e(TAG, exception)
                //統一使用舊的AppConfig繼續
                val oldAppConfig = remoteConfig.getAppConfig()
                _appConfig.value = oldAppConfig
            })
        }
    }

    /**
     * 檢查 dynamic link
     */
    fun checkDynamicLink(intent: Intent) {
        viewModelScope.launch {
            _intentPayload.value = dynamicLinkUseCase.getDynamicsLinksParam(intent)
        }
    }

}