package com.cmoney.kolfanci.ui.screens.group.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.OnBottomReached
import com.cmoney.kolfanci.extension.globalGroupViewModel
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.ui.destinations.ApplyForGroupScreenDestination
import com.cmoney.kolfanci.ui.destinations.CreateGroupScreenDestination
import com.cmoney.kolfanci.ui.destinations.MainScreenDestination
import com.cmoney.kolfanci.ui.screens.group.dialog.GroupItemDialogScreen
import com.cmoney.kolfanci.ui.screens.group.search.viewmodel.DiscoverViewModel
import com.cmoney.kolfanci.ui.screens.shared.GroupItemScreen
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun DiscoverGroupScreen(
    navController: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = koinViewModel(),
    groupItems: ArrayList<Group> = arrayListOf(),
    resultRecipient: ResultRecipient<ApplyForGroupScreenDestination, Boolean>
) {
    val uiState = viewModel.uiState
    val globalGroupViewModel = globalGroupViewModel()

    DiscoverGroupScreenView(
        modifier = modifier,
        navController = navController,
        selectedIndex = uiState.tabIndex,
        groupList = uiState.groupList,
        isLoading = uiState.isLoading,
        onTabClick = {
            viewModel.onTabClick(it)
        },
        onGroupItemClick = {
            viewModel.openGroupItemDialog(it)
        },
        onCreateClick = {
            navController.navigate(CreateGroupScreenDestination())
        },
        onLoadMore = {
            viewModel.onLoadMore()
        }
    )

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                val isJoinComplete = result.value
                if (isJoinComplete) {
                    viewModel.closeGroupItemDialog()
                }
            }
        }
    }

    uiState.searchGroupClick?.apply {
        val isJoined = groupItems.any {
            it.id == this.id
        }

        GroupItemDialogScreen(
            isJoined = isJoined,
            groupModel = this,
            background = LocalColor.current.env_80,
            titleColor = LocalColor.current.text.default_100,
            descColor = LocalColor.current.text.default_80,
            joinTextColor = LocalColor.current.primary,
            onDismiss = {
                viewModel.closeGroupItemDialog()
            },
            onConfirm = {
                if (isJoined) {
                    //global change group
                    globalGroupViewModel.setCurrentGroup(it)
                    navController.popBackStack()
                } else {
                    //不公開
                    if (it.isNeedApproval == true) {
                        navController.navigate(
                            ApplyForGroupScreenDestination(
                                group = it
                            )
                        )
                    }
                    //公開
                    else {
                        viewModel.joinGroup(it)
                    }
                }
            }
        )
    }

    if (uiState.joinSuccess != null) {
        globalGroupViewModel.setCurrentGroup(uiState.joinSuccess)
        navController.popBackStack(MainScreenDestination, inclusive = false)
//        navController.popBackStack()
    }

    LaunchedEffect(key1 = uiState.tabIndex) {
        when (uiState.tabIndex) {
            0 -> {
                AppUserLogger.getInstance()
                    .log(page = Page.ExploreGroupPopularGroups)
            }
            1 -> {
                AppUserLogger.getInstance()
                    .log(page = Page.ExploreGroupNewestGroups)
            }
        }
    }
}

@Composable
private fun DiscoverGroupScreenView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    selectedIndex: Int,
    groupList: List<Group>,
    onTabClick: (Int) -> Unit,
    onGroupItemClick: (Group) -> Unit,
    onCreateClick: () -> Unit,
    onLoadMore: () -> Unit,
    isLoading: Boolean
) {
    val list = listOf("熱門社團", "最新社團")

    val listState = rememberLazyListState()

    listState.OnBottomReached {
        onLoadMore.invoke()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBarScreen(
                title = "探索社團",
                leadingIcon = Icons.Filled.Home,
                backClick = {
                    navController.popBackStack()
                }
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(LocalColor.current.primary)
                    .clickable {
                        onCreateClick.invoke()
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = Color.White)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LocalColor.current.env_80)
                .padding(top = 20.dp, bottom = 10.dp, start = 18.dp, end = 18.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedIndex,
                modifier = Modifier
                    .height(40.dp)
                    .padding(1.dp)
                    .clip(RoundedCornerShape(35)),
                indicator = { tabPositions: List<TabPosition> ->
                    Box {}
                },
                backgroundColor = LocalColor.current.env_100
            ) {
                list.forEachIndexed { index, text ->
                    val selected = selectedIndex == index
                    Tab(
                        modifier = if (selected) {
                            Modifier
                                .padding(2.dp)
                                .clip(RoundedCornerShape(35))
                                .background(
                                    LocalColor.current.env_60
                                )
                        } else {
                            Modifier
                                .padding(10.dp)
                                .clip(RoundedCornerShape(35))
                                .background(
                                    Color.Transparent
                                )
                        },
                        selected = selected,
                        onClick = {
                            onTabClick.invoke(index)
                        },
                        text = {
                            Text(
                                text = text,
                                color = LocalColor.current.text.default_100,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                state = listState
            ) {
                items(groupList) { group ->
                    GroupItemScreen(
                        groupModel = group,
                        background = LocalColor.current.background,
                        titleTextColor = LocalColor.current.text.default_100,
                        subTitleColor = LocalColor.current.text.default_50,
                        descColor = LocalColor.current.text.default_80
                    ) { groupModel ->
                        onGroupItemClick.invoke(groupModel)
                    }
                }

                if (isLoading) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(size = 32.dp),
                                color = LocalColor.current.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiscoverGroupPopularScreenPreview() {
    FanciTheme {
        DiscoverGroupScreenView(
            navController = EmptyDestinationsNavigator,
            selectedIndex = 0,
            groupList = listOf(
                Group(
                    name = "Hi"
                )
            ),
            onTabClick = {},
            onGroupItemClick = {},
            onCreateClick = {},
            onLoadMore = {},
            isLoading = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DiscoverGroupLatestScreenPreview() {
    FanciTheme {
        DiscoverGroupScreenView(
            navController = EmptyDestinationsNavigator,
            selectedIndex = 1,
            groupList = listOf(
                Group(
                    name = "Hi"
                )
            ),
            onTabClick = {},
            onGroupItemClick = {},
            onCreateClick = {},
            onLoadMore = {},
            isLoading = true
        )
    }
}