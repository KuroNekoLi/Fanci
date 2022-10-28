package com.cmoney.fanci.ui.screens.shared.bottomSheet

import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.ui.screens.shared.InteractDialogScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 互動 彈窗總類
 */
sealed class MessageInteract(message: ChatMessageModel) {
    //回覆
    data class Reply(val message: ChatMessageModel) : MessageInteract(message)

    //收回
    data class Recycle(val message: ChatMessageModel) : MessageInteract(message)

    //複製
    data class Copy(val message: ChatMessageModel) : MessageInteract(message)

    //置頂
    data class Announcement(val message: ChatMessageModel) : MessageInteract(message)

    //隱藏用戶
    data class HideUser(val message: ChatMessageModel) : MessageInteract(message)

    //檢舉用戶
    data class Report(val message: ChatMessageModel) : MessageInteract(message)

    //刪除
    data class Delete(val message: ChatMessageModel) : MessageInteract(message)
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InteractBottomSheet(
    parent: ViewGroup,
    composeView: ComposeView,
    message: ChatMessageModel,
    onInteractClick: (MessageInteract) -> Unit
) {
    val TAG = parent::class.java.simpleName
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState =
        rememberModalBottomSheetState(
            ModalBottomSheetValue.Hidden,
            confirmStateChange = {
                it != ModalBottomSheetValue.HalfExpanded
            }
        )
    var isSheetOpened by remember { mutableStateOf(false) }

    ModalBottomSheetLayout(
        sheetBackgroundColor = Color.Transparent,
        sheetState = modalBottomSheetState,
        sheetContent = {
            InteractDialogScreen(coroutineScope, modalBottomSheetState, message, onInteractClick)
        }
    ) {}


    BackHandler {
        animateHideBottomSheet(coroutineScope, modalBottomSheetState)
    }

    // Take action based on hidden state
    LaunchedEffect(modalBottomSheetState.currentValue) {
        when (modalBottomSheetState.currentValue) {
            ModalBottomSheetValue.Hidden -> {
                when {
                    isSheetOpened -> parent.removeView(composeView)
                    else -> {
                        isSheetOpened = true
                        modalBottomSheetState.show()
                    }
                }
            }
            else -> {
                Log.i(TAG, "Bottom sheet ${modalBottomSheetState.currentValue} state")
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
private fun animateHideBottomSheet(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState
) {
    coroutineScope.launch {
        modalBottomSheetState.hide() // will trigger the LaunchedEffect
    }
}