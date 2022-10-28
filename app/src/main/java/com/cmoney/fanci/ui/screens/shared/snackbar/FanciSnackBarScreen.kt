package com.cmoney.fanci.ui.screens.shared.snackbar

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanci.R
import com.radusalagean.infobarcompose.BaseInfoBarMessage
import com.radusalagean.infobarcompose.InfoBar
import androidx.compose.ui.Alignment
import com.radusalagean.infobarcompose.InfoBarSlideEffect

class CustomMessage(
    val textString: String,
    @DrawableRes val iconRes: Int,
    val iconColor: Color,
    val textColor: Color = Color.Unspecified,
    override val backgroundColor: Color? = null,
    override val displayTimeSeconds: Int? = 3,
) : BaseInfoBarMessage() {
    override val containsControls: Boolean = false
}

@Composable
fun FanciSnackBarScreen(
    modifier: Modifier = Modifier,
    message: CustomMessage?,
    onDismiss: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        InfoBar(
            modifier
                .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
                .align(Alignment.BottomCenter)
                .height(47.dp),
            offeredMessage = message,
            content = content,
            fadeEffect = false,
            slideEffect = InfoBarSlideEffect.FROM_BOTTOM
        ) {
            onDismiss.invoke()
        }
    }
}

val content: @Composable (CustomMessage) -> Unit = { message ->
    Row {
        Icon(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically),
            imageVector = ImageVector.vectorResource(id = message.iconRes),
            contentDescription = null,
            tint = message.iconColor
        )
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = message.textString,
            color = message.textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FanciSnackBarScreenPreview() {
    FanciSnackBarScreen(
        message = CustomMessage(
            textString = "This is a custom message",
            textColor = Color(0xFF414141),
            iconRes = R.drawable.recycle,
            iconColor = Color(0xFF27C54D),
            backgroundColor = Color(0xFFE3F1E6)
        )
    ) {

    }
}