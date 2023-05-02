package com.cmoney.kolfanci.extension

import android.app.Activity
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.kolfanci.ui.main.MainActivity
import com.cmoney.kolfanci.ui.screens.post.dialog.PostInteract
import com.cmoney.kolfanci.ui.screens.shared.bottomSheet.BottomSheetWrapper
import com.cmoney.kolfanci.ui.screens.shared.bottomSheet.ColorPickerBottomSheet
import com.cmoney.kolfanci.ui.screens.shared.bottomSheet.InteractBottomSheet
import com.cmoney.kolfanci.ui.screens.shared.bottomSheet.MessageInteract
import com.cmoney.kolfanci.ui.screens.shared.bottomSheet.PostMoreActionBottomSheet
import com.cmoney.kolfanci.ui.theme.FanciTheme

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
                    val theme by globalViewModel.theme.collectAsState()
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
    selectedColor: com.cmoney.fanciapi.fanci.model.Color,
    onColorPicker: (com.cmoney.fanciapi.fanci.model.Color) -> Unit
) {
    val viewGroup = this.findViewById(android.R.id.content) as ViewGroup
    if (this is MainActivity) {
        viewGroup.addView(
            ComposeView(viewGroup.context).apply {
                setContent {
                    val theme by globalViewModel.theme.collectAsState()
                    FanciTheme(fanciColor = theme) {
                        ColorPickerBottomSheet(viewGroup, this, selectedColor, onColorPicker)
                    }
                }
            }
        )
    }
}

/**
 * 貼文 更多動作
 */
fun Activity.showPostMoreActionDialogBottomSheet(
    postMessage: BulletinboardMessage,
    onInteractClick: (PostInteract) -> Unit
) {
    val viewGroup = this.findViewById(android.R.id.content) as ViewGroup
    if (this is MainActivity) {
        viewGroup.addView(
            ComposeView(viewGroup.context).apply {
                setContent {
                    val theme by globalViewModel.theme.collectAsState()
                    FanciTheme(fanciColor = theme) {
                        PostMoreActionBottomSheet(
                            parent = viewGroup,
                            composeView = this,
                            postMessage = postMessage,
                            onInteractClick = onInteractClick
                        )
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

@Composable
fun ComponentActivity.LifecycleEventListener(event: (Lifecycle.Event) -> Unit) {
    val eventHandler by rememberUpdatedState(newValue = event)
    val lifecycle = this@LifecycleEventListener.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            eventHandler(event)
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}