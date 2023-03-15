package com.cmoney.kolfanci.ui.screens.shared.member

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.OnBottomReached
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.GroupMemberSelect
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.MemberViewModel
import com.cmoney.kolfanci.ui.screens.shared.setting.BottomButtonScreen
import com.cmoney.kolfanci.ui.screens.shared.snackbar.CustomMessage
import com.cmoney.kolfanci.ui.screens.shared.snackbar.FanciSnackBarScreen
import com.cmoney.kolfanci.ui.theme.Color_80FFFFFF
import com.cmoney.kolfanci.ui.theme.Color_99FFFFFF
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

/**
 * 搜尋所有成員畫面,並將勾選成員 callback
 */
@Destination
@Composable
fun AddMemberScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    excludeMember: Array<GroupMember> = emptyArray(),
    viewModel: MemberViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<String>,
    title: String = "新增成員",
    subTitle: String = "直接指定成員進入私密頻道。",
    btnTitle: String = "新增"
) {
    val TAG = "AddMemberScreen"

    val uiState = viewModel.uiState

    AddMemberScreenPreview(
        modifier = modifier,
        navigator = navigator,
        title = title,
        subTitle = subTitle,
        btnTitle = btnTitle,
        groupMember = uiState.groupMember ?: emptyList(),
        onMemberClick = {
            viewModel.onMemberClick(it)
        },
        onAddClick = {
            viewModel.onAddSelectedMember()
        },
        onLoadMore = {
            viewModel.onLoadMoreGroupMember(group.id.orEmpty())
        },
        onSearch = {
            viewModel.onSearchMember(it)
        },
        onBack = {
            resultNavigator.navigateBack(
                result = viewModel.fetchSelectedMember()
            )
        }
    )

    //抓取 會員資料
    if (uiState.groupMember == null) {
        viewModel.fetchGroupMember(
            groupId = group.id.orEmpty(),
            excludeMember = excludeMember.toList()
        )
    }

    //Toast
    if (uiState.showAddSuccessTip) {
        FanciSnackBarScreen(
            modifier = Modifier.padding(bottom = 70.dp),
            message = CustomMessage(
                textString = "成員新增成功！",
                iconRes = R.drawable.all_member,
                iconColor = Color_99FFFFFF,
                textColor = Color.White
            )
        ) {
            viewModel.dismissAddSuccessTip()
        }
    }

    BackHandler {
        KLog.i(TAG, "BackHandler")
        resultNavigator.navigateBack(
            result = viewModel.fetchSelectedMember()
        )
    }

    //Loading progress
    if (uiState.loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(size = 32.dp),
                color = LocalColor.current.primary
            )
        }
    }

    //Deprecate
//    val lifecycleOwner = LocalLifecycleOwner.current
//    DisposableEffect(lifecycleOwner) {
//        val observer = LifecycleEventObserver { _, event ->
//            when (event) {
//                Lifecycle.Event.ON_START -> {
//                    KLog.i(TAG, "ON_START")
//                }
//                Lifecycle.Event.ON_STOP -> {
//                    KLog.i(TAG, "ON_STOP")
//                    resultNavigator.navigateBack(
//                        result = viewModel.fetchSelectedMember()
//                    )
//                }
//                else -> {
//                }
//            }
//        }
//
//        lifecycleOwner.lifecycle.addObserver(observer)
//
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//        }
//    }
}

@Composable
private fun AddMemberScreenPreview(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    groupMember: List<GroupMemberSelect>,
    onMemberClick: (GroupMemberSelect) -> Unit,
    onAddClick: () -> Unit,
    onLoadMore: () -> Unit,
    onSearch: (String) -> Unit,
    onBack: () -> Unit,
    title: String,
    subTitle: String,
    btnTitle: String
) {
    val TAG = "AddMemberScreenPreview"
    val listState: LazyListState = rememberLazyListState()
    var textState by remember { mutableStateOf("") }
    val maxLength = 20

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = title,
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    onBack.invoke()
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .background(LocalColor.current.env_80)
                .padding(padding)
        ) {

            Column(
                modifier = Modifier.padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 20.dp)
            ) {

                Text(text = subTitle, fontSize = 14.sp, color = Color_80FFFFFF)

                Spacer(modifier = Modifier.height(20.dp))

                //Search bar
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    value = textState,
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = LocalColor.current.text.default_100,
                        backgroundColor = LocalColor.current.background,
                        cursorColor = LocalColor.current.primary,
                        disabledLabelColor = LocalColor.current.text.default_30,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    onValueChange = {
                        if (it.length <= maxLength) {
                            textState = it
                            onSearch.invoke(it)
                        }
                    },
                    shape = RoundedCornerShape(4.dp),
                    maxLines = 1,
                    textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                    placeholder = {
                        Text(
                            text = "輸入名稱搜尋成員",
                            fontSize = 16.sp,
                            color = LocalColor.current.text.default_30
                        )
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.member_search),
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (textState.isNotEmpty()) {
                            Image(
                                modifier = Modifier.clickable {
                                    textState = ""
                                },
                                painter = painterResource(id = R.drawable.clear),
                                contentDescription = null
                            )
                        }
                    }
                )

            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(groupMember) { member ->
                    MemberItem(member) {
                        onMemberClick.invoke(it)
                    }
                }
            }

            BottomButtonScreen(
                text = btnTitle
            ) {
                KLog.i(TAG, "on save click.")
                onAddClick.invoke()
            }
        }

        listState.OnBottomReached {
            onLoadMore.invoke()
        }
    }
}


@Composable
private fun MemberItem(
    groupMemberSelect: GroupMemberSelect,
    onMemberClick: (GroupMemberSelect) -> Unit
) {
    val groupMember = groupMemberSelect.groupMember
    Row(
        modifier = Modifier
            .background(LocalColor.current.background)
            .clickable {
                onMemberClick.invoke(groupMemberSelect)
            }
            .padding(start = 30.dp, top = 8.dp, bottom = 8.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape),
            model = groupMember.thumbNail.orEmpty(),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.placeholder)
        )

        Spacer(modifier = Modifier.width(15.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = groupMember.name.orEmpty(),
                fontSize = 16.sp,
                color = LocalColor.current.text.default_100
            )
            Text(
                text = groupMember.serialNumber.toString(),
                fontSize = 12.sp,
                color = LocalColor.current.text.default_50
            )
        }

        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(17.dp)
                    .clip(CircleShape)
                    .background(
                        if (groupMemberSelect.isSelected) {
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
}

@Preview(showBackground = true)
@Composable
fun AddMemberScreen() {
    FanciTheme {
        AddMemberScreenPreview(
            modifier = Modifier,
            navigator = EmptyDestinationsNavigator,
            groupMember = listOf(
                GroupMemberSelect(
                    groupMember = GroupMember(name = "A1", serialNumber = 123, thumbNail = ""),
                ),
                GroupMemberSelect(
                    groupMember = GroupMember(name = "A2", serialNumber = 456, thumbNail = ""),
                ),
                GroupMemberSelect(
                    groupMember = GroupMember(name = "A3", serialNumber = 789, thumbNail = ""),
                )
            ),
            onMemberClick = {},
            onAddClick = {},
            onLoadMore = {},
            onSearch = {},
            onBack = {},
            title = "新增成員",
            subTitle = "直接指定成員進入私密頻道。",
            btnTitle = "新增"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MemberItemPreview() {
    FanciTheme {
        MemberItem(
            groupMemberSelect = GroupMemberSelect(
                groupMember = GroupMember(
                    name = "Hello",
                    serialNumber = 12345
                ),
                isSelected = true
            )
        ) {}
    }
}