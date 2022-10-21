package com.cmoney.fanci.ui.screens.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.ui.common.BlueCircleDot
import com.cmoney.fanci.ui.screens.shared.CenterTopAppBar
import com.socks.library.KLog

@Composable
fun ChatRoomTopBarScreen(navController: NavHostController) {
    val TAG = "ChatRoomTopBarScreen"
    CenterTopAppBar(
        leading = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(Icons.Filled.ArrowBack, null)
            }
        },
        title = { Text("\uD83D\uDC57｜金針菇穿什麼", fontSize = 17.sp, color = Color.White) },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        trailing = {
            Box(
                modifier = Modifier
                    .size(width = 35.dp, height = 35.dp)
                    .padding(end = 16.dp)
                    .clickable {
                        KLog.i(TAG, "more click")
                    },
                contentAlignment = Alignment.Center
            ) {
                BlueCircleDot()
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ChatRoomTopBarScreenPreview() {
    ChatRoomTopBarScreen(rememberNavController())
}