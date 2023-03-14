package com.cmoney.kolfanci.ui.screens.group.setting.group.channel.sort

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.screens.group.setting.group.channel.viewmodel.ChannelSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.theme.Blue_4F70E5
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.Group
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.koin.androidx.compose.koinViewModel
import com.cmoney.kolfanci.R
@Destination
@Composable
fun SortCategoryScreen(
    modifier: Modifier = Modifier,
    group: Group,
    navigator: DestinationsNavigator,
    viewModel: ChannelSettingViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<Group>
) {
    val uiState = viewModel.uiState

    SortCategoryScreenView(
        modifier = modifier,
        navigator = navigator,
        category = group.categories.orEmpty(),
        onSave = {
            viewModel.onSortCategoryOrChannel(group, it)
        }
    )

    if (uiState.group != null) {
        resultNavigator.navigateBack(uiState.group)
    }
}

@Composable
private fun SortCategoryScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    category: List<Category>,
    onSave: (List<Category>) -> Unit
) {
    val data = remember { mutableStateOf(category.drop(1)) }
    val state = rememberReorderableLazyListState(onMove = { from, to ->
        data.value = data.value.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    })

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "分類排序",
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    navigator.popBackStack()
                }
            )
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {
            if (category.isNotEmpty()) {
                CategoryItem(
                    category = category.first(),
                    withSortIcon = false
                )

                Spacer(modifier = Modifier.height(1.dp))

                LazyColumn(
                    state = state.listState,
                    modifier = Modifier
                        .weight(1f)
                        .reorderable(state)
                        .detectReorderAfterLongPress(state),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(data.value, { it }) { item ->
                        ReorderableItem(state, key = item) { isDragging ->
                            val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
                            Column(
                                modifier = Modifier
                                    .shadow(elevation.value)
                            ) {
                                CategoryItem(
                                    category = item
                                )
                            }
                        }
                    }
                }

                //========== 儲存 ==========
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(135.dp)
                        .background(LocalColor.current.env_100),
                    contentAlignment = Alignment.Center
                ) {
                    BlueButton(text = "儲存") {
                        val newList = data.value.toMutableList()
                        newList.add(0, category[0])
                        onSave.invoke(newList)
                    }
                }
            }
        }

    }
}

@Composable
private fun CategoryItem(
    category: Category,
    withSortIcon: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalColor.current.background)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            modifier = Modifier.weight(1f),
            text = category.name.orEmpty().ifEmpty {
                "（不分類頻道）"
            },
            fontSize = 14.sp,
            color = LocalColor.current.text.default_80
        )

        if (withSortIcon) {
            Image(
                modifier = Modifier.padding(end = 24.dp),
                painter = painterResource(id = R.drawable.menu),
                colorFilter = ColorFilter.tint(color = Blue_4F70E5),
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryItemPreview() {
    FanciTheme {
        CategoryItem(
            Category(
                name = "123"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SortCategoryScreenPreview() {
    FanciTheme {
        SortCategoryScreenView(
            navigator = EmptyDestinationsNavigator,
            category = listOf(
                Category(
                    name = "1"
                ),
                Category(
                    name = "2"
                ),
                Category(
                    name = "3"
                )
            ),
            onSave = {}
        )
    }
}