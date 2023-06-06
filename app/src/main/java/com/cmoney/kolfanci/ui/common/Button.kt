package com.cmoney.kolfanci.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

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
            color = LocalColor.current.text.default_100,
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
fun BlueButton(
    modifier: Modifier = Modifier
        .padding(25.dp)
        .fillMaxWidth()
        .height(50.dp),
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
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

@Composable
fun GrayButton(
    text: String,
    shape: RoundedCornerShape = RoundedCornerShape(15),
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = shape,
        colors = ButtonDefaults.buttonColors(backgroundColor = LocalColor.current.env_80),
        onClick = {
            onClick.invoke()
        }) {
        Text(
            text = text,
            color = LocalColor.current.text.default_100,
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GrayButtonPreview() {
    FanciTheme {
        GrayButton(text = "Hello") {}
    }
}

@Composable
fun BorderButton(
    modifier: Modifier = Modifier,
    text: String,
    shape: RoundedCornerShape = RoundedCornerShape(15),
    borderColor: Color,
    textColor: Color = borderColor,
    onClick: () -> Unit?
) {
    Button(
        modifier = modifier,
        shape = shape,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        border = BorderStroke(1.dp, borderColor),
        elevation = ButtonDefaults.elevation(0.dp),
        onClick = {
            onClick.invoke()
        }) {
        Text(
            text = text,
            color = textColor,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BorderButtonPreview() {
    FanciTheme {
        BorderButton(text = "Hello BorderButtonPreview", borderColor = Color.Gray) {}
    }
}