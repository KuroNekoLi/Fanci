package com.cmoney.kolfanci.ui.screens.group.setting.group.channel.sort

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.common.CategoryText
import com.cmoney.kolfanci.ui.common.ChannelText
import com.cmoney.kolfanci.ui.screens.group.setting.group.channel.viewmodel.ChannelSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.toolbar.EditToolbarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun SortChannelScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    viewModel: ChannelSettingViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<Group>
) {
    val uiState = viewModel.uiState

    val categoryList = if (uiState.group != null) {
        uiState.group.categories.orEmpty()
    } else {
        group.categories.orEmpty()
    }

    key(uiState.group?.categories) {
        SortChannelScreenView(
            modifier = modifier,
            navigator = navigator,
            categoryList = categoryList,
            moveCallback = {
                viewModel.sortChannel(it)
            },
            onSave = {
                AppUserLogger.getInstance().log(Clicked.Confirm, From.ChannelOrder)
                viewModel.onSortCategoryOrChannel(
                    group = group,
                    categories = it
                )
            }
        )
    }

    if (uiState.group == null) {
        viewModel.setGroup(group)
    }

    if (uiState.isSoredToServerComplete && uiState.group != null) {
        resultNavigator.navigateBack(uiState.group)
    }
}

@Composable
private fun SortChannelScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    categoryList: List<Category>,
    moveCallback: (MoveItem) -> Unit,
    onSave: (List<Category>) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            EditToolbarScreen(
                title = stringResource(id = R.string.sort_channel),
                leadingIcon = Icons.Filled.ArrowBack,
                saveClick = {
                    onSave.invoke(categoryList)
                },
                backClick = {
                    navigator.popBackStack()
                }
            )
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {
            LongPressDraggable(
                modifier = Modifier
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .background(LocalColor.current.env_80)
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(categoryList) { category ->
                        CategoryItem(category = category) {
                            moveCallback.invoke(it)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Record move item
 */
data class MoveItem(
    val fromCategory: Category,
    val toCategory: Category?,
    val channel: Channel
)

@Composable
private fun CategoryItem(
    modifier: Modifier = Modifier,
    category: Category,
    moveCallback: (MoveItem) -> Unit
) {
    val channelList = category.channels.orEmpty()

    DropTarget<MoveItem>(
        modifier = modifier
            .background(LocalColor.current.background)
            .fillMaxSize()
    ) { isInBound, moveItem, currentDropTargetPosition ->

        val bgColor = if (isInBound) Color.DarkGray else LocalColor.current.background

        Column(
            modifier = modifier
                .background(bgColor)
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            //分類
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryText(
                    modifier = Modifier.weight(1f),
                    text = if (!category.name.isNullOrEmpty()) {
                        category.name.orEmpty()
                    } else {
                        "（不分類頻道）"
                    }
                )
            }

            //Channel List
            repeat(channelList.size) { pos ->
                DragTarget(
                    modifier = Modifier.wrapContentSize(),
                    dataToDrop = MoveItem(
                        fromCategory = category,
                        channel = channelList[pos],
                        toCategory = null
                    )
                ) {
                    ChannelBarScreen(
                        channel = channelList[pos],
                        horizontalPadding = 0.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }


        if (isInBound && moveItem != null) {
            val finalMoveItem = moveItem.copy(
                toCategory = category
            )
            finalMoveItem.let {
                moveCallback.invoke(it)
            }
        }
    }
}

@Composable
private fun ChannelBarScreen(
    channel: Channel,
    horizontalPadding: Dp = 14.dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = horizontalPadding, vertical = 7.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(LocalColor.current.background),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp)
        ) {
            Icon(
                painterResource(id = R.drawable.message),
                contentDescription = null,
                tint = LocalColor.current.component.other
            )
            Spacer(modifier = Modifier.width(14.dp))
            ChannelText(
                modifier = Modifier.weight(1f),
                channel.name.orEmpty()
            )

            Image(
                modifier = Modifier.padding(end = 14.dp),
                painter = painterResource(id = R.drawable.menu),
                colorFilter = ColorFilter.tint(color = LocalColor.current.primary),
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChannelBarPreview() {
    FanciTheme {
        ChannelBarScreen(
            channel = Channel(
                name = "Channel1"
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SortChannelScreenPreview() {
    FanciTheme {
        SortChannelScreenView(
            navigator = EmptyDestinationsNavigator,
            categoryList = listOf(
                Category(
                    name = "1",
                    channels = listOf(
                        Channel(name = "1"),
                        Channel(name = "2"),
                        Channel(name = "3")
                    )
                ),
                Category(
                    name = "2",
                    channels = listOf(
                        Channel(name = "4"),
                        Channel(name = "5"),
                        Channel(name = "6")
                    )
                ),
                Category(
                    name = "3",
                    channels = listOf(
                        Channel(name = "7"),
                        Channel(name = "8"),
                        Channel(name = "9")
                    )
                )
            ),
            moveCallback = {},
            onSave = {}
        )
    }


}