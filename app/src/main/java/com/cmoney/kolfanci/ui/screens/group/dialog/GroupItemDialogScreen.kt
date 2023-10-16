package com.cmoney.kolfanci.ui.screens.group.dialog

import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.globalGroupViewModel
import com.cmoney.kolfanci.model.GroupJoinStatus
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.common.AutoLinkText
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.common.GroupJoinButton
import com.cmoney.kolfanci.ui.common.GroupText
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * @param groupModel 社團model
 * @param onDismiss 取消 callback
 * @param onConfirm 確定按鈕 callback
 * @param background 背景顏色
 * @param titleColor title 顏色
 * @param descColor 內文顏色
 */
@Composable
fun GroupItemDialogScreen(
    modifier: Modifier = Modifier,
    groupModel: Group,
    onDismiss: () -> Unit,
    onConfirm: (Group, GroupJoinStatus) -> Unit,
    background: Color = LocalColor.current.env_80,
    titleColor: Color = LocalColor.current.text.default_100,
    descColor: Color = LocalColor.current.text.default_80
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
            GroupItemDialogScreenView(
                modifier = modifier,
                groupModel = groupModel,
                background = background,
                titleColor = titleColor,
                descColor = descColor,
                onConfirm = onConfirm,
                groupStatus = groupStatus
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
private fun GroupItemDialogScreenView(
    modifier: Modifier = Modifier,
    groupModel: Group,
    background: Color = LocalColor.current.env_80,
    titleColor: Color = LocalColor.current.text.default_100,
    descColor: Color = LocalColor.current.text.default_80,
    onConfirm: (Group, GroupJoinStatus) -> Unit,
    groupStatus: GroupJoinStatus
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

            Spacer(modifier = Modifier.height(35.dp))

            when (groupStatus) {
                GroupJoinStatus.InReview -> {
                    GroupJoinButton(
                        modifier = Modifier.padding(20.dp),
                        text = "等待審核中...",
                        onClick = {
                            onConfirm.invoke(groupModel, groupStatus)
                        }
                    )
                }

                GroupJoinStatus.Joined -> {
                    BorderButton(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                            .height(50.dp),
                        text = "已經加入，進入社團",
                        borderColor = LocalColor.current.text.default_100,
                        onClick = {
                            onConfirm.invoke(groupModel, groupStatus)
                        }
                    )
                }

                GroupJoinStatus.NotJoin -> {
                    BlueButton(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                            .height(50.dp),
                        text = "加入社團",
                        onClick = {
                            onConfirm.invoke(groupModel, groupStatus)
                        }
                    )
                }
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
            groupModel = MockData.mockGroup,
            onConfirm = { group, joinStatus ->
            },
            groupStatus = GroupJoinStatus.Joined
        )
    }
}