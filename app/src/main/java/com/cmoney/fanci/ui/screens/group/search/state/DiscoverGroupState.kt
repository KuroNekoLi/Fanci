package com.cmoney.fanci.ui.screens.group.search.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.ui.screens.group.viewmodel.GroupViewModel
import com.cmoney.fanciapi.fanci.model.Group
import org.koin.androidx.compose.koinViewModel

class DiscoverGroupState(
    val navController: NavHostController,
    val openGroupDialog: MutableState<Boolean>,
    val viewModel: GroupViewModel
) {

    /**
     * 點擊 社團
     */
    fun openGroupItemDialog(groupModel: Group) {
        openGroupDialog.value = true
    }

    /**
     * 關閉 社團 彈窗
     */
    fun closeGroupItemDialog() {
        openGroupDialog.value = false
    }

}

@Composable
fun rememberDiscoverGroupState(
    navController: NavHostController = rememberNavController(),
    openGroupDialog: MutableState<Boolean> = mutableStateOf(false),
    viewModel: GroupViewModel = koinViewModel()
) = remember {
    DiscoverGroupState(navController, openGroupDialog, viewModel)
}