package com.cmoney.kolfanci.ui.screens.follow.viewmodel

import android.content.res.Configuration
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.extension.px
import com.cmoney.kolfanci.model.persistence.SettingsDataStore
import com.cmoney.kolfanci.model.usecase.GroupApplyUseCase
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.cmoney.kolfanci.model.usecase.NotificationUseCase
import com.cmoney.kolfanci.utils.Utils
import com.cmoney.xlogin.XLoginHelper
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Follow ui state
 *
 * @property spaceHeight 滑動時的間距
 * @property imageOffset 滑動時圖片Offset
 * @property visibleAvatar 是否要顯示大頭貼
 * @property lazyColumnScrollEnabled LazyColumn 是否可以滑動
 * @property showLoginDialog 呈現登入彈窗
 * @property navigateToCreateGroup 前往建立社團
 * @property navigateToApproveGroup 前往社團認證
 * @property needNotifyAllowNotificationPermission 是否提示使用者允許推播通知，false 表示未處理或是已通知過
 * @property isShowBubbleTip 是否出現 提示彈窗
 */
data class FollowUiState(
    val spaceHeight: Int = 190.px,
    val imageOffset: Int = 0,
    val visibleAvatar: Boolean = false,
    val lazyColumnScrollEnabled: Boolean = false,
    val showLoginDialog: Boolean = false,
    val navigateToCreateGroup: Boolean = false,
    val navigateToApproveGroup: Group? = null,
    val needNotifyAllowNotificationPermission: Boolean = false,
    val isShowBubbleTip: Boolean = false
)

