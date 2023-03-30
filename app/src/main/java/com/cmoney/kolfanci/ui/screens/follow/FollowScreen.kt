package com.cmoney.kolfanci.ui.screens.follow

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.MainActivity
import com.cmoney.kolfanci.MainViewModel
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.destinations.ChatRoomScreenDestination
import com.cmoney.kolfanci.destinations.DiscoverGroupScreenDestination
import com.cmoney.kolfanci.destinations.GroupSettingScreenDestination
import com.cmoney.kolfanci.destinations.MyScreenDestination
import com.cmoney.kolfanci.extension.findActivity
import com.cmoney.kolfanci.ui.screens.follow.model.GroupItem
import com.cmoney.kolfanci.ui.screens.follow.viewmodel.FollowViewModel
import com.cmoney.kolfanci.ui.screens.my.MyInfoScreen
import com.cmoney.kolfanci.ui.theme.Black_99000000
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.xlogin.XLoginHelper
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun FollowScreen(
    modifier: Modifier,
    globalViewModel: MainViewModel,
    navigator: DestinationsNavigator,
    viewModel: FollowViewModel = koinViewModel(),
    firstInitData: MutableState<Boolean> = remember {
        mutableStateOf(true)
    }
) {
    val globalUiState = globalViewModel.uiState
    val uiState = viewModel.uiState
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val scrollableState = rememberScrollableState {
        viewModel.scrollOffset(it, density, configuration)
        it
    }

    /**
     * 抓取資料時機點,
     * 登入成功後觸發一次
     * 還未登入
     */
    fun triggerFetchGroup(): Boolean {
        return (globalViewModel.uiState.isFetchFollowData) || (!XLoginHelper.isLogin && uiState.firstFetchData)
    }

    if (triggerFetchGroup()) {
        viewModel.fetchMyGroup()
        if (XLoginHelper.isLogin) {
            globalViewModel.fetchFollowDataDone()
        }
    }

    val group = globalUiState.currentGroup

    if (group == null && uiState.myGroupList.isNotEmpty()) {
        uiState.myGroupList.find {
            it.isSelected
        }?.groupModel?.apply {
            globalViewModel.setCurrentGroup(this)
            firstInitData.value = false
        }
    }

    FollowScreenView(
        navigator = navigator,
        groupList = uiState.myGroupList,
        group = group,
        imageOffset = uiState.imageOffset,
        spaceHeight = uiState.spaceHeight,
        scrollableState = scrollableState,
        visibleAvatar = uiState.visibleAvatar,
        lazyColumnScrollEnabled = uiState.lazyColumnScrollEnabled,
        onGroupItemClick = {
            globalViewModel.setCurrentGroup(it.groupModel)
            viewModel.groupItemClick(it)
        },
        lazyColumnAtTop = {
            viewModel.lazyColumnAtTop()
        },
        isLoading = uiState.isLoading
    )
}

