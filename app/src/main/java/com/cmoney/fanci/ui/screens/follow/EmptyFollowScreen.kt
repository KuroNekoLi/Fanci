package com.cmoney.fanci.ui.screens.follow

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.screens.follow.state.FollowScreenState
import com.cmoney.fanci.ui.screens.follow.state.rememberFollowScreenState
import com.cmoney.fanci.ui.screens.group.dialog.GroupItemDialogScreen
import com.cmoney.fanci.ui.screens.shared.GroupItemScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor

@Composable
fun EmptyFollowScreen(
    followScreenState: FollowScreenState = rememberFollowScreenState(),
    modifier: Modifier = Modifier
) {
    val groupList = followScreenState.viewModel.groupList.observeAsState()

    Column(
        modifier = modifier
            .padding(top = 20.dp, start = 20.dp, end = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            painter = painterResource(id = R.drawable.fanci),
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                color = LocalColor.current.primary
            )
        )
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.3f),
            contentScale = ContentScale.Fit,
            painter = painterResource(id = R.drawable.follow_empty),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "加入Fanci社團跟我們一起快快樂樂！\n立即建立、加入熱門社團",
            fontSize = 14.sp,
            color = LocalColor.current.text.default_100,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(LocalColor.current.primary)
                .clip(RoundedCornerShape(4.dp))
                .clickable {
                    // TODO:
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "建立社團",
                fontSize = 16.sp,
                color = LocalColor.current.text.other,
                textAlign = TextAlign.Center
            )
        }

        groupList.value?.forEach {
            Spacer(modifier = Modifier.height(10.dp))
            GroupItemScreen(
                groupModel = it
            ) { groupModel ->
                followScreenState.openGroupItemDialog(groupModel)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        followScreenState.openGroupDialog.value?.let { group ->
            GroupItemDialogScreen(
                groupModel = group,
                onDismiss = {
                    followScreenState.closeGroupItemDialog()
                },
                onConfirm = {
                    followScreenState.viewModel.joinGroup(it)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyFollowScreenPreview() {
    FanciTheme {
        EmptyFollowScreen()
    }
}