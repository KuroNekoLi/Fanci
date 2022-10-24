package com.cmoney.fanci.ui.screens.shared.bottomSheet

import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import com.cmoney.fanci.ui.screens.shared.InteractDialogScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetWrapper(
//    wrapWithBottomSheetUI: Boolean,
    parent: ViewGroup,
    composeView: ComposeView,
//    content: @Composable (() -> Unit) -> Unit
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
            // TODO:
            InteractDialogScreen(coroutineScope, modalBottomSheetState)
//            when {
//                wrapWithBottomSheetUI -> {
//                    BottomSheetUIWrapper(coroutineScope, modalBottomSheetState) { content {
//                        animateHideBottomSheet(coroutineScope, modalBottomSheetState)
//                    } }
//                }
//                else -> content {
//                    animateHideBottomSheet(coroutineScope, modalBottomSheetState)
//                }
//            }
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