fun LazyListState.isScrolledToTop() = firstVisibleItemScrollOffset == 0

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FollowScreenView(
    navigator: DestinationsNavigator,
    groupList: List<GroupItem>,
    group: Group?,
    imageOffset: Int,
    spaceHeight: Int,
    scrollableState: ScrollableState,
    visibleAvatar: Boolean,
    lazyColumnScrollEnabled: Boolean,
    onGroupItemClick: (GroupItem) -> Unit,
    lazyColumnAtTop: () -> Unit,
    isLoading: Boolean
) {
    val TAG = "FollowScreenView"

    val coroutineScope = rememberCoroutineScope()

    val scaffoldState: ScaffoldState =
        rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))

    val context = LocalContext.current

    val lazyListState = rememberLazyListState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        backgroundColor = LocalColor.current.env_60,
        scaffoldState = scaffoldState,
        drawerContent = if (XLoginHelper.isLogin) {
            {
                DrawerMenuScreen(
                    groupList = groupList,
                    onClick = {
                        //Close Drawer
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }

                        onGroupItemClick.invoke(it)
                    },
                    onSearch = {
                        //Close Drawer
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }

                        val arrayGroupItems = arrayListOf<Group>()
                        arrayGroupItems.addAll(groupList.map {
                            it.groupModel
                        })

                        navigator.navigate(
                            DiscoverGroupScreenDestination(
                                groupItems = arrayGroupItems
                            )
                        )
                    },
                    onProfile = {
                        navigator.navigate(MyScreenDestination)

                        //TODO Deprecate Logout
//                        XLoginHelper.logOut(context)
//                        val intent = Intent(context, MainActivity::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        context.findActivity().finish()
//                        context.startActivity(intent)
                    }
                )
            }
        } else {
            null
        },
        drawerBackgroundColor = Color.Transparent,
        drawerElevation = 0.dp,
        drawerScrimColor = Black_99000000
    ) { innerPadding ->
        //是否登入
        if (XLoginHelper.isLogin) {
            //是否讀取資料中
            if (isLoading) {
                //Loading View
                LoadingView()
            } else {
                //是否已經加入群組
                if (group != null) {
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            //Cover Image
                            AsyncImage(
                                alignment = Alignment.TopCenter,
                                model = group.coverImageUrl,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .offset {
                                        IntOffset(
                                            x = 0,
                                            y = imageOffset
                                        ) //设置偏移量
                                    },
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                placeholder = painterResource(id = R.drawable.placeholder)
                            )

                            //Menu
                            Box(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LocalColor.current.env_80)
                                    .clickable {
                                        //Open Drawer
                                        coroutineScope.launch {
                                            scaffoldState.drawerState.open()
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.menu),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(color = LocalColor.current.env_100)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .scrollable(
                                    state = scrollableState,
                                    orientation = Orientation.Vertical
                                )
                        ) {
                            Spacer(modifier = Modifier
                                .height(
                                    with(LocalDensity.current) {
                                        spaceHeight.toDp()
                                    }
                                )
                                .width(65.dp)
                                .clickable {
                                    //Open Drawer
                                    coroutineScope.launch {
                                        scaffoldState.drawerState.open()
                                    }
                                })
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(850.dp)
                                    .padding(
                                        top = 0.dp,
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 16.dp
                                    ),
                                elevation = 0.dp,
                                shape = RoundedCornerShape(20.dp),
                                backgroundColor = LocalColor.current.env_80
                            ) {
                                // observer when reached end of list
                                val endOfListReached by remember {
                                    derivedStateOf {
                                        lazyListState.isScrolledToTop()
                                    }
                                }

                                LaunchedEffect(endOfListReached) {
                                    lazyColumnAtTop.invoke()
                                }

                                LazyColumn(
                                    state = lazyListState,
                                    userScrollEnabled = lazyColumnScrollEnabled,
                                    verticalArrangement = Arrangement.spacedBy(15.dp),
                                    modifier = Modifier.padding(
                                        start = 20.dp,
                                        end = 20.dp,
                                        top = 20.dp
                                    )
                                ) {
                                    //置頂 縮圖
                                    stickyHeader {
                                        GroupHeaderScreen(
                                            followGroup = group,
                                            visibleAvatar = visibleAvatar,
                                            modifier = Modifier.background(LocalColor.current.env_80)
                                        ) { group ->
                                            navigator.navigate(
                                                GroupSettingScreenDestination(
                                                    initGroup = group
                                                )
                                            )
                                        }
                                    }

                                    //頻道
                                    items(group.categories.orEmpty()) { category ->
                                        CategoryScreen(category = category) { channel ->
                                            KLog.i(TAG, "Category click:$channel")
                                            navigator.navigate(
                                                ChatRoomScreenDestination(
                                                    channelId = channel.id.orEmpty(),
                                                    channelTitle = channel.name.orEmpty()
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    //Empty follow group
                    EmptyFollowScreen(
                        navigator = navigator
                    )
                }
            }
        } else {
            EmptyFollowScreen(
                navigator = navigator
            )
        }
    }
}

@Composable
fun LoadingView() {
    Column(
        modifier = Modifier
            .padding(top = 20.dp, start = 20.dp, end = 20.dp),
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(size = 64.dp),
                color = LocalColor.current.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingViewPreview() {
    FanciTheme {
        LoadingView()
    }
}

@Preview(showBackground = true)
@Composable
fun FollowScreenPreview() {
    FanciTheme {
        FollowScreenView(
            navigator = EmptyDestinationsNavigator,
            groupList = emptyList(),
            group = Group(),
            imageOffset = 0,
            spaceHeight = 0,
            scrollableState = rememberScrollableState {
                it
            },
            visibleAvatar = false,
            lazyColumnScrollEnabled = true,
            onGroupItemClick = {},
            lazyColumnAtTop = {},
            isLoading = true
        )
    }
}
