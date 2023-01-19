package com.cmoney.fanci.ui.screens.group.setting.report.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.extension.EmptyBodyException
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanciapi.fanci.model.ReportInformation
import com.cmoney.fanciapi.fanci.model.ReportProcessStatus
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class UiState(
    val reportList: List<ReportInformation> = emptyList()
)

class GroupReportViewModel(
    val groupUseCase: GroupUseCase,
    val reportList: List<ReportInformation>
) : ViewModel() {

    private val TAG = GroupReportViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    init {
        uiState = uiState.copy(
            reportList = reportList
        )
    }

    /**
     * 忽略 檢舉
     *
     * @param reportInformation 檢舉 model
     */
    fun ignoreReport(reportInformation: ReportInformation) {
        KLog.i(TAG, "ignoreReport:$reportInformation")
        viewModelScope.launch {
            groupUseCase.handlerReport(
                channelId = reportInformation.channel?.id.orEmpty(),
                reportId = reportInformation.id.orEmpty(),
                reportProcessStatus = ReportProcessStatus.ignored
            ).fold({
            }, {
                if (it is EmptyBodyException) {
                    val newList = uiState.reportList.filter {
                        it != reportInformation
                    }
                    uiState = uiState.copy(
                        reportList = newList
                    )
                } else {
                    KLog.e(TAG, it)
                }
            })
        }
    }
}