package com.cmoney.fanci.ui.screens.follow.state

import android.content.res.Configuration
import androidx.compose.material.DrawerValue
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.ui.screens.follow.FollowViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FollowScreenState(
    val navController: NavHostController,
    val viewModel: FollowViewModel,
    val configuration: Configuration,
    val localDensity: Density,
    val scaffoldState: ScaffoldState,
    val coroutineScope: CoroutineScope
) {

    // 偏移折叠工具栏上移高度
    val imageOffsetHeightPx = mutableStateOf(0f)

    //Space Height
    val maxSpaceUpPx = with(localDensity) { 190.dp.roundToPx().toFloat() }
    val spaceOffsetHeightPx = mutableStateOf(maxSpaceUpPx)
    var visibleAvatar = false

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

            val newSpaceOffset = spaceOffsetHeightPx.value + delta
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
}

@Composable
fun rememberFollowScreenState(
    navController: NavHostController = rememberNavController(),
    viewModel: FollowViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    configuration: Configuration = LocalConfiguration.current,
    localDensity: Density = androidx.compose.ui.platform.LocalDensity.current,
    scaffoldState: ScaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed)),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember {
    FollowScreenState(navController, viewModel, configuration, localDensity, scaffoldState, coroutineScope)
}