package com.cmoney.kolfanci.ui.screens.my

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.PurchasedSale
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.copyToClipboard
import com.cmoney.kolfanci.extension.showToast
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.usecase.VipManagerUseCase
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyScreenViewModel(
    private val context: Application,
    private val vipManagerUseCase: VipManagerUseCase
) : AndroidViewModel(context) {
    private val TAG = MyScreenViewModel::class.java.simpleName

    private val _userVipPlan = MutableStateFlow<List<VipPlanModel>>(emptyList())
    val userVipPlan = _userVipPlan.asStateFlow()

    /**
     * 取得 購買過的方案 清單
     */
    fun getUserVipPlan() {
        KLog.i(TAG, "getUserVipPlan")
        viewModelScope.launch {
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

    /**
     * 點擊vip 方案彈窗, 複製信箱
     */
    fun onVipDialogClick() {
        KLog.i(TAG, "onVipDialogClick")
        val mail = context.getString(R.string.official_mail)
        context.copyToClipboard(mail)
        context.showToast(context.getString(R.string.copy_success))
    }

}