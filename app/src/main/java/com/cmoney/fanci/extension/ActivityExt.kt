package com.cmoney.fanci.extension

import android.app.Activity
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.cmoney.fanci.ui.screens.shared.bottomSheet.BottomSheetWrapper

fun Activity.showInteractDialogBottomSheet() {
    val viewGroup = this.findViewById(android.R.id.content) as ViewGroup
    viewGroup.addView(
        ComposeView(viewGroup.context).apply {
            setContent {
                BottomSheetWrapper(viewGroup, this)
            }
        }
    )
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
//                BottomSheetWrapper(wrapWithBottomSheetUI, viewGroup, this, content, onCloseEvent)
            }
        }
    )
}