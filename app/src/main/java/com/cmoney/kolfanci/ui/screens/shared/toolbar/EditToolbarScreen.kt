package com.cmoney.kolfanci.ui.screens.shared.toolbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.shared.CenterTopAppBar
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 帶有 關閉 以及 儲存的 toolbar
 */
@Composable
fun EditToolbarScreen(
    modifier: Modifier = Modifier,
    title: String,
    leadingIcon: ImageVector = Icons.Filled.Close,
    backgroundColor: Color = LocalColor.current.env_100,
    confirmText: String = stringResource(id = R.string.confirm),
    saveClick: (() -> Unit)? = null,
    backClick: (() -> Unit)? = null,
) {
    CenterTopAppBar(
        modifier = modifier,
        leading = {
            IconButton(onClick = {
                backClick?.invoke()
            }) {
                Icon(
                    leadingIcon, null,
                    tint = Color.White
                )
            }
        },
        title = { Text(title, fontSize = 17.sp, color = Color.White) },
        backgroundColor = backgroundColor,
        contentColor = LocalColor.current.text.default_100,
        trailing = {
            Box(modifier = Modifier
                .wrapContentSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    saveClick?.invoke()
                }
                .padding(end = 15.dp)) {
                Text(text = confirmText, fontSize = 17.sp, color = LocalColor.current.primary)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EditToolbarScreenPreview() {
    FanciTheme {
        EditToolbarScreen(
            title = "Test"
        )
    }
}