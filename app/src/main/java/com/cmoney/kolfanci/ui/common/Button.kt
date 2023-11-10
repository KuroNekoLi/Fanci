package com.cmoney.kolfanci.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
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
fun GroupJoinButton(
    modifier: Modifier = Modifier,
    text: String,
    shape: RoundedCornerShape = RoundedCornerShape(4.dp),
    onClick: () -> Unit
) {

    androidx.compose.material3.Button(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = LocalColor.current.background,
            contentColor = LocalColor.current.background
        ),
        shape = shape,
        onClick = {
            onClick.invoke()
        }
    ) {
        Text(
            text = text,
            color = LocalColor.current.text.default_100,
            fontSize = 16.sp
        )
    }
}

@Preview
@Composable
fun GroupJoinButtonPreview() {
    FanciTheme {
        GroupJoinButton(text = "Hello") {}
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
    onClick: () -> Unit
) {
    androidx.compose.material3.Button(
        modifier = modifier,
        shape = shape,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Transparent
        ),
        border = BorderStroke(1.dp, borderColor),
        onClick = onClick
    ) {
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

/**
 * 虛線邊匡 - 中間加號
 */
@Composable
fun DashPlusButton(
    onClick: () -> Unit
) {
    val stroke = Stroke(
        width = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)
    )
    val borderColor = LocalColor.current.text.default_30

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable {
                onClick.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            Modifier.fillMaxSize()
        ) {
            drawRoundRect(
                color = borderColor, style = stroke,
                cornerRadius = CornerRadius(8.dp.toPx())
            )
        }

        Image(
            painter = painterResource(id = R.drawable.plus_white),
            contentDescription = null
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashPlusButtonPreview() {
    FanciTheme {
        DashPlusButton {
        }
    }
}