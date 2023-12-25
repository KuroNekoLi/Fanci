package com.cmoney.kolfanci.ui.screens.shared.item

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun WideItem(
    modifier: Modifier = Modifier,
    title: String,
    displayContent: (@Composable () -> Unit)? = null,
    titleColor: Color = LocalColor.current.text.default_100,
    description: String? = null,
    descriptionColor: Color = LocalColor.current.text.default_50,
    interactionSource: MutableInteractionSource = remember {
        MutableInteractionSource()
    },
    indication: Indication? = LocalIndication.current,
    onClick: (() -> Unit)? = null
) {
    val itemModifier = if (onClick != null) {
        Modifier
            .heightIn(min = 86.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = indication,
                onClick = onClick
            )
            .then(modifier)
    } else {
        Modifier
            .heightIn(min = 86.dp)
            .indication(
                interactionSource = interactionSource,
                indication = indication
            )
            .then(modifier)
    }
    Row(
        modifier = itemModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = titleColor,
                fontWeight = FontWeight.Bold
            )
            if (description != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = descriptionColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (displayContent != null) {
            Spacer(modifier = Modifier.width(4.dp))
            displayContent()
        }
    }
}

object WideItemDefaults {
    val paddingValues = PaddingValues(
        start = 25.dp,
        end = 24.dp,
        top = 15.dp,
        bottom = 15.dp
    )

    @Composable
    fun imageDisplay(
        model: Any?,
        placeHolder: Painter? = painterResource(id = R.drawable.placeholder),
        modifier: Modifier = Modifier
            .size(56.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp)),
    ): @Composable () -> Unit = @Composable {
        AsyncImage(
            model = model,
            modifier = modifier,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            placeholder = placeHolder
        )
    }
}

@Preview
@Composable
fun WideItemPreview() {
    FanciTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalColor.current.env_80),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                WideItem(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .background(LocalColor.current.background)
                        .padding(WideItemDefaults.paddingValues),
                    title = "標題文字",
                    displayContent = WideItemDefaults.imageDisplay(model = ""),
                    onClick = {
                    }
                )
            }
            item {
                WideItem(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .background(LocalColor.current.background)
                        .padding(WideItemDefaults.paddingValues),
                    title = "標題文字",
                    description = "補述文字補述文字補述文字補述文字補述文字補述文字補述文字補述文字補述文字",
                    displayContent = WideItemDefaults.imageDisplay(model = ""),
                    onClick = {
                    }
                )
            }
        }
    }
}
