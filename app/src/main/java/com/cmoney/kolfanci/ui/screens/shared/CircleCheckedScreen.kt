package com.cmoney.kolfanci.ui.screens.shared

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun CircleCheckedScreen(modifier: Modifier = Modifier, isChecked: Boolean) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(
                    if (isChecked) {
                        LocalColor.current.primary
                    } else {
                        Color.Transparent
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Image(
                    modifier = Modifier.size(12.dp),
                    painter = painterResource(id = R.drawable.checked),
                    colorFilter = ColorFilter.tint(color = Color.White),
                    contentDescription = null)
            }
        }

        Canvas(modifier = Modifier.size(57.dp)) {
            drawCircle(
                color = Color.White,
                radius = 30f,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CircleCheckedScreenPreview() {
    FanciTheme {
        CircleCheckedScreen(isChecked = true)
    }
}