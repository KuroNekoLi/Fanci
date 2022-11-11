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
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.theme.Black_2B313C
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanci.ui.theme.White_494D54

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
                moreClick = null
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
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
                        modifier = if (selected) Modifier
                            .padding(2.dp)
                            .clip(RoundedCornerShape(35))
                            .background(
                                LocalColor.current.env_60
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
                                text = text, color =
                                if (selected) {
                                    LocalColor.current.text.default_100
                                } else {
                                    LocalColor.current.text.other
                                }, fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                items(state.viewModel.uiState.groupList) {
                    GroupItemScreen(
                        groupModel = it
                    ) { groupModel ->
                        state.viewModel.openGroupItemDialog(groupModel)
                    }
                }
            }

            state.viewModel.uiState.searchGroupClick?.apply {
                GroupItemDialogScreen(
                    groupModel = this,
                    onDismiss = {
                        state.viewModel.closeGroupItemDialog()
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