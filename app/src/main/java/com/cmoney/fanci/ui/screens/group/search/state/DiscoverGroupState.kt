package com.cmoney.fanci.ui.screens.group.search.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.model.GroupModel
import com.cmoney.fanci.model.viewmodel.GroupViewModelFactory
import com.cmoney.fanci.ui.screens.group.viewmodel.GroupViewModel

class DiscoverGroupState(
    val navController: NavHostController,
    val openGroupDialog: MutableState<Boolean>,
    val viewModel: GroupViewModel
) {

    /**
     * 點擊 社團
     */
    fun openGroupItemDialog(groupModel: GroupModel) {
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
    viewModel: GroupViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = GroupViewModelFactory()
    )
) = remember {
    DiscoverGroupState(navController, openGroupDialog, viewModel)
}