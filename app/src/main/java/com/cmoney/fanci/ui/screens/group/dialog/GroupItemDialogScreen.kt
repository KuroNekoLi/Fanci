package com.cmoney.fanci.ui.screens.group.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.cmoney.fanci.R
import com.cmoney.fanci.model.GroupModel
import com.cmoney.fanci.ui.common.AutoLinkText
import com.cmoney.fanci.ui.common.GroupText
import com.cmoney.fanci.ui.theme.Blue_4F70E5
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanci.ui.theme.White_262C34

@Composable
fun GroupItemDialogScreen(
    modifier: Modifier = Modifier,
    groupModel: GroupModel,
    onDismiss: () -> Unit
) {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        Dialog(onDismissRequest = {
            openDialog.value = false
            onDismiss.invoke()
        }) {
            Box(
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .clip(RoundedCornerShape(25.dp))
                    .background(LocalColor.current.env_80)
            ) {
                Column {
                    AsyncImage(
                        model = groupModel.coverImageUrl,
                        modifier = Modifier
                            .height(170.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        placeholder = painterResource(id = R.drawable.resource_default)
                    )

                    GroupText(
                        modifier = Modifier.padding(top = 15.dp, start = 110.dp),
                        text = groupModel.name,
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
                            text = groupModel.description,
                            fontSize = 17.sp,
                            color = LocalColor.current.text.default_80
                        )
                    }

                    Button(
                        modifier = modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent
                        ),
                        elevation = ButtonDefaults.elevation(0.dp),
                        onClick = { /*TODO*/ }) {
                        Text(text = "加入社團", fontSize = 16.sp, color = LocalColor.current.primary)
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
                    placeholder = painterResource(id = R.drawable.resource_default)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JoinGroupDialogScreenPreview() {
    FanciTheme {
        GroupItemDialogScreen(
            groupModel = GroupModel(
                groupId = "",
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
            )
        ) {

        }
    }
}