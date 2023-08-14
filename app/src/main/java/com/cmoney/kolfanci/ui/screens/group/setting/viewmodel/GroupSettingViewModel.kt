package com.cmoney.kolfanci.ui.screens.group.setting.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.ColorTheme
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.ReportInformation
import com.cmoney.kolfanci.extension.EmptyBodyException
import com.cmoney.kolfanci.model.usecase.GroupApplyUseCase
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.cmoney.kolfanci.model.usecase.ThemeUseCase
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.theme.model.GroupTheme
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GroupSettingUiState(
    val settingGroup: Group? = null,
    val isLoading: Boolean = false,
    val groupAvatarLib: List<String> = emptyList(),         //社團 預設大頭貼 清單
    val groupCoverLib: List<String> = emptyList(),          //社團 預設背景 清單
    val groupThemeList: List<GroupTheme> = emptyList(),     //社團 主題色彩
    val previewTheme: GroupTheme? = null,                   //社團 設定主題 Preview
    val unApplyCount: Long? = null,                         //等待加入申請數量
    val showDelectDialog: Boolean = false,                  //是否呈現解散彈窗
    val showFinalDelectDialog: Boolean = false,             //是否呈現最後解散彈窗
    val popToMain: Boolean = false                          //跳回首頁
)

class GroupSettingViewModel(
    private val groupUseCase: GroupUseCase,
    private val themeUseCase: ThemeUseCase,
    private val groupApplyUseCase: GroupApplyUseCase,
) : ViewModel() {
    private val TAG = GroupSettingViewModel::class.java.simpleName

    var uiState by mutableStateOf(GroupSettingUiState())
        private set

    //檢舉清單
    private val _reportList = MutableStateFlow<List<ReportInformation>>(emptyList())
    val reportList = _reportList.asStateFlow()

    fun settingGroup(group: Group) {
        KLog.i(TAG, "settingGroup:$group")
        uiState = uiState.copy(
            settingGroup = group
        )
    }

    /**
     * 抓取 檢舉清單
     */
    fun fetchReportList(groupId: String) {
        KLog.i(TAG, "fetchReportList:$groupId")
        viewModelScope.launch {
            groupUseCase.getReportList(groupId = groupId).fold({
                _reportList.value = it
            }, {
                it.printStackTrace()
                KLog.i(TAG, it)
            })
        }
    }

    /**
     * 抓取 加入申請 數量
     * @param groupId 社團 id
     */
    fun fetchUnApplyCount(groupId: String) {
        viewModelScope.launch {
            groupApplyUseCase.getUnApplyCount(groupId = groupId).fold({
                uiState = uiState.copy(
                    unApplyCount = it.count
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 抓取 預設大頭貼 清單
     */
    fun fetchFanciAvatarLib() {
        KLog.i(TAG, "fetchFanciAvatarLib")
        viewModelScope.launch {
            loading()
            groupUseCase.fetchGroupAvatarLib().fold({
                uiState = uiState.copy(
                    groupAvatarLib = it,
                    isLoading = false
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 抓取 預設背景 清單
     */
    fun fetchFanciCoverLib() {
        KLog.i(TAG, "fetchFanciCoverLib")
        viewModelScope.launch {
            loading()
            groupUseCase.fetchGroupCoverLib().fold({
                uiState = uiState.copy(
                    groupCoverLib = it,
                    isLoading = false
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    private fun loading() {
        uiState = uiState.copy(
            isLoading = true
        )
    }

    /**
     * 抓取所有主題設定檔案
     * @param group 目前的主題
     */
    fun fetchAllTheme(group: Group?) {
        KLog.i(TAG, "fetchAllTheme:$group")
        viewModelScope.launch {
            val currentThemeName = group?.colorSchemeGroupKey?.name.orEmpty()
            themeUseCase.fetchAllThemeConfig().fold({
                uiState = uiState.copy(
                    groupThemeList = it.map { item ->
                        if (item.id.lowercase() == currentThemeName.lowercase()) {
                            item.copy(isSelected = true)
                        } else {
                            item
                        }
                    }
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 抓取 特定主題 資訊
     * @param themeId 主題Key ex: ThemeBabyBlue
     */
    fun fetchThemeInfo(themeId: String) {
        KLog.i(TAG, "fetchThemeInfo:$themeId")
        viewModelScope.launch {
            ColorTheme.decode(themeId)?.let {
                themeUseCase.fetchThemeConfig(it).fold({ groupTheme ->
                    uiState = uiState.copy(
                        previewTheme = groupTheme
                    )
                }, {
                    KLog.e(TAG, it)
                })
            }
        }
    }

    /**
     * Reset 要設定的model.
     */
    fun resetSettingGroup() {
        uiState = uiState.copy(
            settingGroup = null
        )
    }

    /**
     * 最終 確認刪除
     */
    fun onFinalConfirmDelete(group: Group) {
        KLog.i(TAG, "onFinalConfirmDelete:$group")
        viewModelScope.launch {
            groupUseCase.deleteGroup(groupId = group.id.orEmpty()).fold({
            }, {
                if (it is EmptyBodyException) {
                    KLog.i(TAG, "Group delete complete.")
                    uiState = uiState.copy(
                        popToMain = true
                    )
                } else {
                    KLog.e(TAG, it)
                }
            })
        }
    }
}