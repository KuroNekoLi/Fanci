package com.cmoney.kolfanci.ui.screens.shared.dialog

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cmoney.kolfanci.ui.theme.FanciTheme

/**
 * 打開相機/選擇圖片 彈窗
 */
@Composable
fun PhotoPickDialogScreen(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onAttach: (Uri) -> Unit,
) {
    GroupPhotoPickDialogScreen(
        modifier = modifier,
        isShowFanciPic = false,
        onDismiss = onDismiss,
        onAttach = onAttach,
        onFanciClick = {}
    )
}

private var captureUri: Uri? = null //Camera result callback

@Preview(showBackground = true)
@Composable
fun PhotoPickDialogScreenPreview() {
    FanciTheme {
        PhotoPickDialogScreen(
            onDismiss = {},
            onAttach = {}
        )
    }
}