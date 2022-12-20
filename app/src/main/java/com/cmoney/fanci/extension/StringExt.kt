package com.cmoney.fanci.extension

import androidx.compose.ui.graphics.Color


fun String.toColor(): Color = Color(this.toLong(16))
