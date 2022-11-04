package com.cmoney.fanci.ui.screens.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.cmoney.fanci.ui.screens.group.state.DiscoverGroupState
import com.cmoney.fanci.ui.screens.group.state.rememberDiscoverGroupState
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.theme.Black_2B313C
import com.cmoney.fanci.ui.theme.FanciTheme
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
                .background(MaterialTheme.colors.surface)
                .padding(top = 20.dp, bottom = 10.dp, start = 18.dp, end = 18.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedIndex,
                modifier = Modifier
                    .height(40.dp)
                    .clip(RoundedCornerShape(20))
                    .padding(1.dp),
                indicator = { tabPositions: List<TabPosition> ->
                    Box {}
                },
                backgroundColor = Black_2B313C
            ) {
                list.forEachIndexed { index, text ->
                    val selected = selectedIndex == index
                    Tab(
                        modifier = if (selected) Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(20))
                            .background(
                                White_494D54
                            )
                        else Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(20))
                            .background(
                                Color.Transparent
                            ),
                        selected = selected,
                        onClick = { selectedIndex = index },
                        text = { Text(text = text, color = Color.White, fontSize = 14.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                items(10) {
                    GroupItemScreen {
                        state.openGroupItemDialog()
                    }
                }
            }

            if (state.openGroupDialog.value) {
                GroupItemDialogScreen(
                    onDismiss = {
                        state.closeGroupItemDialog()
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