class FollowViewModel(
    private val groupUseCase: GroupUseCase,
    private val notificationUseCase: NotificationUseCase,
    private val dataStore: SettingsDataStore,
    private val groupApplyUseCase: GroupApplyUseCase
) : ViewModel() {

    private val TAG = FollowViewModel::class.java.simpleName

    //點擊加入群組彈窗
    private val _openGroupDialog: MutableStateFlow<Group?> = MutableStateFlow(null)
    val openGroupDialog = _openGroupDialog.asStateFlow()

    //刷新 我目前的社團清單
    private val _refreshMyGroup: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val refreshMyGroup = _refreshMyGroup.asStateFlow()

    //我申請中的社團
    private val _allMyApplyGroup: MutableStateFlow<List<Group>> = MutableStateFlow(emptyList())
    val allMyApplyGroup = _allMyApplyGroup.asStateFlow()

    //是否出現 輸入邀請碼彈窗
    private val _isShowInviteCodeDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isShowInviteCodeDialog = _isShowInviteCodeDialog.asStateFlow()

    var uiState by mutableStateOf(FollowUiState())
        private set

    /**
     * 加入社團
     */
    fun joinGroup(group: Group) {
        KLog.i(TAG, "joinGroup:$group")
        if (XLoginHelper.isLogin) {
            viewModelScope.launch {
                if (group.isNeedApproval == true) {
                    uiState = uiState.copy(
                        navigateToApproveGroup = group
                    )
                } else {
                    groupUseCase.joinGroup(group).fold({
                        closeGroupItemDialog()
                        _refreshMyGroup.value = true
                    }, {
                        KLog.e(TAG, it)
                    })
                }
            }
        } else {
            showLoginDialog()
        }
    }


    /**
     * 滑動位移量, 來調整 top space，並判斷目前是否要開始捲動頻道列表
     *
     * @param offset 位移量
     */
    fun scrollOffset(offset: Float, localDensity: Density, configuration: Configuration) {
        val maxSpaceUpPx = 190.px
        val newSpaceOffset = uiState.spaceHeight + offset

        val screenWidth = configuration.screenWidthDp.px
        // ToolBar 最大向上位移量
        val maxUpPx = screenWidth + 10
        // ToolBar 最小向上位移量
        val minUpPx = 0f
        //位移偏差值
        val offsetVariable = 2f
        val newOffset = uiState.imageOffset + offset * offsetVariable

        //是否顯示 大頭貼
        val isVisibleAvatar = (uiState.spaceHeight <= 10f)

        uiState = uiState.copy(
            spaceHeight = newSpaceOffset.coerceIn(0f, maxSpaceUpPx.toFloat()).toInt(),
            imageOffset = newOffset.coerceIn(-maxUpPx.toFloat(), -minUpPx).toInt(),
            visibleAvatar = isVisibleAvatar,
            lazyColumnScrollEnabled = isVisibleAvatar
        )
    }

    /**
     * 滑動到最上方 觸發
     */
    fun lazyColumnAtTop() {
        uiState = uiState.copy(visibleAvatar = false)
    }

    /**
     * 點擊建立社團
     */
    fun onCreateGroupClick() {
        KLog.i(TAG, "onCreateGroupClick")
        uiState = if (XLoginHelper.isLogin) {
            uiState.copy(
                navigateToCreateGroup = true
            )
        } else {
            uiState.copy(
                showLoginDialog = true
            )
        }
    }

    /**
     * 顯示登入彈窗
     */
    fun showLoginDialog() {
        uiState = uiState.copy(
            showLoginDialog = true
        )
    }

    /**
     * 關閉登入彈窗
     */
    fun dismissLoginDialog() {
        uiState = uiState.copy(
            showLoginDialog = false
        )
    }

    fun navigateDone() {
        uiState = uiState.copy(
            navigateToCreateGroup = false,
            navigateToApproveGroup = null
        )
    }

    /**
     * 點擊加入彈窗
     */
    fun openGroupItemDialog(group: Group) {
        KLog.i(TAG, "openGroupItemDialog:$group")
        _openGroupDialog.value = group
    }

    /**
     * 關閉 社團 彈窗
     */
    fun closeGroupItemDialog() {
        KLog.i(TAG, "closeGroupItemDialog")
        _openGroupDialog.value = null
    }

    /**
     * 關閉刷新任務
     */
    fun refreshMyGroupDone() {
        KLog.i(TAG, "refreshMyGroupDone")
        _refreshMyGroup.value = false
    }

    fun disableBubbleTip() {
        KLog.i(TAG, "onMoreClick")
        viewModelScope.launch {
            dataStore.alreadyShowHomeBubble()
            uiState = uiState.copy(isShowBubbleTip = false)
        }
    }

    fun fetchSetting() {
        viewModelScope.launch {
            uiState = uiState.copy(isShowBubbleTip = dataStore.isShowBubble.first())
        }
    }

    /**
     * 確認是否通知使用者允許通知權限
     */
    fun checkNeedNotifyAllowNotificationPermission() {
        viewModelScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasNotifyAllowNotificationPermission =
                    notificationUseCase.hasNotifyAllowNotificationPermission()
                        .getOrNull() ?: false
                if (!hasNotifyAllowNotificationPermission) {
                    uiState = uiState.copy(
                        needNotifyAllowNotificationPermission = true
                    )
                }
            }
        }
    }

    /**
     * 已通知使用者允許推播通知權限
     */
    fun alreadyNotifyAllowNotificationPermission() {
        viewModelScope.launch {
            notificationUseCase.alreadyNotifyAllowNotificationPermission()
            uiState = uiState.copy(
                needNotifyAllowNotificationPermission = false
            )
        }
    }

    /**
     * 取得 我申請的社團-審核中
     */
    fun fetchAllMyGroupApplyUnConfirmed() {
        KLog.i(TAG, "fetchAllMyGroupApplyUnConfirmed.")
        viewModelScope.launch {
            groupApplyUseCase.fetchAllMyGroupApplyUnConfirmed()
                .onSuccess { groupList ->
                    _allMyApplyGroup.value = groupList
                }
                .onFailure { err ->
                    KLog.e(TAG, err)
                }
        }
    }

    /**
     * 點擊 輸入邀請碼
     */
    fun onInputInviteCodeClick() {
        KLog.i(TAG, "onInputInviteCodeClick")
        if (XLoginHelper.isLogin) {
            _isShowInviteCodeDialog.value = true
        } else {
            uiState = uiState.copy(
                showLoginDialog = true
            )
        }
    }

    fun closeInviteCodeDialog() {
        _isShowInviteCodeDialog.value = false
    }

    /**
     * 輸入 邀請碼
     */
    fun onInputInviteCode(inviteCode: String) {
        KLog.i(TAG, "onInputInviteCode:$inviteCode")
        viewModelScope.launch {
            val groupId = Utils.decryptInviteCode(
                input = inviteCode
            )
            KLog.i(TAG, "onInputInviteCode groupId:$groupId")

            groupId?.let {
                groupUseCase.getGroupById(groupId.toString())
                    .onSuccess { group ->
                        closeInviteCodeDialog()
                        openGroupItemDialog(group)
                    }
                    .onFailure {
                        KLog.e(TAG, it)
                    }
            }
        }
    }
}