package com.cmoney.fanci.ui.screens.shared

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.common.EmojiText
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanci.ui.theme.White_262C34

@Composable
fun EmojiCountScreen(
    @DrawableRes emojiResource: Int,
    countText: String,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable {
                onClick.invoke(emojiResource)
            }
            .background(LocalColor.current.background)
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(15.dp),
                painter = painterResource(id = emojiResource), contentDescription = null
            )
            Spacer(modifier = Modifier.width(5.dp))
            EmojiText(text = countText)
        }

    }
}


@Preview(showBackground = true)
@Composable
fun EmojiCountScreenPreview() {
    EmojiCountScreen(R.drawable.emoji_happiness, "123"){}
}