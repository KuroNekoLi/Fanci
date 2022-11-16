package com.cmoney.fanci.ui.screens.group.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.ui.screens.group.dialog.GroupItemDialogScreen
import com.cmoney.fanci.ui.screens.group.search.state.DiscoverGroupState
import com.cmoney.fanci.ui.screens.group.search.state.rememberDiscoverGroupState
import com.cmoney.fanci.ui.screens.shared.GroupItemScreen
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.theme.*

@Composable
fun DiscoverGroupScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    state: DiscoverGroupState = rememberDiscoverGroupState(navController = navController)
) {
    var selectedIndex by remember { mutableStateOf(0) }
    val list = listOf("熱門社團", "最新社團")

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBarScreen(
                state.navController,
                title = "探索社團",
                leadingEnable = true,
                leadingIcon = Icons.Filled.Home,
                trailingEnable = true,
                moreEnable = false,
                backgroundColor = Color_20262F,
                moreClick = null
            )
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
                        onClick = { selectedIndex = index },
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
                items(state.viewModel.uiState.groupList) {
                    GroupItemScreen(
                        groupModel = it,
                        background = Color_0DFFFFFF,
                        titleTextColor = Color.White,
                        subTitleColor = Color_80FFFFFF,
                        descColor = Color_CCFFFFFF
                    ) { groupModel ->
                        state.viewModel.openGroupItemDialog(groupModel)
                    }
                }
            }

            state.viewModel.uiState.searchGroupClick?.apply {
                GroupItemDialogScreen(
                    groupModel = this,
                    background = Color_2B313C,
                    titleColor = Color.White,
                    descColor = Color_CCFFFFFF,
                    joinTextColor = Blue_4F70E5,
                    onDismiss = {
                        state.viewModel.closeGroupItemDialog()
                    },
                    onConfirm = {
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiscoverGroupScreenPreview() {
    FanciTheme {
        DiscoverGroupScreen(navController = rememberNavController())
    }
}