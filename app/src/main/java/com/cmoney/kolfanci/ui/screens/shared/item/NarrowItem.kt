package com.cmoney.kolfanci.ui.screens.shared.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun NarrowItem(
    modifier: Modifier = Modifier,
    title: String,
    titleFontWeight: FontWeight = FontWeight.Bold,
    titleFontSize: TextUnit = 16.sp,
    prefixIcon: Painter? = null,
    prefixIconColor: Color = LocalColor.current.primary,
    subTitle: String? = null,
    subTitleFontSize: TextUnit = 16.sp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    indication: Indication? = LocalIndication.current,
    titleColor: Color = LocalColor.current.text.default_100,
    subTitleColor: Color = LocalColor.current.text.default_100,
    actionContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val itemModifier = if (onClick != null) {
        Modifier
            .heightIn(min = 44.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = indication,
                onClick = onClick
            )
            .then(modifier)
    } else {
        Modifier
            .heightIn(min = 44.dp)
            .indication(interactionSource, indication)
            .then(modifier)
    }
    Row(
        modifier = itemModifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            if (prefixIcon != null) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = prefixIcon,
                    contentDescription = "prefix",
                    tint = prefixIconColor
                )
            }
            Text(
                text = title,
                fontSize = titleFontSize,
                color = titleColor,
                fontWeight = titleFontWeight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (subTitle != null || actionContent != null) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (subTitle != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        modifier = Modifier.weight(1f, fill = false),
                        text = subTitle,
                        fontSize = subTitleFontSize,
                        color = subTitleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (actionContent != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    actionContent.invoke()
                }
            }
        }
    }
}

object NarrowItemDefaults {
    val paddingValues = PaddingValues(start = 25.dp, end = 15.dp, top = 10.dp, bottom = 10.dp)

    @Composable
    fun nextIcon(): @Composable () -> Unit = @Composable {
        Image(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = R.drawable.next),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = LocalColor.current.text.default_80)
        )
    }

    @Composable
    fun menuIcon(
        color: Color = LocalColor.current.primary,
        interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
        indication: Indication? = LocalIndication.current,
        onClick: () -> Unit
    ): @Composable () -> Unit = {
        Image(
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = indication,
                    onClick = onClick
                ),
            painter = painterResource(id = R.drawable.menu),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = color)
        )
    }

    @Composable
    fun text(
        text: String = "按鈕",
        fontSize: TextUnit = 14.sp,
        color: Color = LocalColor.current.primary,
        onClick: () -> Unit
    ): @Composable () -> Unit = {
        Text(
            modifier = Modifier
                .clickable(onClick = onClick),
            text = text,
            fontSize = fontSize,
            color = color
        )
    }
}

@Preview
@Composable
private fun NarrowItemPreview() {
    FanciTheme {
        val bellPainter = painterResource(id = R.drawable.bell)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalColor.current.env_80),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            previewItemContent()
            previewItemContent(
                prefixIcon = bellPainter
            )
        }
    }
}

private fun LazyListScope.previewItemContent(
    prefixIcon: Painter? = null
) {
    item {
        NarrowItem(
            modifier = Modifier
                .fillParentMaxWidth()
                .background(color = LocalColor.current.background)
                .padding(NarrowItemDefaults.paddingValues),
            prefixIcon = prefixIcon,
            title = "標題標題標題",
            subTitle = "文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字",
            actionContent = NarrowItemDefaults.nextIcon(),
            onClick = {}
        )
    }
    item {
        NarrowItem(
            modifier = Modifier
                .fillParentMaxWidth()
                .background(color = LocalColor.current.background)
                .padding(NarrowItemDefaults.paddingValues),
            prefixIcon = prefixIcon,
            title = "標題",
            actionContent = NarrowItemDefaults.nextIcon(),
            onClick = {}
        )
    }
    item {
        NarrowItem(
            modifier = Modifier
                .fillParentMaxWidth()
                .background(color = LocalColor.current.background)
                .padding(NarrowItemDefaults.paddingValues),
            prefixIcon = prefixIcon,
            title = "標題"
        )
    }
    item {
        NarrowItem(
            modifier = Modifier
                .fillParentMaxWidth()
                .background(color = LocalColor.current.background)
                .padding(NarrowItemDefaults.paddingValues),
            prefixIcon = prefixIcon,
            title = "標題",
            subTitle = "文字",
            actionContent = NarrowItemDefaults.nextIcon(),
            onClick = {}
        )
    }
    item {
        NarrowItem(
            modifier = Modifier
                .fillParentMaxWidth()
                .background(color = LocalColor.current.background)
                .padding(NarrowItemDefaults.paddingValues),
            prefixIcon = prefixIcon,
            title = "標題",
            titleColor = LocalColor.current.text.default_30,
            actionContent = NarrowItemDefaults.nextIcon(),
            onClick = {}
        )
    }
    item {
        NarrowItem(
            modifier = Modifier
                .fillParentMaxWidth()
                .background(color = LocalColor.current.background)
                .padding(NarrowItemDefaults.paddingValues),
            prefixIcon = prefixIcon,
            title = "標題",
            actionContent = NarrowItemDefaults.text {}
        )
    }
    item {
        val interactionSource = remember { MutableInteractionSource() }
        val indication = LocalIndication.current
        NarrowItem(
            modifier = Modifier
                .fillParentMaxWidth()
                .background(color = LocalColor.current.background)
                .padding(NarrowItemDefaults.paddingValues),
            prefixIcon = prefixIcon,
            title = "標題",
            interactionSource = interactionSource,
            indication = indication,
            actionContent = NarrowItemDefaults.menuIcon(
                interactionSource = interactionSource,
                indication = null
            ) {
            }
        )
    }
}
