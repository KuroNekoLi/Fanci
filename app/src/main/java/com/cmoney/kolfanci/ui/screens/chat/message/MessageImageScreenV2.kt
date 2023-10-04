package com.cmoney.kolfanci.ui.screens.chat.message

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bumptech.glide.Glide
import com.cmoney.kolfanci.R
import com.socks.library.KLog
import com.stfalcon.imageviewer.StfalconImageViewer

/**
 * 內容圖片顯示 Layout
 *
 * @param modifier 第一張圖的 Modifier, 因為聊天室第一張比較內縮
 * @param otherItemModifier 非第一張圖的 Modifier
 * @param isClickable 是否可以點擊
 * @param onImageClick 點擊 callback
 */
@Composable
fun MessageImageScreenV2(
    images: List<Any>,
    modifier: Modifier = Modifier,
    otherItemModifier: Modifier = Modifier,
    isClickable: Boolean = true,
    onImageClick: ((Any) -> Unit)? = null
) {
    val context = LocalContext.current

    //單張圖片
    if (images.size == 1) {
        MessageImageItem(
            modifier = modifier,
            image = images.first(),
            imageHeightMax = 410.dp,
            isClickable = isClickable,
            onClick = {
                onImageClick?.invoke(it)
                StfalconImageViewer
                    .Builder(
                        context, images
                    ) { imageView, image ->
                        Glide
                            .with(context)
                            .load(image)
                            .into(imageView)
                    }
                    .show()
            }
        )
    }
    //多張圖片
    else {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            itemsIndexed(images) { position, image ->
                val fixModifier = if (position == 0) {
                    modifier
                } else {
                    otherItemModifier
                }

                MessageImageItem(
                    modifier = fixModifier,
                    image = image,
                    imageHeightMin = 220.dp,
                    imageHeightMax = 220.dp,
                    imageWidthMin = 165.dp,
                    imageWidthMax = 290.dp,
                    isClickable = isClickable,
                    onClick = {
                        onImageClick?.invoke(it)
                        StfalconImageViewer
                            .Builder(
                                context, images
                            ) { imageView, image ->
                                Glide
                                    .with(context)
                                    .load(image)
                                    .into(imageView)
                            }
                            .show()
                            .setCurrentPosition(position)
                    }
                )
            }
        }
    }
}

/**
 * 訊息圖片 item
 * 圖片依寬度撐滿
 * 當高度固定時, 需要依高度撐滿
 *
 * @param image 圖片
 * @param imageHeightMin 圖片最小高度
 * @param imageHeightMax 圖片最大高度
 * @param imageWidthMin 圖片最小寬度
 * @param imageWidthMax 圖片最大寬度
 */
@Composable
fun MessageImageItem(
    modifier: Modifier = Modifier,
    image: Any,
    imageHeightMin: Dp = 0.dp,
    imageHeightMax: Dp = 0.dp,
    imageWidthMin: Dp = 0.dp,
    imageWidthMax: Dp = 0.dp,
    isClickable: Boolean = true,
    onClick: ((Any) -> Unit)? = null
) {
    val TAG = "MessageImageItem"

    var maxWidth by remember {
        mutableStateOf(imageWidthMax)
    }

    var scaleType by remember {
        mutableStateOf(ContentScale.FillWidth)
    }

    val imageModifier = modifier
        .heightIn(min = imageHeightMin, max = imageHeightMax)
        .clip(RoundedCornerShape(12.dp))
        .clickable(enabled = isClickable) {
            onClick?.invoke(image)
        }.let {
            if (imageWidthMax == 0.dp) {
                it.then(Modifier.fillMaxWidth())
            } else {
                it.then(Modifier.widthIn(min = imageWidthMin, max = maxWidth))
            }
        }

    AsyncImage(
        modifier = imageModifier,
        model = image,
        contentDescription = null,
        contentScale = scaleType,
        alignment = Alignment.TopStart,
        placeholder = painterResource(id = R.drawable.placeholder),
        onSuccess = {
            val size = it.painter.intrinsicSize
            val width = size.width
            val height = size.height

            KLog.i(TAG, "size: width = $width, height = $height")

            maxWidth = when {
                (height == width) -> {
                    imageHeightMax
                }

                (height > width) -> {
                    //高度固定時
                    if (imageHeightMin == imageHeightMax) {
                        scaleType = ContentScale.Crop
                    }
                    imageWidthMin
                }

                else -> {
                    //高度固定時
                    if (imageHeightMin == imageHeightMax) {
                        scaleType = ContentScale.FillHeight
                    }
                    imageWidthMax
                }
            }
        }
    )
}

@Preview
@Composable
fun MessageImageScreenV2Preview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        MessageImageScreenV2(
            listOf(
                "https://firebasestorage.googleapis.com/v0/b/kolfanci.appspot.com/o/rectange4.jpg?alt=media&token=48914caf-98ee-459a-a08d-94eeca0a9561",
                "https://firebasestorage.googleapis.com/v0/b/kolfanci.appspot.com/o/rectange3.jpg?alt=media&token=9cc451b9-328c-49e9-9eb5-564ec9b95306",
                "https://firebasestorage.googleapis.com/v0/b/kolfanci.appspot.com/o/rectange2.jpg?alt=media&token=3a8ba67f-3e75-4893-9825-60d1f42788c6",
                "https://firebasestorage.googleapis.com/v0/b/kolfanci.appspot.com/o/rectange.png?alt=media&token=19343790-47e2-4382-890d-5b0c4fe50966",
                "https://firebasestorage.googleapis.com/v0/b/kolfanci.appspot.com/o/square.jpg?alt=media&token=28638c38-b28c-40d4-83d4-e5a70ddb0cc2"
            )
        )
    }

}