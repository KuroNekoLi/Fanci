package com.cmoney.fanci.ui.screens.group.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class DiscoverGroupState(
    val navController: NavHostController,
    val openGroupDialog: MutableState<Boolean>
) {

    /**
     * 點擊 社團
     */
    fun openGroupItemDialog() {
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
    openGroupDialog: MutableState<Boolean> = mutableStateOf(false)
) = remember {
    DiscoverGroupState(navController, openGroupDialog)
}