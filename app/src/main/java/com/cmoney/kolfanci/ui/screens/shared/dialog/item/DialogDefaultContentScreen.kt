package com.cmoney.kolfanci.ui.screens.shared.dialog.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 彈窗內容 以及 確定,返回 二個按鈕
 */
@Composable
fun DialogDefaultContentScreen(
    modifier: Modifier = Modifier,
    content: AnnotatedString,
    inlineContent: Map<String, InlineTextContent> = emptyMap(),
    confirmTitle: String,
    cancelTitle: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    DialogContentView(
        modifier = modifier,
        showContent = content.isNotBlank(),
        content = {
            Text(
                text = content,
                inlineContent = inlineContent,
                fontSize = 17.sp,
                color = LocalColor.current.text.default_100
            )
        },
        confirmTitle = confirmTitle,
        cancelTitle = cancelTitle,
        onConfirm = onConfirm,
        onCancel = onCancel
    )
}

/**
 * 彈窗內容 以及 確定,返回 二個按鈕
 */
@Composable
fun DialogDefaultContentScreen(
    modifier: Modifier = Modifier,
    content: String,
    confirmTitle: String,
    cancelTitle: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    DialogContentView(
        modifier = modifier,
        showContent = content.isNotBlank(),
        content = {
            Text(
                text = content,
                fontSize = 17.sp,
                color = LocalColor.current.text.default_100
            )
        },
        confirmTitle = confirmTitle,
        cancelTitle = cancelTitle,
        onConfirm = onConfirm,
        onCancel = onCancel
    )
}

@Composable
private fun DialogContentView(
    modifier: Modifier = Modifier,
    showContent: Boolean = true,
    content: @Composable () -> Unit,
    confirmTitle: String,
    cancelTitle: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (showContent) {
            item {
                content()
            }
        }
        item {
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = confirmTitle,
                borderColor = LocalColor.current.component.other,
                textColor = LocalColor.current.specialColor.red
            ) {
                onConfirm.invoke()
            }
        }
        item {
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = cancelTitle,
                borderColor = LocalColor.current.component.other,
                textColor = LocalColor.current.text.default_100
            ) {
                onCancel.invoke()
            }
        }
    }
}

@Preview
@Composable
fun DialogDefaultContentScreenPreview() {
    FanciTheme {
        DialogDefaultContentScreen(
            content = "一旦被禁言，將會無法對頻道做出\n" +
                    "任何社群行為：留言、按讚等等。",
            confirmTitle = "確定禁言",
            cancelTitle = "返回",
            onConfirm = {},
            onCancel = {}
        )
    }
}