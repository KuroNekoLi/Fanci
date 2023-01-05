package com.cmoney.fanci.ui.screens.group.setting.apply

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.common.BorderButton
import com.cmoney.fanci.ui.screens.group.setting.apply.viewmodel.GroupApplyViewModel
import com.cmoney.fanci.ui.screens.group.setting.apply.viewmodel.GroupRequirementApplySelected
import com.cmoney.fanci.ui.screens.shared.CenterTopAppBar
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupRequirementApply
import com.cmoney.fanciapi.fanci.model.IGroupRequirementAnswer
import com.cmoney.fanciapi.fanci.model.User
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@Destination
@Composable
fun GroupApplyScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    viewModel: GroupApplyViewModel = koinViewModel(),
    group: Group
) {
    val TAG = "GroupApplyScreen"

    val uiState = viewModel.uiState

    if (uiState.applyList == null) {
        viewModel.fetchApplyQuestion(
            groupId = group.id.orEmpty()
        )
    }

    GroupApplyScreenView(
        modifier = modifier,
        navigator = navigator,
        groupRequirementApplyList = uiState.applyList.orEmpty(),
        onApplyClick = {
            KLog.i(TAG, "onApplyClick:$it")
        }
    )
}

@Composable
private fun GroupApplyScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    groupRequirementApplyList: List<GroupRequirementApplySelected>,
    onApplyClick: (GroupRequirementApplySelected) -> Unit
) {
    val TAG = "GroupApplyScreenView"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopAppBar(
                backClick = {
                    navigator.popBackStack()
                },
                onSelectedAll = {
                    KLog.i(TAG, "onSelectedAll click.")
                    // TODO:
                }
            )
        }
    ) {
        Column {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(groupRequirementApplyList) { question ->
                    ApplyQuestionItem(question) {
                        onApplyClick.invoke(it)
                    }
                }
            }

            BottomButton(
                onReject = {
                    // TODO:  
                },
                onApply = {
                    // TODO:
                }
            )
        }


    }
}

@Composable
private fun TopAppBar(
    backClick: (() -> Unit)? = null,
    onSelectedAll: (() -> Unit)? = null
) {
    CenterTopAppBar(
        leading = {
            IconButton(onClick = {
                backClick?.invoke()
            }) {
                Icon(
                    Icons.Filled.ArrowBack, null,
                    tint = Color.White
                )
            }
        },
        title = { Text("加入申請", fontSize = 17.sp, color = Color.White) },
        backgroundColor = LocalColor.current.env_100,
        contentColor = Color.White,
        trailing = {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .offset(x = (-15).dp)
                    .clickable {
                        onSelectedAll?.invoke()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "全選", fontSize = 17.sp, color = LocalColor.current.primary)
            }
        }
    )
}

@Composable
private fun ApplyQuestionItem(
    groupRequirementApplySelected: GroupRequirementApplySelected,
    onClick: (GroupRequirementApplySelected) -> Unit
) {
    val groupRequirementApply = groupRequirementApplySelected.groupRequirementApply
    val user = groupRequirementApply.user

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalColor.current.background)
            .clickable {
                onClick.invoke(groupRequirementApplySelected)
            }
            .padding(top = 20.dp, bottom = 20.dp, start = 24.dp, end = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape),
                model = user?.thumbNail,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.resource_default)
            )

            Spacer(modifier = Modifier.width(15.dp))

            Text(
                text = user?.name.orEmpty(),
                fontSize = 16.sp,
                color = LocalColor.current.text.default_100
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                modifier = Modifier.weight(1f),
                text = user?.serialNumber.toString(),
                fontSize = 12.sp,
                color = LocalColor.current.text.default_50
            )

            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(17.dp)
                        .clip(CircleShape)
                        .background(
                            if (groupRequirementApplySelected.isSelected) {
                                LocalColor.current.primary
                            } else {
                                Color.Transparent
                            }
                        )
                )

                Canvas(modifier = Modifier.size(57.dp)) {
                    drawCircle(
                        color = Color.White,
                        radius = 30f,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }

        }

        groupRequirementApply.answers?.forEach { answer ->
            Text(
                text = answer.question.orEmpty(),
                fontSize = 14.sp,
                color = LocalColor.current.text.default_50
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = answer.answer.orEmpty(),
                fontSize = 14.sp,
                color = LocalColor.current.text.default_100
            )

            Spacer(modifier = Modifier.height(15.dp))
        }

        val date =
            Date(groupRequirementApply.updateUnixTime?.times(10001) ?: System.currentTimeMillis())
        val dayString = SimpleDateFormat("yyyy/MM/dd").format(date)

        Text(
            text = "作答日期：%s".format(dayString),
            fontSize = 12.sp,
            color = LocalColor.current.text.default_30
        )
    }
}

@Composable
private fun BottomButton(
    onReject: () -> Unit,
    onApply: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .background(LocalColor.current.env_100)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row {
            BorderButton(
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp),
                text = "拒絕",
                borderColor = LocalColor.current.text.default_50,
                textColor = LocalColor.current.text.default_100
            ) {
                onReject.invoke()
            }


            Spacer(modifier = Modifier.width(24.dp))

            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = LocalColor.current.primary),
                onClick = {
                    onApply.invoke()
                }) {
                Text(
                    text = "批准加入",
                    color = LocalColor.current.text.other,
                    fontSize = 16.sp
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GroupApplyScreenPreview() {
    FanciTheme {
        GroupApplyScreenView(
            navigator = EmptyDestinationsNavigator,
            groupRequirementApplyList = listOf(
                GroupRequirementApplySelected(
                    groupRequirementApply = GroupRequirementApply(
                        user = User(name = "Name", serialNumber = 123456),
                        answers = listOf(
                            IGroupRequirementAnswer(
                                question = "Q1：金針菇是哪國人？",
                                answer = "\uD83C\uDDF0\uD83C\uDDF7韓國"
                            ),
                            IGroupRequirementAnswer(
                                question = "Q2：金針菇第一隻破十萬觀看數的影片是哪一隻呢？（敘述即可不用寫出全名）",
                                answer = "金針菇快閃韓國，回家嚇到爸媽"
                            )
                        )
                    ),
                    isSelected = true
                )
            ),
            onApplyClick = {
            }
        )
    }
}