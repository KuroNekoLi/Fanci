package com.cmoney.kolfanci.ui.screens.shared.member.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.model.state.LoadingState
import com.cmoney.kolfanci.model.usecase.ChannelUseCase
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.cmoney.kolfanci.model.usecase.OrderUseCase
import com.cmoney.kolfanci.ui.screens.group.setting.group.channel.viewmodel.ChannelSettingViewModel
import com.google.gson.Gson
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class MemberUiState(
    val isLoading: Boolean = false,
    val group: Group? = null,
    val channelRole: List<FanciRole>? = null,        //目前頻道顯示角色List
    val groupRoleList: List<AddChannelRoleModel> = emptyList(),
    val confirmRoleList: String = "",
    val showAddSuccessTip: Boolean = false          //show 新增成功 toast
)

data class AddChannelRoleModel(val role: FanciRole, val isChecked: Boolean = false)

class RoleViewModel(
    private val channelUseCase: ChannelUseCase,
    private val groupUseCase: GroupUseCase,
    private val orderUseCase: OrderUseCase
) : ViewModel() {
    private val TAG = ChannelSettingViewModel::class.java.simpleName

    var uiState by mutableStateOf(MemberUiState())
        private set

    var loadingState by mutableStateOf(LoadingState(isLoading = true))
        private set

    //群組角色清單
    val orgGroupRoleList = mutableListOf<AddChannelRoleModel>()

    //新增的角色
    private val addGroupRoleQueue = mutableListOf<FanciRole>()

    private fun dismissLoading() {
        loadingState = loadingState.copy(
            isLoading = false
        )
    }

    /**
     * 取得 社團下角色清單
     * @param groupId 社團 id
     * @param exclusiveRole 排除的 Role
     */
    fun getGroupRoleList(groupId: String, exclusiveRole: Array<FanciRole>) {
        KLog.i(TAG, "getGroupRole:$groupId")
        viewModelScope.launch {
            groupUseCase.fetchGroupRole(
                groupId = groupId
            ).fold({
                dismissLoading()
                val filterRole = it.filter {
                    !exclusiveRole.contains(it)
                }.map {
                    AddChannelRoleModel(role = it)
                }

                orgGroupRoleList.clear()
                orgGroupRoleList.addAll(filterRole)

                uiState = uiState.copy(
                    groupRoleList = filterRole
                )
            }, {
                dismissLoading()
                KLog.e(TAG, it)
            })
        }
    }


    /**
     * 點擊角色, 勾選/取消勾選
     */
    fun onRoleClick(addChannelRoleModel: AddChannelRoleModel) {
        val role = addChannelRoleModel.copy(
            isChecked = !addChannelRoleModel.isChecked
        )

        val newList = uiState.groupRoleList.map {
            if (it.role.id == addChannelRoleModel.role.id) {
                role
            } else {
                it
            }
        }

        uiState = uiState.copy(
            groupRoleList = newList
        )
    }

    /**
     * 確定新增 角色
     */
    fun onAddRoleConfirm() {
        KLog.i(TAG, "onAddRoleConfirm.")
        val confirmRole = uiState.groupRoleList.filter {
            it.isChecked
        }
        val finalRoleList = confirmRole.map {
            it.role
        }

        addGroupRoleQueue.addAll(finalRoleList)
        val distinctList = addGroupRoleQueue.distinct()
        addGroupRoleQueue.clear()
        addGroupRoleQueue.addAll(distinctList)

//        val orgRoleList = uiState.groupRoleList.orEmpty()
//        val filterRole = orgRoleList.filter {
//            addGroupRoleQueue.find { exclude ->
//                exclude.id == it.role.id
//            } == null
//        }
//        uiState = uiState.copy(
//            groupRoleList = filterRole,
//            showAddSuccessTip = true
//        )

        uiState = uiState.copy(
            confirmRoleList = Gson().toJson(addGroupRoleQueue),
            showAddSuccessTip = true
        )
    }

    /**
     * 取消 toast
     */
    fun dismissAddSuccessTip() {
        uiState = uiState.copy(
            showAddSuccessTip = false
        )
    }

    /**
     * 取得 已選擇的角色清單
     */
    fun fetchSelectedRole(): String {
        val gson = Gson()
        return gson.toJson(addGroupRoleQueue)
    }
}