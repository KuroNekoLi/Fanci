package com.cmoney.fanci.ui.screens.group.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.R
import com.cmoney.fanci.destinations.CreateGroupScreenDestination
import com.cmoney.fanci.ui.screens.group.dialog.GroupItemDialogScreen
import com.cmoney.fanci.ui.screens.group.search.viewmodel.DiscoverViewModel
import com.cmoney.fanci.ui.screens.shared.GroupItemScreen
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.theme.*
import com.cmoney.fanciapi.fanci.model.Group
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun DiscoverGroupScreen(
    navController: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState

    DiscoverGroupScreenView(
        modifier = modifier,
        navController = navController,
        selectedIndex = uiState.tabIndex,
        groupList = uiState.groupList,
        onTabClick = {
            viewModel.onTabClick(it)
        },
        onGroupItemClick = {
            viewModel.openGroupItemDialog(it)
        },
        onCreateClick = {
            navController.navigate(CreateGroupScreenDestination())
        }
    )

    uiState.searchGroupClick?.apply {
        GroupItemDialogScreen(
            groupModel = this,
            background = Color_2B313C,
            titleColor = Color.White,
            descColor = Color_CCFFFFFF,
            joinTextColor = Blue_4F70E5,
            onDismiss = {
                viewModel.closeGroupItemDialog()
            },
            onConfirm = {
                viewModel.joinGroup(it)
            }
        )
    }

    if (uiState.joinSuccess) {
        navController.popBackStack()
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
    onCreateClick: () -> Unit
) {
    val list = listOf("熱門社團", "最新社團")

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBarScreen(
                title = "探索社團",
                leadingEnable = true,
                leadingIcon = Icons.Filled.Home,
                trailingEnable = true,
                moreEnable = false,
                backgroundColor = Color_20262F,
                moreClick = {
                },
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
                .background(Black_2B313C)
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
                backgroundColor = Color_20262F
            ) {
                list.forEachIndexed { index, text ->
                    val selected = selectedIndex == index
                    Tab(
                        modifier = if (selected) Modifier
                            .padding(2.dp)
                            .clip(RoundedCornerShape(35))
                            .background(
                                Color_303744
                            )
                        else Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(35))
                            .background(
                                Color.Transparent
                            ),
                        selected = selected,
                        onClick = {
                            onTabClick.invoke(index)
                        },
                        text = {
                            Text(
                                text = text, color = Color.White, fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                items(groupList) { group ->
                    GroupItemScreen(
                        groupModel = group,
                        background = Color_0DFFFFFF,
                        titleTextColor = Color.White,
                        subTitleColor = Color_80FFFFFF,
                        descColor = Color_CCFFFFFF
                    ) { groupModel ->
                        onGroupItemClick.invoke(groupModel)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiscoverGroupScreenPreview() {
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
            onCreateClick = {}
        )
    }
}