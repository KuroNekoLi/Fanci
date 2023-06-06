package com.cmoney.kolfanci.ui.screens.group.setting.vip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.model.usecase.VipManagerUseCase
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel
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
}