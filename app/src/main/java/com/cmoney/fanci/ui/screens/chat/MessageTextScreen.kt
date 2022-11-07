package com.cmoney.fanci.ui.screens.chat

import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Patterns
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.util.LinkifyCompat
import com.cmoney.fanci.ui.theme.White_DDDEDF

/**
 * 文字訊息
 */
@Composable
fun MessageTextScreen(modifier: Modifier = Modifier, text: String) {
    val context = LocalContext.current
    val customLinkifyTextView = remember {
        TextView(context)
    }
    AndroidView(modifier = modifier, factory = { customLinkifyTextView }) { textView ->
        textView.text = text
        textView.setTextColor(White_DDDEDF.toArgb())
        textView.textSize = 17f
        LinkifyCompat.addLinks(textView, Linkify.WEB_URLS)
        Linkify.addLinks(
            textView, Patterns.PHONE, "tel:",
            Linkify.sPhoneNumberMatchFilter, Linkify.sPhoneNumberTransformFilter
        )
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}

@Preview(showBackground = true)
@Composable
fun MessageTextScreenPreview() {
    MessageTextScreen(text = "Hello")
}