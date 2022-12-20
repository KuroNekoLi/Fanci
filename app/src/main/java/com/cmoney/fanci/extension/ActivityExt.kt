package com.cmoney.fanci.extension

import android.app.Activity
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.cmoney.fanci.MainActivity
import com.cmoney.fanci.ui.screens.shared.bottomSheet.BottomSheetWrapper
import com.cmoney.fanci.ui.screens.shared.bottomSheet.ColorPickerBottomSheet
import com.cmoney.fanci.ui.screens.shared.bottomSheet.InteractBottomSheet
import com.cmoney.fanci.ui.screens.shared.bottomSheet.MessageInteract
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanciapi.fanci.model.ChatMessage

/**
 * Show 聊天室 互動彈窗
 */
fun Activity.showInteractDialogBottomSheet(
    message: ChatMessage,
    onInteractClick: (MessageInteract) -> Unit
) {
    val viewGroup = this.findViewById(android.R.id.content) as ViewGroup
    if (this is MainActivity) {
        viewGroup.addView(
            ComposeView(viewGroup.context).apply {
                setContent {
                    val theme = globalViewModel.uiState.theme
                    FanciTheme(fanciColor = theme) {
                        InteractBottomSheet(viewGroup, this, message, onInteractClick)
                    }
                }
            }
        )
    }
}

/**
 * 顏色 選擇器
 * @param selectedColor 預設選擇顏色
 */
fun Activity.showColorPickerDialogBottomSheet(
    selectedColor: Color,
    onColorPicker: (Color) -> Unit
) {
    val viewGroup = this.findViewById(android.R.id.content) as ViewGroup
    if (this is MainActivity) {
        viewGroup.addView(
            ComposeView(viewGroup.context).apply {
                setContent {
                    val theme = globalViewModel.uiState.theme
                    FanciTheme(fanciColor = theme) {
                        ColorPickerBottomSheet(viewGroup, this, selectedColor, onColorPicker)
                    }
                }
            }
        )
    }
}


fun Activity.showAsBottomSheet(
    wrapWithBottomSheetUI: Boolean = false,
    content: @Composable (() -> Unit) -> Unit
) {
    val viewGroup = this.findViewById(android.R.id.content) as ViewGroup
    addContentToView(wrapWithBottomSheetUI, viewGroup, content)
}

fun Fragment.showAsBottomSheet(
    wrapWithBottomSheetUI: Boolean = false,
    content: @Composable (() -> Unit) -> Unit
) {
    val viewGroup = requireActivity().findViewById(android.R.id.content) as ViewGroup
    addContentToView(wrapWithBottomSheetUI, viewGroup, content)
}

private fun addContentToView(
    wrapWithBottomSheetUI: Boolean,
    viewGroup: ViewGroup,
    content: @Composable (() -> Unit) -> Unit
) {
    viewGroup.addView(
        ComposeView(viewGroup.context).apply {
            setContent {
                BottomSheetWrapper(wrapWithBottomSheetUI, viewGroup, this, content)
            }
        }
    )
}