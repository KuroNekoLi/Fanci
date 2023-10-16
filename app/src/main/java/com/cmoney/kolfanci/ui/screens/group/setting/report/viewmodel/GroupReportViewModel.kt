package com.cmoney.kolfanci.ui.screens.group.setting.report.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.BanPeriodOption
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.ReportInformation
import com.cmoney.fanciapi.fanci.model.ReportProcessStatus
import com.cmoney.kolfanci.model.usecase.BanUseCase
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class UiState(
    val reportList: List<ReportInformation> = emptyList(),
    val showReportDialog: ReportInformation? = null,  //懲處 dialog
    val showSilenceDialog: ReportInformation? = null, //禁言 dialog
    val kickDialog: ReportInformation? = null,         //踢除社團 dialog
    val loading: Boolean = true
)

class GroupReportViewModel(
    val groupUseCase: GroupUseCase,
    val reportList: List<ReportInformation>,
    val group: Group,
    private val banUseCase: BanUseCase
) : ViewModel() {

    private val TAG = GroupReportViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    init {
        uiState = uiState.copy(
            reportList = reportList
        )
    }

    private fun showLoading() {
        uiState = uiState.copy(
            loading = true
        )
    }

    private fun dismissLoading() {
        uiState = uiState.copy(
            loading = false
        )
    }

    /**
     * dismiss Kick dialog
     */
    fun dismissKickDialog() {
        uiState = uiState.copy(
            kickDialog = null
        )
    }

    /**
     * show Kick dialog
     */
    fun showKickDialog(reportInformation: ReportInformation) {
        uiState = uiState.copy(
            kickDialog = reportInformation
        )
    }

    /**
     * dismiss 禁言 dialog
     */
    fun dismissSilenceDialog() {
        uiState = uiState.copy(
            showSilenceDialog = null
        )
    }

    /**
     * show 禁言 dialog
     */
    fun showSilenceDialog(reportInformation: ReportInformation) {
        uiState = uiState.copy(
            showSilenceDialog = reportInformation
        )
    }

    /**
     * dismiss 懲處 dialog
     */
    fun dismissReportDialog() {
        uiState = uiState.copy(
            showReportDialog = null
        )
    }

    /**
     * show 懲處 dialog
     */
    fun showReportDialog(reportInformation: ReportInformation) {
        uiState = uiState.copy(
            showReportDialog = reportInformation
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
                val newList = uiState.reportList.filter {
                    it != reportInformation
                }
                uiState = uiState.copy(
                    reportList = newList
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 懲處完 要另外跟 server 説
     */
    private fun punished(reportInformation: ReportInformation) {
        KLog.i(TAG, "punished:$reportInformation")
        viewModelScope.launch {
            groupUseCase.handlerReport(
                channelId = reportInformation.channel?.id.orEmpty(),
                reportId = reportInformation.id.orEmpty(),
                reportProcessStatus = ReportProcessStatus.punished
            ).fold({
                val newList = uiState.reportList.filter {
                    it != reportInformation
                }
                uiState = uiState.copy(
                    reportList = newList
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }


    /**
     *  禁言
     *
     *  @param reportInformation 檢舉model
     *  @param banPeriodOption 禁言 model
     */
    fun silenceUser(reportInformation: ReportInformation, banPeriodOption: BanPeriodOption) {
        KLog.i(TAG, "silenceUser:$reportInformation")
        viewModelScope.launch {
            banUseCase.banUser(
                groupId = group.id.orEmpty(),
                userId = reportInformation.reportee?.id.orEmpty(),
                banPeriodOption = banPeriodOption
            ).fold({
                dismissSilenceDialog()
                punished(reportInformation)
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 剔除人員
     */
    fun kickOutMember(reportInformation: ReportInformation) {
        KLog.i(TAG, "kickOutMember:$reportInformation")
        viewModelScope.launch {
            groupUseCase.kickOutMember(
                groupId = group.id.orEmpty(),
                userId = reportInformation.reportee?.id.orEmpty()
            ).fold({
                dismissKickDialog()
                punished(reportInformation)
            }, {
                KLog.e(TAG, it)
            })
        }
    }


}