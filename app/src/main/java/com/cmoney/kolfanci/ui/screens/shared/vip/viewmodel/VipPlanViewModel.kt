package com.cmoney.kolfanci.ui.screens.shared.vip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.extension.toVipPlanModel
import com.cmoney.kolfanci.model.usecase.VipManagerUseCase
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VipPlanViewModel(
    private val vipManagerUseCase: VipManagerUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()

    private val _vipPlanModels = MutableStateFlow<List<VipPlanModel>?>(null)
    val vipPlanModels = _vipPlanModels.asStateFlow()

    /**
     * 取得可以設定的權限
     *
     * @param models 已選擇的VIP方案
     */
    fun fetchVipPlan(models: Array<VipPlanModel>, group: Group) {
        viewModelScope.launch {
            _isLoading.value = true
            vipManagerUseCase.getVipPlan(group = group)
                .onSuccess { data ->
                    val allVipPlanModel = data.map {
                        it.toVipPlanModel()
                    }
                    val modelSet = models.toSet()
                    _vipPlanModels.value = allVipPlanModel.filterNot { model ->
                        modelSet.contains(model)
                    }
                }
                .onFailure { error ->
                    KLog.d(TAG, error)
                }
            _isLoading.value = false
        }
    }

    companion object {
        private const val TAG = "VipPlanModel"
    }
}