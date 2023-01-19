package com.cmoney.fanci.model.usecase

import com.cmoney.fanci.extension.checkResponseBody
import com.cmoney.fanciapi.fanci.api.UserApi
import com.cmoney.fanciapi.fanci.model.UserParam
import com.cmoney.xlogin.XLoginHelper

class UserUseCase(private val userApi: UserApi) {

    /**
     * 取得我的個人資訊
     */
    suspend fun fetchMyInfo() = kotlin.runCatching {
        userApi.apiV1UserMeGet().checkResponseBody()
    }

    suspend fun registerUser() = kotlin.runCatching {
        userApi.apiV1UserMePut(
            UserParam(
                name = XLoginHelper.nickName,
                thumbNail = XLoginHelper.headImagePath
            )
        )
    }
}