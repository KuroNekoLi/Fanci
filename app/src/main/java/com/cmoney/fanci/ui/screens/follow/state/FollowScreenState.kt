package com.cmoney.fanci.ui.screens.follow.state

import android.content.res.Configuration
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.ui.screens.follow.viewmodel.FollowViewModel
import com.cmoney.fanciapi.fanci.model.Group
import com.socks.library.KLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class FollowScreenState(
    val navController: NavHostController,
    val viewModel: FollowViewModel,
    val configuration: Configuration,
    val localDensity: Density,
    val scaffoldState: ScaffoldState,
    val coroutineScope: CoroutineScope,
    var openGroupDialog: MutableState<Group?>
) {
    private val TAG = FollowScreenState::class.java.simpleName

    // 偏移折叠工具栏上移高度
    val imageOffsetHeightPx = mutableStateOf(0f)

    //Space Height
    val maxSpaceUpPx = with(localDensity) { 190.dp.roundToPx().toFloat() }
    val spaceOffsetHeightPx = mutableStateOf(maxSpaceUpPx)
    var visibleAvatar = false

    var lazyColumnScrollEnabled = false

    val nestedScrollConnection = object : NestedScrollConnection {
        val screenWidth = configuration.screenWidthDp.dp

        // ToolBar 最大向上位移量
        val maxUpPx = with(localDensity) { screenWidth.roundToPx().toFloat() + 10 }

        // ToolBar 最小向上位移量
        val minUpPx = 0f

        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            val delta = available.y

            //位移偏差值
            val offsetVariable = 2.2f
            val newOffset = imageOffsetHeightPx.value + delta * offsetVariable

            imageOffsetHeightPx.value = newOffset.coerceIn(-maxUpPx, -minUpPx)

            val spaceOffsetVariable = 1f
            val newSpaceOffset = spaceOffsetHeightPx.value + delta * spaceOffsetVariable

            spaceOffsetHeightPx.value = newSpaceOffset.coerceIn(0f, maxSpaceUpPx)

            //是否顯示 大頭貼
            visibleAvatar = spaceOffsetHeightPx.value <= 10f
            return Offset.Zero
        }
    }

    /**
     * 打開 側邊menu
     */
    fun openDrawer() {
        coroutineScope.launch {
            scaffoldState.drawerState.open()
        }
    }

    /**
     * 關閉 側邊menu
     */
    fun closeDrawer() {
        coroutineScope.launch {
            scaffoldState.drawerState.close()
        }
    }

    /**
     * 點擊 社團
     */
    fun openGroupItemDialog(groupModel: Group) {
        openGroupDialog.value = groupModel
    }

    /**
     * 關閉 社團 彈窗
     */
    fun closeGroupItemDialog() {
        openGroupDialog.value = null
    }

    /**
     * 滑動位移量, 來調整 top space
     *
     * @param offset 位移量
     */
    fun scrollOffset(offset: Float) {
        val newSpaceOffset = spaceOffsetHeightPx.value + offset
        spaceOffsetHeightPx.value = newSpaceOffset.coerceIn(0f, maxSpaceUpPx)

        val screenWidth = configuration.screenWidthDp.dp
        // ToolBar 最大向上位移量
        val maxUpPx = with(localDensity) { screenWidth.roundToPx().toFloat() + 10 }
        // ToolBar 最小向上位移量
        val minUpPx = 0f
        //位移偏差值
        val offsetVariable = 1.2f
        val newOffset = imageOffsetHeightPx.value + offset * offsetVariable
        imageOffsetHeightPx.value = newOffset.coerceIn(-maxUpPx, -minUpPx)

        //是否顯示 大頭貼
        visibleAvatar = spaceOffsetHeightPx.value <= 10f
        lazyColumnScrollEnabled = visibleAvatar
    }

    /**
     * 滑動到最上方 觸發
     */
    fun lazyColumnAtTop() {
        lazyColumnScrollEnabled = false
        visibleAvatar = false
    }
}

@Composable
fun rememberFollowScreenState(
    navController: NavHostController = rememberNavController(),
    viewModel: FollowViewModel = koinViewModel(),
    configuration: Configuration = LocalConfiguration.current,
    localDensity: Density = LocalDensity.current,
    scaffoldState: ScaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed)),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    openGroupDialog: MutableState<Group?> = remember {
        mutableStateOf(null)
    }
) = remember {
    FollowScreenState(
        navController,
        viewModel,
        configuration,
        localDensity,
        scaffoldState,
        coroutineScope,
        openGroupDialog
    )
}