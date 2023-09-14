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
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.cmoney.kolfanci.model.usecase.NotificationUseCase
import com.cmoney.xlogin.XLoginHelper
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
 */
data class FollowUiState(
    val spaceHeight: Int = 190.px,
    val imageOffset: Int = 0,
    val visibleAvatar: Boolean = false,
    val lazyColumnScrollEnabled: Boolean = false,
    val showLoginDialog: Boolean = false,
    val navigateToCreateGroup: Boolean = false,
    val navigateToApproveGroup: Group? = null,
    val needNotifyAllowNotificationPermission: Boolean = false
)

class FollowViewModel(
    private val groupUseCase: GroupUseCase,
    private val notificationUseCase: NotificationUseCase
) : ViewModel() {
    private val TAG = FollowViewModel::class.java.simpleName

    //點擊加入群組彈窗
    private val _openGroupDialog: MutableStateFlow<Group?> = MutableStateFlow(null)
    val openGroupDialog = _openGroupDialog.asStateFlow()

    //刷新 我目前的社團清單
    private val _refreshMyGroup: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val refreshMyGroup = _refreshMyGroup.asStateFlow()

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

    /**
     * 確認是否通知使用者允許通知權限
     */
    fun checkNeedNotifyAllowNotificationPermission() {
        viewModelScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasNotifyAllowNotificationPermission = notificationUseCase.hasNotifyAllowNotificationPermission()
                    .getOrNull() ?: false
                uiState = uiState.copy(
                    needNotifyAllowNotificationPermission = !hasNotifyAllowNotificationPermission
                )
            }
        }
    }

    /**
     * 已通知使用者允許推播通知權限
     */
    fun alreadyNotifyAllowNotificationPermission() {
        viewModelScope.launch {
            notificationUseCase.alreadyNotifyAllowNotificationPermission()
        }
    }

}