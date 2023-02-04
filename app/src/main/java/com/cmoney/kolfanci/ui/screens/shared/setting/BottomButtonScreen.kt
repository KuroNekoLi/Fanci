package com.cmoney.kolfanci.ui.screens.shared.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun BottomButtonScreen(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .background(LocalColor.current.env_100),
        contentAlignment = Alignment.Center
    ) {
        BlueButton(
            text = text
        ) {
            onClick.invoke()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomButtonScreenPreview() {
    FanciTheme {
        BottomButtonScreen(text = "Save") {}
    }
}