package com.cmoney.kolfanci.ui.screens.group.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.ui.common.AutoLinkText
import com.cmoney.kolfanci.ui.common.GroupText
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun GroupItemDialogScreen(
    modifier: Modifier = Modifier,
    isJoined: Boolean = false,
    groupModel: Group,
    background: Color = LocalColor.current.env_80,
    titleColor: Color = LocalColor.current.text.default_100,
    descColor: Color = LocalColor.current.text.default_80,
    joinTextColor: Color = LocalColor.current.primary,
    onDismiss: () -> Unit,
    onConfirm: (Group) -> Unit
) {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        Dialog(onDismissRequest = {
            openDialog.value = false
            onDismiss.invoke()
        }) {
            GroupItemDialogScreenView(
                modifier = modifier,
                background = background,
                groupModel = groupModel,
                titleColor = titleColor,
                descColor = descColor,
                onConfirm = onConfirm,
                isJoined = isJoined,
                joinTextColor = joinTextColor
            )
        }
    }
    LaunchedEffect(key1 = groupModel) {
        AppUserLogger.getInstance()
            .log(page = Page.Group)
    }
}

@Composable
private fun GroupItemDialogScreenView(
    modifier: Modifier = Modifier,
    groupModel: Group,
    background: Color = LocalColor.current.env_80,
    titleColor: Color = LocalColor.current.text.default_100,
    descColor: Color = LocalColor.current.text.default_80,
    joinTextColor: Color = LocalColor.current.primary,
    onConfirm: (Group) -> Unit,
    isJoined: Boolean
) {
    Box(
        modifier = modifier
            .padding(bottom = 30.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(25.dp))
            .background(background)
    ) {
        Column {
            AsyncImage(
                model = groupModel.coverImageUrl,
                modifier = Modifier
                    .height(170.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.placeholder)
            )

            GroupText(
                modifier = Modifier.padding(top = 15.dp, start = 110.dp),
                text = groupModel.name.orEmpty(),
                textColor = titleColor
            )

            Spacer(modifier = Modifier.height(35.dp))

            Column(
                modifier = Modifier
                    .heightIn(0.dp, 300.dp)
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                AutoLinkText(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 15.dp),
                    text = groupModel.description.orEmpty(),
                    fontSize = 17.sp,
                    color = descColor
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.Transparent)
                    .clickable {
                        onConfirm.invoke(groupModel)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isJoined) {
                        "已經加入，進入社團"
                    } else {
                        "加入社團"
                    }, fontSize = 16.sp,
                    color = joinTextColor
                )
            }
        }

        AsyncImage(
            model = groupModel.thumbnailImageUrl,
            modifier = Modifier
                .padding(top = 130.dp, start = 20.dp)
                .size(75.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(25.dp)),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.placeholder)
        )
    }
}

@Preview
@Composable
fun GroupItemDialogScreenPreview() {
    FanciTheme {
        GroupItemDialogScreenView(
            isJoined = true,
            groupModel = Group(
                id = "",
                name = "Hello",
                description = "大家好，我是愛莉莎莎Alisasa！\n" +
                        "\n" +
                        "台灣人在韓國留學八個月 \n" +
                        "已經在2018 一月\n" +
                        "回到台灣當全職Youtuber囉！\n" +
                        "\n" +
                        "但是我還是每個月會去韓國\n" +
                        "更新最新的韓國情報 （流行 美妝 美食等等） \n" +
                        "提供給大家不同於一般觀光客\n" +
                        "內行的認識韓國新角度\n" +
                        "\n" +
                        "另外也因為感情經驗豐富（？）\n" +
                        "可以提供給大家一些女生的秘密想法～\n" +
                        "\n" +
                        "希望大家喜歡我的頻道＾＾\n" +
                        "\n" +
                        "\n" +
                        "如果你喜歡我的影片，希望你可以幫我訂閱＋分享\n" +
                        "\n" +
                        "任何合作邀約請洽Pressplay Email :\n" +
                        "alisasa@pressplay.cc\n" +
                        "═════════════════════════════════════\n" +
                        "\n" +
                        "追蹤我 Follow Me \n" +
                        "\n" +
                        "★Facebook社團『愛莉莎莎敗家基地』: https://www.facebook.com/groups/924974291237889/\n" +
                        "★Facebook粉絲專頁: https://www.facebook.com/alisasa11111/\n" +
                        "★Instagram: goodalicia",
                coverImageUrl = "",
                thumbnailImageUrl = "",
                categories = emptyList()
            ),
            onConfirm = {
            }
        )
    }
}