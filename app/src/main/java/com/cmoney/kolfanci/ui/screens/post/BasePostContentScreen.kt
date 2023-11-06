package com.cmoney.kolfanci.ui.screens.post

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RichTooltipBox
import androidx.compose.material3.RichTooltipState
import androidx.compose.material3.TooltipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.MediaType
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.getFileName
import com.cmoney.kolfanci.extension.getFleSize
import com.cmoney.kolfanci.extension.toAttachmentType
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.common.AutoLinkPostText
import com.cmoney.kolfanci.ui.common.CircleDot
import com.cmoney.kolfanci.ui.screens.chat.AttachmentController
import com.cmoney.kolfanci.ui.screens.chat.message.MessageImageScreenV2
import com.cmoney.kolfanci.ui.screens.chat.message.MessageOGScreen
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
import com.cmoney.kolfanci.ui.screens.shared.ChatUsrAvatarScreen
import com.cmoney.kolfanci.ui.screens.shared.EmojiCountScreen
import com.cmoney.kolfanci.ui.screens.shared.attachment.AttachmentFileItem
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.kolfanci.utils.Utils
import com.google.accompanist.flowlayout.FlowRow
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog
import kotlinx.coroutines.launch

/**
 * 顯示 貼文/留言/回覆 內容
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasePostContentScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    post: BulletinboardMessage,
    defaultDisplayLine: Int = 4,
    contentModifier: Modifier = Modifier,
    hasMoreAction: Boolean = true,
    backgroundColor: Color = LocalColor.current.background,
    multiImageHeight: Dp = 220.dp,
    onMoreClick: () -> Unit? = {},
    onEmojiClick: (Int) -> Unit,
    onAddNewEmojiClick: (Int) -> Unit,
    onImageClick: (() -> Unit)? = null,
    onTextExpandClick: (() -> Unit)? = null,
    bottomContent: @Composable ColumnScope.() -> Unit
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    //Popup emoji selector
    val tooltipStateRich = remember { RichTooltipState() }

    //最多顯示幾行
    var maxDisplayLine by remember {
        mutableIntStateOf(defaultDisplayLine)
    }

    //是否要顯示 顯示更多文案
    var showMoreLine by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .background(backgroundColor)
            .padding(top = 10.dp, start = 20.dp, end = 20.dp, bottom = 5.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    KLog.i("TAG", offset)
                }
            }
    ) {
        //Avatar
        Row(verticalAlignment = Alignment.CenterVertically) {
            //大頭貼
            post.author?.let {
                ChatUsrAvatarScreen(user = it)
            }

            Spacer(modifier = Modifier.weight(1f))

            //More
            if (hasMoreAction) {
                Box(
                    modifier = Modifier
                        .padding(end = 15.dp)
                        .size(25.dp)
                        .clickable {
                            onMoreClick.invoke()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    CircleDot()
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Column(
            modifier = contentModifier
        ) {
            //Message text
            post.content?.text?.apply {
                if (this.isNotEmpty()) {
                    AutoLinkPostText(
                        text = this,
                        fontSize = 17.sp,
                        color = LocalColor.current.text.default_100,
                        maxLine = maxDisplayLine,
                        onLineCount = {
                            showMoreLine = it > maxDisplayLine
                        },
                        onClick = {
                            maxDisplayLine = if (maxDisplayLine == Int.MAX_VALUE) {
                                defaultDisplayLine
                            } else {
                                onTextExpandClick?.invoke()
                                Int.MAX_VALUE
                            }
                        }
                    )

                    //OG
                    Utils.extractLinks(this).forEach { url ->
                        MessageOGScreen(
                            modifier = Modifier.padding(
                                top = 10.dp,
                                end = 10.dp
                            ), url = url
                        )
                    }
                }
            }


            //顯示更多
            if (showMoreLine) {
                Text(
                    modifier = Modifier.clickable {
                        maxDisplayLine = if (maxDisplayLine == Int.MAX_VALUE) {
                            defaultDisplayLine
                        } else {
                            onTextExpandClick?.invoke()
                            Int.MAX_VALUE
                        }
                    },
                    text = "⋯⋯ 顯示更多",
                    fontSize = 17.sp,
                    color = LocalColor.current.text.default_100
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            //附加檔案
            post.content?.medias?.let { medias ->
                //========= Image =========
                val imageUrl = medias.filter {
                    it.type == MediaType.image
                }.map {
                    it.resourceLink.orEmpty()
                }

                MessageImageScreenV2(
                    images = imageUrl,
                    multiImageHeight = multiImageHeight,
                    onImageClick = {
                        onImageClick?.invoke()
                        AppUserLogger.getInstance().log(Page.PostImage)
                    }
                )

                Spacer(modifier = Modifier.height(15.dp))

                //========= Other File =========
                val otherUrl = medias.filter {
                    it.type != MediaType.image && it.type != MediaType.audio
                }

                LazyRow(
                    modifier = modifier.padding(start = 10.dp, end = 10.dp),
                    state = rememberLazyListState(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(otherUrl) { media ->
                        val fileUrl = media.resourceLink
                        val mediaType = media.type

                        AttachmentFileItem(
                            modifier = Modifier
                                .width(270.dp)
                                .height(75.dp),
                            file = Uri.parse(fileUrl),
                            fileSize = media.getFleSize(),
                            isItemClickable = true,
                            isItemCanDelete = false,
                            isShowResend = false,
                            displayName = media.getFileName(),
                            onClick = {
                                AttachmentController.onAttachmentClick(
                                    navController = navController,
                                    uri = Uri.parse(fileUrl),
                                    context = context,
                                    attachmentType = mediaType?.toAttachmentType(),
                                    fileName = media.getFileName().orEmpty()
                                )
                            },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                //========= Audio File =========
            }

            //Emoji
            FlowRow(
                crossAxisSpacing = 10.dp
            ) {
                post.emojiCount?.apply {
                    Utils.emojiMapping(this).forEach { emoji ->
                        if (emoji.second != 0) {
                            EmojiCountScreen(
                                modifier = Modifier
                                    .padding(end = 10.dp),
                                emojiResource = emoji.first,
                                countText = emoji.second.toString()
                            ) {
                                onEmojiClick.invoke(it)
                            }
                        }
                    }
                }

                RichTooltipBox(
                    modifier = Modifier
                        .padding(20.dp, bottom = 25.dp)
                        .fillMaxWidth()
                        .height(60.dp),
                    action = {
                        EmojiFeedback(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-15).dp)
                        ) {
                            onAddNewEmojiClick.invoke(it)
                            scope.launch { tooltipStateRich.dismiss() }
                        }
                    },
                    text = { },
                    shape = RoundedCornerShape(69.dp),
                    tooltipState = tooltipStateRich,
                    colors = TooltipDefaults.richTooltipColors(
                        containerColor = LocalColor.current.env_80
                    )
                ) {
                    //Add Emoji
                    EmojiCountScreen(
                        modifier = Modifier
                            .height(30.dp),
                        emojiResource = R.drawable.empty_emoji,
                        emojiIconSize = 23.dp,
                        countText = ""
                    ) {
                        scope.launch { tooltipStateRich.show() }
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            bottomContent()
        }
    }
}

@Composable
fun EmojiFeedback(
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
    Row(modifier = modifier) {
        Constant.emojiLit.forEach { res ->
            AsyncImage(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clickable {
                        onClick.invoke(res)
                    }
                    .padding(10.dp),
                model = res,
                contentDescription = null
            )
        }
    }
}


@Preview
@Composable
fun PostContentScreenPreview() {
    FanciTheme {
        BasePostContentScreen(
            post = PostViewModel.mockPost,
            bottomContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1天以前",
                        fontSize = 14.sp,
                        color = LocalColor.current.text.default_100
                    )

                    Box(
                        modifier = Modifier
                            .padding(start = 5.dp, end = 5.dp)
                            .size(3.8.dp)
                            .clip(CircleShape)
                            .background(LocalColor.current.text.default_100)
                    )

                    Text(
                        text = "留言 142",
                        fontSize = 14.sp,
                        color = LocalColor.current.text.default_100
                    )
                }
            },
            onMoreClick = {},
            onEmojiClick = {},
            onAddNewEmojiClick = {},
            navController = EmptyDestinationsNavigator
        )
    }
}