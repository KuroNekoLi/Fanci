package com.cmoney.kolfanci.ui.screens.follow.state

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.kolfanci.ui.screens.follow.viewmodel.FollowViewModel
import com.cmoney.fanciapi.fanci.model.Group
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class FollowScreenState(
    val navController: NavHostController,
    val viewModel: FollowViewModel,
    val scaffoldState: ScaffoldState,
    private val coroutineScope: CoroutineScope,
    val scrollState: LazyListState,
    var openGroupDialog: MutableState<Group?>,
) {
    private val TAG = FollowScreenState::class.java.simpleName

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
}

@Composable
fun rememberFollowScreenState(
    navController: NavHostController = rememberNavController(),
    viewModel: FollowViewModel = koinViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed)),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    scrollState: LazyListState = rememberLazyListState(),
    openGroupDialog: MutableState<Group?> = remember {
        mutableStateOf(null)
    },
) = remember {
    FollowScreenState(
        navController,
        viewModel,
        scaffoldState,
        coroutineScope,
        scrollState,
        openGroupDialog
    )
}