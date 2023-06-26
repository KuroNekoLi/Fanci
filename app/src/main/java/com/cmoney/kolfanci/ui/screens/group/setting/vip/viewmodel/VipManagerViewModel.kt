package com.cmoney.kolfanci.ui.screens.group.setting.vip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.kolfanci.extension.toVipPlanModel
import com.cmoney.kolfanci.model.usecase.VipManagerUseCase
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanPermissionModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanPermissionOptionModel
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VipManagerViewModel(
    private val group: Group,
    private val vipManagerUseCase: VipManagerUseCase
) : ViewModel() {
    private val TAG = VipManagerViewModel::class.java.simpleName

    /**
     * 目前vip plan
     */
    private val _vipPlanModel = MutableStateFlow<VipPlanModel?>(null)
    val vipPlanModel = _vipPlanModel.asStateFlow()

    /**
     * vip 銷售方案
     */
    private val _planSourceList = MutableStateFlow<List<String>>(emptyList())
    val planSourceList = _planSourceList.asStateFlow()

    /**
     * vip 方案下的成員
     */
    private val _vipMembers = MutableStateFlow<List<GroupMember>>(emptyList())
    val vipMembers = _vipMembers.asStateFlow()

    //Vip 方案清單
    private val _vipPlanList = MutableStateFlow<List<VipPlanModel>>(emptyList())
    val vipPlanList = _vipPlanList.asStateFlow()

    /**
     * 管理Vip方案 tab 選項
     */
    enum class VipManageTabKind {
        INFO,
        PERMISSION,
        MEMBER
    }

    //管理Vip 方案, 目前選的 tab position
    private val _manageTabPosition = MutableStateFlow(VipManageTabKind.INFO)
    val manageTabPosition = _manageTabPosition.asStateFlow()

    /**
     * 各頻道的權限狀態
     */
    private val _permissionModels = MutableStateFlow<List<VipPlanPermissionModel>?>(null)
    val permissionModels = _permissionModels.asStateFlow()

    /**
     * 可以選擇的權限選項
     */
    private val _permissionOptionModels =
        MutableStateFlow<List<VipPlanPermissionOptionModel>?>(null)
    val permissionOptionModels = _permissionOptionModels.asStateFlow()

    /**
     * 已經購買的方案清單
     */
    private val _alreadyPurchasePlan = MutableStateFlow<List<VipPlanModel>>(emptyList())
    val alreadyPurchasePlan = _alreadyPurchasePlan.asStateFlow()

    /**
     *  取得該社團目前有的 Vip 方案清單
     */
    fun fetchVipPlan() {
        viewModelScope.launch {
            vipManagerUseCase.getVipPlan(group).fold({
                _vipPlanList.value = it.map { role ->
                    role.toVipPlanModel()
                }
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 點擊 管理 vip 方案 tab
     */
    fun onManageVipTabClick(tabPosition: Int) {
        KLog.i(TAG, "onManageVipTabClick:$tabPosition")
        _manageTabPosition.value = VipManageTabKind.values()[tabPosition]
        when (tabPosition) {
            //資訊 tab
            0 -> {
                _vipPlanModel.value?.let {
                    fetchVipSales(it.id)
                }

            }
            //權限 tab
            1 -> {
            }
            //成員 tab
            else -> {
                _vipPlanModel.value?.let {
                    fetchVipMembers(it.id)
                }
            }
        }
    }

    /**
     * 設定 vip 名稱
     */
    fun setVipName(vipName: String) {
        KLog.i(TAG, "setVipName:$vipName")
        _vipPlanModel.value = _vipPlanModel.value?.copy(
            name = vipName
        )

        _vipPlanModel.value?.let { planModel ->
            viewModelScope.launch {
                vipManagerUseCase.changeVipRoleName(
                    groupId = group.id.orEmpty(),
                    roleId = planModel.id,
                    name = vipName
                ).onSuccess {
                    KLog.i(TAG, "setVipName onSuccess")
                }.onFailure {
                    KLog.e(TAG, it)
                }
            }
        }
    }

    /**
     * 取得VIP管理的權限資料
     */
    fun fetchPermissions(vipPlanModel: VipPlanModel) {
        KLog.i(TAG, "fetchPermissions.")
        viewModelScope.launch {
            vipManagerUseCase.getPermissions(group = group, vipPlanModel = vipPlanModel)
                .onSuccess { data ->
                    _permissionModels.value = data
                }
                .onFailure { error ->
                    KLog.i(TAG, error)
                }
        }
    }

    /**
     * 設定此頻道在此方案下權限
     */
    fun setPermission(permissionModel: VipPlanPermissionModel) {
        KLog.i(TAG, "setPermission:$permissionModel")

        viewModelScope.launch {
            vipManagerUseCase.setChannelVipRolePermission(
                channelId = permissionModel.id,
                vipRoleId = _vipPlanModel.value?.id.orEmpty(),
                authType = permissionModel.authType
            ).onSuccess {
                KLog.i(TAG, "setPermission success.")
            }.onFailure {
                KLog.e(TAG, it)
            }

            _permissionModels.value = _permissionModels.value.orEmpty()
                .map { oldPermission ->
                    if (oldPermission.id == permissionModel.id) {
                        permissionModel
                    } else {
                        oldPermission
                    }
                }
        }
    }

    /**
     * 取得頻道設定的權限選擇
     */
    fun fetchPermissionOptions(vipPlanModel: VipPlanModel) {
        viewModelScope.launch {
            vipManagerUseCase.getPermissionOptions(vipPlanModel = vipPlanModel)
                .onSuccess { data ->
                    _permissionOptionModels.value = data
                }
                .onFailure { error ->
                    KLog.e(TAG, error)
                }
        }
    }

    /**
     * 取得該會員已購買的清單
     */
    fun fetchAlreadyPurchasePlan(groupMember: GroupMember) {
        viewModelScope.launch {
            vipManagerUseCase.getAlreadyPurchasePlan(
                groupId = group.id.orEmpty(),
                groupMember = groupMember
            ).fold({
                _alreadyPurchasePlan.value = it.map { purchaseRole ->
                    val plans = purchaseRole.vipSalePlans.orEmpty()
                    plans.map { plan ->
                        VipPlanModel(
                            id = plan.vipSaleId.toString(),
                            name = purchaseRole.roleName.orEmpty(),
                            memberCount = 0,
                            description = plan.vipSaleName.orEmpty()
                        )
                    }
                }.flatten()
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 初始化,選擇的方案
     * 設定目前選擇的 vip 名稱
     * 取得 資訊tab 訂閱方案
     */
    fun initVipPlanModel(vipPlanModel: VipPlanModel) {
        KLog.i(TAG, "initVipPlanModel:$vipPlanModel")
        _vipPlanModel.value = vipPlanModel
        fetchVipSales(roleId = vipPlanModel.id)
    }

    /**
     * 取得 該vip 的銷售方案, 會加工 方案名稱前面多個 ・
     *
     * @param roleId vip 方案id
     */
    private fun fetchVipSales(roleId: String) {
        KLog.i(TAG, "fetchVipSales:$roleId")
        viewModelScope.launch {
            vipManagerUseCase.getVipSales(roleId = roleId).fold({
                _planSourceList.value = it.map { sale ->
                    "・%s".format(sale.vipSaleName.orEmpty())
                }
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 取得該方案下的成員
     *
     * @param roleId vip 方案id
     */
    private fun fetchVipMembers(roleId: String) {
        KLog.i(TAG, "fetchVipMembers:$roleId")
        viewModelScope.launch {
            vipManagerUseCase.getVipPlanMember(
                groupId = group.id.orEmpty(),
                roleId = roleId
            ).onSuccess {
                _vipMembers.value = it
            }.onFailure {
                KLog.e(TAG, it)
            }
        }
    }
}