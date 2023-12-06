package com.cmoney.kolfanci.ui.screens.shared.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.globalGroupViewModel
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.GroupText
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 *
 * 邀請加入社團 Dialog
 *
 * @param groupModel 社團model
 * @param onDismiss 取消 callback
 * @param background 背景顏色
 * @param titleColor title 顏色
 */
@Composable
fun InviteGroupDialogScreen(
    modifier: Modifier = Modifier,
    groupModel: Group,
    onDismiss: () -> Unit,
    background: Color = LocalColor.current.env_80,
    titleColor: Color = LocalColor.current.text.default_100
) {
    val globalGroupViewModel = globalGroupViewModel()

    val myGroupList by globalGroupViewModel.myGroupList.collectAsState()

    val groupStatus by globalGroupViewModel.joinGroupStatus.collectAsState()

    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        Dialog(onDismissRequest = {
            openDialog.value = false
            onDismiss.invoke()
        }) {
            InviteGroupDialogScreenView(
                modifier = modifier,
                groupModel = groupModel,
                background = background,
                titleColor = titleColor
            )
        }
    }

    LaunchedEffect(key1 = groupModel) {
        AppUserLogger.getInstance()
            .log(page = Page.Group)
    }

    LaunchedEffect(key1 = groupStatus, key2 = myGroupList) {
        globalGroupViewModel.getGroupJoinStatus(groupModel)
    }
}

@Composable
private fun InviteGroupDialogScreenView(
    modifier: Modifier = Modifier,
    groupModel: Group,
    background: Color = LocalColor.current.env_80,
    titleColor: Color = LocalColor.current.text.default_100
) {
    Box(
        modifier = modifier
            .padding(bottom = 30.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(25.dp))
            .background(background)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                modifier = Modifier.padding(top = 45.dp),
                text = groupModel.name.orEmpty(),
                textColor = titleColor
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "通知朋友下列邀請碼，以加入社團",
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = LocalColor.current.text.default_80
                )
            )

            Spacer(modifier = Modifier.height(15.dp))

            //邀請碼
            Box(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(LocalColor.current.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "522 180",
                    style = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 30.sp,
                        color = LocalColor.current.text.other
                    )
                )
            }

            BlueButton(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                text = "複製邀請碼",
                onClick = {
                    //TODO
                }
            )

            //Divider
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = LocalColor.current.text.default_80
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "或透過社團連結直接邀請",
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = LocalColor.current.text.default_80
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Divider(
                    modifier = Modifier.weight(1f),
                    color = LocalColor.current.text.default_80
                )
            }

            BlueButton(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 40.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                text = "分享",
                onClick = {
                    //TODO
                }
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 130.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = groupModel.thumbnailImageUrl,
                modifier = Modifier
                    .size(75.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(25.dp)),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.placeholder)
            )
        }
    }
}

@Preview
@Composable
fun InviteGroupDialogScreenPreview() {
    FanciTheme {
        InviteGroupDialogScreenView(
            groupModel = MockData.mockGroup
        )
    }
}