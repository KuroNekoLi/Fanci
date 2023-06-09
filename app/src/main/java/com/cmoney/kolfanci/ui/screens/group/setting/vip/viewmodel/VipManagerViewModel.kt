package com.cmoney.kolfanci.ui.screens.group.setting.vip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.kolfanci.model.usecase.VipManagerUseCase
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanInfoModel
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

    //管理Vip 方案, 方案詳細資訊
    private val _planInfo = MutableStateFlow<VipPlanInfoModel?>(null)
    val planInfo = _planInfo.asStateFlow()

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
                _vipPlanList.value = it
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
    }

    /**
     * 抓取該 vip 方案, 更詳細資訊
     */
    fun fetchVipPlanInfo(vipPlanModel: VipPlanModel) {
        KLog.i(TAG, "fetchVipPlanInfo:$vipPlanModel")
        viewModelScope.launch {
            vipManagerUseCase.getVipPlanInfo(vipPlanModel).fold({
                _planInfo.value = it
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 設定 vip 名稱
     */
    fun setVipName(vipName: String) {
        KLog.i(TAG, "setVipName:$vipName")

        _planInfo.value = _planInfo.value?.copy(
            name = vipName
        )
        //TODO call api change name
    }

    /**
     * 取得VIP管理的權限資料
     */
    fun fetchPermissions(vipPlanModel: VipPlanModel) {
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
        viewModelScope.launch {
            // TODO call api change permission
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
            vipManagerUseCase.getAlreadyPurchasePlan(groupMember = groupMember).fold({
                _alreadyPurchasePlan.value = it
            }, {
                KLog.e(TAG, it)
            })
        }
    }
}