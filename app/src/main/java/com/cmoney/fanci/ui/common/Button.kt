package com.cmoney.fanci.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor

@Composable
fun TransparentButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .padding(25.dp)
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        elevation = ButtonDefaults.elevation(0.dp),
        border = BorderStroke(1.dp, LocalColor.current.text.default_50),
        onClick = {
            onClick.invoke()
        }) {
        Text(
            text = text,
            color = LocalColor.current.text.other,
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransparentButtonPreview() {
    FanciTheme {
        TransparentButton(text = "Hello") {}
    }
}

@Composable
fun BlueButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .padding(25.dp)
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = LocalColor.current.primary),
        onClick = {
            onClick.invoke()
        }) {
        Text(
            text = text,
            color = LocalColor.current.text.other,
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BlueButtonPreview() {
    FanciTheme {
        BlueButton(text = "Hello") {}
    }
}