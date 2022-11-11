package com.cmoney.fanci.ui.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.ui.common.CircleDot
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.socks.library.KLog

@Composable
fun TopBarScreen(
    navController: NavHostController,
    title: String,
    leadingEnable: Boolean = true,
    leadingIcon: ImageVector = Icons.Filled.ArrowBack,
    trailingEnable: Boolean = true,
    moreEnable: Boolean,
    moreClick: (() -> Unit)?
) {
    val TAG = "TopBarScreen"
    CenterTopAppBar(
        leading = {
            if (leadingEnable) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        leadingIcon, null,
                        tint = Color.White
                    )
                }
            }
        },
        title = { Text(title, fontSize = 17.sp, color = Color.White) },
        backgroundColor = LocalColor.current.env_100,
        contentColor = Color.White,
        trailing = {
            if (trailingEnable) {
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .offset(x = (-15).dp)
                        .clip(CircleShape)
                        .background(
                            if (moreEnable) {
                                LocalColor.current.background
                            } else {
                                Color.Transparent
                            }
                        )
                        .clickable {
                            if (moreEnable) {
                                KLog.i(TAG, "more click")
                                moreClick?.invoke()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (moreEnable) {
                        CircleDot()
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ChatRoomTopBarScreenPreview() {
    FanciTheme {
        TopBarScreen(
            rememberNavController(),
            title = "Test",
            moreEnable = false
        ) {

        }
    }
}