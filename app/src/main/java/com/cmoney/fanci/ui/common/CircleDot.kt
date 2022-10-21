package com.cmoney.fanci.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.cmoney.fanci.ui.theme.Blue_4F70E5

@Composable
fun BlueCircleDot() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier
                .size(3.8.dp)
                .clip(CircleShape)
                .background(Blue_4F70E5)
        )
        Box(
            modifier = Modifier
                .size(3.8.dp)
                .clip(CircleShape)
                .background(Blue_4F70E5)
        )
        Box(
            modifier = Modifier
                .size(3.8.dp)
                .clip(CircleShape)
                .background(Blue_4F70E5)
        )
    }
}