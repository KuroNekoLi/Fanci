package com.cmoney.kolfanci.ui.screens.group.setting.group.channel.sort

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.globalGroupViewModel
import com.cmoney.kolfanci.ui.screens.shared.toolbar.EditToolbarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Destination
@Composable
fun SortCategoryScreen(
    modifier: Modifier = Modifier,
    group: Group,
    navigator: DestinationsNavigator
) {
    val groupViewModel = globalGroupViewModel()
    SortCategoryScreenView(
        modifier = modifier,
        navigator = navigator,
        categories = group.categories.orEmpty(),
        onSave = {
            groupViewModel.updateCategories(it)
            // TODO 目前不等待結果
            navigator.popBackStack()
        }
    )
}

@Composable
private fun SortCategoryScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    categories: List<Category>,
    onSave: (List<Category>) -> Unit
) {
    val data = remember { mutableStateOf(categories.drop(1)) }
    val state = rememberReorderableLazyListState(onMove = { from, to ->
        data.value = data.value.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    })

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            EditToolbarScreen(
                title = stringResource(id = R.string.sort_category),
                leadingIcon = Icons.Filled.ArrowBack,
                saveClick = {
                    val newList = data.value.toMutableList()
                    newList.add(0, categories[0])
                    onSave.invoke(newList)
                },
                backClick = {
                    navigator.popBackStack()
                }
            )
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {
            if (categories.isNotEmpty()) {
                CategoryItem(
                    category = categories.first(),
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
                colorFilter = ColorFilter.tint(color = LocalColor.current.primary),
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
            categories = listOf(
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