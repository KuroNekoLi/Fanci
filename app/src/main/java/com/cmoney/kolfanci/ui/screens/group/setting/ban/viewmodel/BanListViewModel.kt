package com.cmoney.kolfanci.ui.screens.group.setting.ban.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.User
import com.cmoney.kolfanci.model.usecase.BanUseCase
import com.socks.library.KLog
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class UiState(
    val banUserList: List<BanUiModel>? = null,
    val loading: Boolean = true
)

data class BanUiModel(
    val user: GroupMember?,
    val startDay: String,   //ex: 2020/10/22
    val duration: String    //ex: 3日
)

class BanListViewModel(private val banUseCase: BanUseCase) : ViewModel() {
    private val TAG = BanListViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    private fun showLoading() {
        uiState = uiState.copy(
            loading = true
        )
    }

    private fun dismissLoading() {
        uiState = uiState.copy(
            loading = false
        )
    }

    /**
     * 抓取禁言列表資料
     */
    fun fetchBanList(groupId: String) {
        KLog.i(TAG, "fetchBanList:$groupId")
        viewModelScope.launch {
            showLoading()
            banUseCase.fetchBanList(groupId).fold({
                val banUiModelList = it.map { userBanInformation ->
                    val date = Date(
                        userBanInformation.startDateTime?.times(1000) ?: System.currentTimeMillis()
                    )
                    val startDay = SimpleDateFormat("yyyy/MM/dd").format(date)

                    val oneDaySecond = TimeUnit.DAYS.toSeconds(1)
                    var duration = 0
                    userBanInformation.panaltySeconds?.let { second ->
                        duration = (second / oneDaySecond).toInt()
                    }

                    val durationStr = if (duration > 365) {
                        "永久"
                    } else {
                        "%d日".format(duration)
                    }

                    BanUiModel(
                        user = userBanInformation.user,
                        startDay = startDay,
                        duration = durationStr
                    )
                }

                uiState = uiState.copy(
                    banUserList = banUiModelList
                )

                dismissLoading()
            }, {
                dismissLoading()
                
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 解除 禁言
     */
    fun liftBanUser(userId: String, groupId: String) {
        KLog.i(TAG, "liftBanUser:$userId")
        viewModelScope.launch {
            banUseCase.liftBanUser(
                groupId = groupId,
                userIds = listOf(userId)
            ).fold({
                val newBanList = uiState.banUserList?.filter {
                    it.user?.id != userId
                }
                uiState = uiState.copy(
                    banUserList = newBanList
                )

            }, {
                KLog.e(TAG, it)
            })
        }
    }

}