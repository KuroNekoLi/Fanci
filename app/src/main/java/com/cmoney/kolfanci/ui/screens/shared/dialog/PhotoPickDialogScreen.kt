package com.cmoney.kolfanci.ui.screens.shared.dialog

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cmoney.kolfanci.ui.screens.chat.attachment.AttachImageDefault
import com.cmoney.kolfanci.ui.theme.FanciTheme

/**
 * 打開相機/選擇圖片 彈窗
 */
@Composable
fun PhotoPickDialogScreen(
    modifier: Modifier = Modifier,
    quantityLimit: Int = AttachImageDefault.getQuantityLimit(),
    onDismiss: () -> Unit,
    onAttach: (List<Uri>) -> Unit,
) {
    GroupPhotoPickDialogScreen(
        modifier = modifier,
        isShowFanciPic = false,
        quantityLimit = quantityLimit,
        onDismiss = onDismiss,
        onAttach = onAttach,
        onFanciClick = {}
    )
}

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