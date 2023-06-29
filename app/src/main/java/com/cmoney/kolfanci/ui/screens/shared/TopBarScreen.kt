package com.cmoney.kolfanci.ui.screens.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

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
    )
}

@Preview(showBackground = true)
@Composable
fun ChatRoomTopBarScreenPreview() {
    FanciTheme {
        TopBarScreen(
            title = "Test",
            trailingContent = {
                Image(
                    painter = painterResource(id = R.drawable.vip_diamond),
                    contentDescription = null
                )
            }
        ) {

        }
    }
}