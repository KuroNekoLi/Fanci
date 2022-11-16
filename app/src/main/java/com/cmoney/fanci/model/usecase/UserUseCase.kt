package com.cmoney.fanci.model.usecase

import com.cmoney.fanciapi.fanci.api.UserApi
import com.cmoney.fanciapi.fanci.model.UserParam
import com.cmoney.xlogin.XLoginHelper

class UserUseCase(private val userApi: UserApi) {

    suspend fun registerUser() = kotlin.runCatching {
        userApi.apiV1UserMePut(
            UserParam(
                name = XLoginHelper.nickName,
                thumbNail = XLoginHelper.headImagePath
            )
        )
    }
}