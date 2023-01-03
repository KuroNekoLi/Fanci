package com.cmoney.fanci.ui.screens.shared.dialog.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanci.ui.common.BorderButton
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor

@Composable
fun DisBanItemScreen(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        BorderButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = "解除禁言",
            borderColor = LocalColor.current.component.other,
            textColor = Color.White
        ) {
            onConfirm.invoke()
        }

        Spacer(modifier = Modifier.height(20.dp))

        BorderButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = "返回",
            borderColor = LocalColor.current.component.other,
            textColor = Color.White
        ) {
            onDismiss.invoke()
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DisBanItemScreenPreview() {
    FanciTheme {
        DisBanItemScreen(
            onConfirm = {},
            onDismiss = {}
        )
    }
}