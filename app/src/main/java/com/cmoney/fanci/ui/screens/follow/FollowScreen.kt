package com.cmoney.fanci.ui.screens.follow

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.screens.follow.state.FollowScreenState
import com.cmoney.fanci.ui.screens.follow.state.rememberFollowScreenState
import com.cmoney.fanci.ui.screens.shared.ChannelBar
import com.cmoney.fanci.ui.theme.Black_282A2D
import com.cmoney.fanci.ui.theme.Black_99000000
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FollowScreen(
    followScreenState: FollowScreenState = rememberFollowScreenState(),
    onChannelClick: ((channelBar: ChannelBar) -> Unit)?,
    onSearchClick: () -> Unit
) {
    val followCategoryList = followScreenState.viewModel.followData.observeAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface),
        scaffoldState = followScreenState.scaffoldState,
        drawerContent = {
            DrawerMenuScreen(
                onClick = {
                    followScreenState.closeDrawer()
                },
                onSearch = {
                    onSearchClick.invoke()
                }
            )
        },
        drawerBackgroundColor = Color.Transparent,
        drawerElevation = 0.dp,
        drawerScrimColor = Black_99000000
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .nestedScroll(followScreenState.nestedScrollConnection)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                //Cover Image
                AsyncImage(
                    alignment = Alignment.TopCenter,
                    model = "https://picsum.photos/400/400",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = followScreenState.imageOffsetHeightPx.value.roundToInt()
                            ) //设置偏移量
                        },
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    placeholder = painterResource(id = R.drawable.resource_default)
                )

                //Menu
                Box(
                    modifier = Modifier
                        .padding(20.dp)
                        .size(45.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Black_282A2D)
                        .clickable {
                            followScreenState.openDrawer()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.menu),
                        contentDescription = null
                    )
                }
            }

            Column {
                Spacer(modifier = Modifier.height(
                    with(LocalDensity.current) { followScreenState.spaceOffsetHeightPx.value.toDp() }
                ))
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
                    backgroundColor = MaterialTheme.colors.onSecondary
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)
                    ) {

                        stickyHeader {
                            followCategoryList.value?.let {
                                GroupHeaderScreen(
                                    FollowGroup(
                                        groupName = it.groupName,
                                        groupAvatar = it.avatar
                                    ),
                                    modifier = Modifier.background(
                                        MaterialTheme.colors.onSecondary
                                    ),
                                    followScreenState.visibleAvatar
                                )
                            }
                        }

                        followCategoryList.value?.let {
                            items(it.category) { category ->
                                CategoryScreen(followCategory = category) {
                                    onChannelClick?.invoke(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FollowScreenPreview() {
    FollowScreen(
        onChannelClick = {}
    ) {

    }
}
