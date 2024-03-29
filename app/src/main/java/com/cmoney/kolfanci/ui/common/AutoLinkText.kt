package com.cmoney.kolfanci.ui.common

import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.util.LinkifyCompat
import androidx.core.view.doOnLayout
import com.socks.library.KLog

/**
 *  帶連結 文字訊息
 */
@Composable
fun AutoLinkText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = TextUnit.Unspecified,
    color: Color = Color.Unspecified,
    onLongClick: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val customLinkifyTextView = remember {
        TextView(context)
    }
    AndroidView(modifier = modifier, factory = { customLinkifyTextView }) { textView ->
        textView.text = text
        textView.setTextColor(color.toArgb())
        textView.textSize = fontSize.value
        LinkifyCompat.addLinks(textView, Linkify.WEB_URLS)
//        Linkify.addLinks(
//            textView, Patterns.PHONE, "tel:",
//            Linkify.sPhoneNumberMatchFilter, Linkify.sPhoneNumberTransformFilter
//        )
        textView.movementMethod = LinkMovementMethod.getInstance()

        textView.setOnLongClickListener {
            onLongClick?.invoke()
            true
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AutoLinkTextPreview() {
    AutoLinkText(text = "Hello") {}
}

@Composable
fun AutoLinkPostText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = TextUnit.Unspecified,
    color: Color = Color.Unspecified,
    maxLine: Int = Int.MAX_VALUE,
    onClick: (() -> Unit)? = null,
    onLineCount: ((Int) -> Unit)? = null
) {
    val context = LocalContext.current
    val customLinkifyTextView = remember {
        TextView(context)
    }
    AndroidView(modifier = modifier, factory = { customLinkifyTextView }) { textView ->
        textView.text = text
        textView.setTextColor(color.toArgb())
        textView.textSize = fontSize.value
        textView.maxLines = maxLine
        LinkifyCompat.addLinks(textView, Linkify.WEB_URLS)
//        Linkify.addLinks(
//            textView, Patterns.PHONE, "tel:",
//            Linkify.sPhoneNumberMatchFilter, Linkify.sPhoneNumberTransformFilter
//        )
        textView.movementMethod = LinkMovementMethod.getInstance()

        textView.doOnLayout {
            val lineCount = (it as TextView).lineCount
            onLineCount?.invoke(lineCount)
        }

        textView.setOnClickListener {
            onClick?.invoke()
        }
    }
}