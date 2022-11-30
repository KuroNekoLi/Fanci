package com.cmoney.fanci.ui.screens.group.setting.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.extension.EmptyBodyException
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanciapi.fanci.model.Group
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class GroupSettingUiState(
    val settingGroup: Group? = null,
    val isLoading: Boolean = false,
    val isGroupSettingPop: Boolean = false,
)

class GroupSettingViewModel(
    private val groupUseCase: GroupUseCase
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

        //test
//        uiState = uiState.copy(
//            settingGroup = group.copy(
//                name = name
//            ),
//            isGroupSettingNamePop = true
//        )

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
    fun changeGroupAvatar(uri: Uri, group: Group) {
        KLog.i(TAG, "changeGroupAvatar")
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = true
            )
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
    fun changeGroupCover(uri: Uri, group: Group) {
        KLog.i(TAG, "changeGroupCover")
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = true
            )
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

}