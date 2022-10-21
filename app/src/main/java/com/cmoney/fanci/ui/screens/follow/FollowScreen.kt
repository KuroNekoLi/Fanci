package com.cmoney.fanci.ui.screens.follow

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.screens.shared.ChannelBar
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FollowScreen(
    viewModel: FollowViewModel = viewModel(),
    onChannelClick: ((channelBar: ChannelBar) -> Unit)?
) {
    val followCategoryList = viewModel.followData.observeAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colors.surface
    ) {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp

        // ToolBar 最大向上位移量
        val maxUpPx = with(LocalDensity.current) { screenWidth.roundToPx().toFloat() + 10 }
        // ToolBar 最小向上位移量
        val minUpPx = 0f

        // 偏移折叠工具栏上移高度
        val imageOffsetHeightPx = remember { mutableStateOf(0f) }

        //Space Height
        val maxSpaceUpPx = with(LocalDensity.current) { 190.dp.roundToPx().toFloat() }
        val spaceOffsetHeightPx = remember { mutableStateOf(maxSpaceUpPx) }

        var visibleAvatar by remember { mutableStateOf(false) }

        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    val delta = available.y

                    //位移偏差值
                    val offsetVariable = 2.2f
                    val newOffset = imageOffsetHeightPx.value + delta * offsetVariable
                    imageOffsetHeightPx.value = newOffset.coerceIn(-maxUpPx, -minUpPx)

                    val newSpaceOffset = spaceOffsetHeightPx.value + delta
                    spaceOffsetHeightPx.value = newSpaceOffset.coerceIn(0f, maxSpaceUpPx)

                    //是否顯示 大頭貼
                    visibleAvatar = spaceOffsetHeightPx.value <= 10f

                    return Offset.Zero
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
        ) {
            AsyncImage(
                alignment = Alignment.TopCenter,
                model = "https://picsum.photos/400/400",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .offset {
                        IntOffset(x = 0, y = imageOffsetHeightPx.value.roundToInt()) //设置偏移量
                    },
                contentScale = ContentScale.Crop,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.resource_default)
            )

            Column {
                Spacer(modifier = Modifier.height(
                    with(LocalDensity.current) { spaceOffsetHeightPx.value.toDp() }
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
                                    visibleAvatar
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
    FollowScreen {

    }
}
