package com.cmoney.kolfanci.ui.screens.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.PurchasedSale
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.usecase.VipManagerUseCase
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyScreenViewModel(
    private val vipManagerUseCase: VipManagerUseCase
) : ViewModel() {
    private val TAG = MyScreenViewModel::class.java.simpleName

    private val _userVipPlan = MutableStateFlow<List<VipPlanModel>>(emptyList())
    val userVipPlan = _userVipPlan.asStateFlow()

    /**
     * 取得 購買過的方案 清單
     */
    fun getUserVipPlan() {
        KLog.i(TAG, "getUserVipPlan")
        viewModelScope.launch {
            //TODO: 驗證
            vipManagerUseCase.getUserAllVipPlan(userId = Constant.MyInfo?.id.orEmpty()).fold({
                _userVipPlan.value = it.map { plan ->
                    plan.purchasedRoles.orEmpty().map { role ->
                        VipPlanModel(
                            id = plan.vipSaleId.toString(),
                            name = plan.vipSaleName.orEmpty(),
                            memberCount = 0,
                            description = role.nextPaymentAlert.orEmpty()
                        )
                    }
                }.flatten()
            }, {
                KLog.e(TAG, it)
            })
        }
    }

}