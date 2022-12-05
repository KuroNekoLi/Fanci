package com.cmoney.fanci.ui.screens.group.setting.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.extension.EmptyBodyException
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanci.model.usecase.ThemeUseCase
import com.cmoney.fanci.ui.screens.group.setting.groupsetting.theme.model.GroupTheme
import com.cmoney.fanciapi.fanci.model.ColorTheme
import com.cmoney.fanciapi.fanci.model.Group
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class GroupSettingUiState(
    val settingGroup: Group? = null,
    val isLoading: Boolean = false,
    val isGroupSettingPop: Boolean = false,
    val groupAvatarLib: List<String> = emptyList(),  //社團 預設大頭貼 清單
    val groupCoverLib: List<String> = emptyList(),  //社團 預設背景 清單
    val groupThemeList: List<GroupTheme> = emptyList(),  //社團 主題色彩
    val previewTheme: GroupTheme? = null            //社團 設定主題 Preview
)

class GroupSettingViewModel(
    private val groupUseCase: GroupUseCase,
    private val themeUseCase: ThemeUseCase
) : ViewModel() {
    private val TAG = GroupSettingViewModel::class.java.simpleName

    var uiState by mutableStateOf(GroupSettingUiState())
        private set

    /**
     * 更換 社團 簡介
     * @param desc 簡介
     * @param group 社團 model
     */
    fun changeGroupDesc(desc: String, group: Group) {
        viewModelScope.launch {
            groupUseCase.changeGroupDesc(desc, group).fold({
            }, {
                KLog.e(TAG, it)
                if (it is EmptyBodyException) {
                    uiState = uiState.copy(
                        settingGroup = group.copy(
                            description = desc
                        ),
                        isGroupSettingPop = true
                    )
                }
            }
            )
        }
    }

    /**
     * 更換 社團名字
     * @param name 更換的名字
     * @param group 社團 model
     */
    fun changeGroupName(name: String, group: Group) {
        KLog.i(TAG, "changeGroupNameL$name")
        viewModelScope.launch {
            groupUseCase.changeGroupName(name, group).fold({
            }, {
                KLog.e(TAG, it)
                if (it is EmptyBodyException) {
                    uiState = uiState.copy(
                        settingGroup = group.copy(
                            name = name
                        ),
                        isGroupSettingPop = true
                    )
                }
            }
            )
        }
    }

    /**
     * 更換社團info 結束
     */
    fun changeGroupInfoScreenDone() {
        uiState = uiState.copy(
            isGroupSettingPop = false
        )
    }

    /**
     * 更換社團 頭貼
     */
    fun changeGroupAvatar(uri: Any, group: Group) {
        KLog.i(TAG, "changeGroupAvatar")
        viewModelScope.launch {
            loading()
            groupUseCase.changeGroupAvatar(uri, group).collect {
                uiState = uiState.copy(
                    isLoading = false,
                    settingGroup = group.copy(
                        thumbnailImageUrl = it
                    ),
                    isGroupSettingPop = true
                )
            }
        }
    }

    /**
     * 更換 社團 背景圖
     */
    fun changeGroupCover(uri: Any, group: Group) {
        KLog.i(TAG, "changeGroupCover")
        viewModelScope.launch {
            loading()
            groupUseCase.changeGroupBackground(uri, group).collect {
                uiState = uiState.copy(
                    isLoading = false,
                    settingGroup = group.copy(
                        coverImageUrl = it
                    ),
                    isGroupSettingPop = true
                )
            }
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
     * Usr 選擇的 預設 大頭貼
     */
    fun onGroupAvatarSelect(url: String, group: Group) {
        KLog.i(TAG, "onGroupAvatarSelect:$url")
        uiState = uiState.copy(
            settingGroup = group.copy(
                thumbnailImageUrl = url
            )
        )
    }

    /**
     * Usr 選擇的 預設 背景
     */
    fun onGroupCoverSelect(url: String, group: Group) {
        KLog.i(TAG, "onGroupCoverSelect:$url")
        uiState = uiState.copy(
            settingGroup = group.copy(
                coverImageUrl = url
            )
        )
    }

    /**
     * 抓取所有主題設定檔案
     * @param group 目前的主題
     */
    fun fetchAllTheme(group: Group) {
        KLog.i(TAG, "fetchAllTheme:$group")
        viewModelScope.launch {
            val currentThemeName = group.colorSchemeGroupKey?.name.orEmpty()

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
     * 更換 主題
     * @param groupTheme 要更換的主題
     */
    fun changeTheme(group: Group, groupTheme: GroupTheme) {
        KLog.i(TAG, "changeTheme: $groupTheme")
        viewModelScope.launch {
            themeUseCase.changeGroupTheme(group, groupTheme).fold({

            }, {
                KLog.e(TAG, it)
                if (it is EmptyBodyException) {
                    uiState = uiState.copy(
                        settingGroup = group.copy(
                            colorSchemeGroupKey = ColorTheme.decode(groupTheme.id)
                        ),
                        groupThemeList = uiState.groupThemeList.map {
                            if (it == groupTheme) {
                                groupTheme.copy(isSelected = true)
                            } else {
                                it.copy(isSelected = false)
                            }
                        },
                        previewTheme = groupTheme.copy(isSelected = true),
                        isGroupSettingPop = true
                    )
                }
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
            settingGroup =  null
        )
    }

}