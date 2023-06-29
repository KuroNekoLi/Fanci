package com.cmoney.kolfanci.ui.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import com.cmoney.kolfanci.ui.common.CircleDot
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.socks.library.KLog

@Composable
fun TopBarScreen(
    title: String,
    leadingEnable: Boolean = true,
    leadingIcon: ImageVector = Icons.Filled.ArrowBack,
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
    backgroundColor: Color = LocalColor.current.env_100,
    backClick: (() -> Unit)? = null,
) {
    val TAG = "TopBarScreen"
    CenterTopAppBar(
        leading = {
            if (leadingEnable) {
                IconButton(onClick = {
                    backClick?.invoke()
                }) {
                    Icon(
                        leadingIcon, null,
                        tint = Color.White
                    )
                }
            }
        },
        title = { Text(title, fontSize = 17.sp, color = Color.White) },
        backgroundColor = backgroundColor,
        contentColor = Color.White,
        trailing = trailingContent
//        trailing = {
//            if (trailingEnable) {
//                Box(
//                    modifier = Modifier
//                        .size(35.dp)
//                        .offset(x = (-15).dp)
//                        .clip(CircleShape)
//                        .background(
//                            if (moreEnable) {
//                                LocalColor.current.background
//                            } else {
//                                Color.Transparent
//                            }
//                        )
//                        .clickable(enabled = moreEnable) {
//                            if (moreEnable) {
//                                KLog.i(TAG, "more click")
//                                moreClick?.invoke()
//                            }
//                        },
//                    contentAlignment = Alignment.Center
//                ) {
//                    if (moreEnable) {
//                        trailingContent
//                    }
//                }
//            }
//        }
    )
}

@Preview(showBackground = true)
@Composable
fun ChatRoomTopBarScreenPreview() {
    FanciTheme {
        TopBarScreen(
            title = "Test"
        ) {

        }
    }
}