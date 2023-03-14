package com.cmoney.kolfanci

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.Channel
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel


/**
 * Just Test
 */
@Composable
fun DragBetweenListScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState
    key(uiState.testCategory) {
        DragBetweenListScreenView(
            categoryList = uiState.testCategory
        ) {
            viewModel.sortCallback(it)
        }
    }
}

@Composable
fun DragBetweenListScreenView(
    modifier: Modifier = Modifier,
    categoryList: List<Category>,
    sortedCallback: (List<Category>) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "重新排列",
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                }
            )
        }
    ) { padding ->
        LongPressDraggable(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .background(LocalColor.current.env_80)
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(categoryList) { category ->
                    Category(category) { moveItem ->
                        if (moveItem.fromCategory.id != moveItem.toCategory?.id) {
                            val removedList = categoryList.map { category ->
                                //remove old item
                                if (category.id == moveItem.fromCategory.id) {
                                    val removeChannel = category.channels?.filter { channel ->
                                        channel.id != moveItem.channel.id
                                    }
                                    category.copy(
                                        channels = removeChannel
                                    )
                                } else {
                                    category
                                }
                            }

                            //add new item
                            val finalCategoryList = removedList.map { category ->
                                if (category == moveItem.toCategory) {
                                    val newChannel = category.channels?.toMutableList()
                                    newChannel?.add(moveItem.channel)
                                    category.copy(
                                        channels = newChannel?.distinctBy {
                                            it.id
                                        }
                                    )
                                } else {
                                    category
                                }
                            }

                            KLog.i("Warren", finalCategoryList)
//                            categoryList = finalCategoryList
                            sortedCallback.invoke(finalCategoryList)
                        }
                    }
                }
            }
        }
    }
}

data class MoveItem(
    val fromCategory: Category,
    val toCategory: Category?,
    val channel: Channel
)

@Composable
private fun Category(category: Category, moveCallback: (MoveItem) -> Unit) {
    DropTarget<MoveItem>(
        modifier = Modifier
            .background(Color.DarkGray)
            .fillMaxWidth()
            .padding(24.dp)
    ) { isInBound, moveItem, currentDropTargetPosition ->
        val bgColor = if (isInBound) Color.Red else Color.DarkGray
//        val bgColor = Color.DarkGray
        Column(
            modifier = Modifier.background(bgColor)
        ) {
            Text(text = category.name.orEmpty())
            category.channels?.let { channels ->
                repeat(channels.size) { pos ->
                    val channel = channels[pos]
                    KLog.i("Warren", "category:" + category.id + "  channel:" + channel.id)
                    DragTarget(
                        modifier = Modifier.wrapContentSize(),
                        dataToDrop = MoveItem(
                            fromCategory = category,
                            channel = channel,
                            toCategory = null
                        )
                    ) {
                        ChannelItem(channel)
                    }
                }
            }
        }

        if (isInBound && moveItem != null) {
            val finalMoveItem = moveItem.copy(
                toCategory = category
            )
            KLog.i(
                "Warren",
                "MoveItem from: " + finalMoveItem.fromCategory?.id
                        + " to:" + finalMoveItem.toCategory?.id
                        + " item:" + finalMoveItem.channel
            )
            finalMoveItem.let {
                moveCallback.invoke(it)
            }
        }

    }
}

@Composable
private fun ChannelItem(channel: Channel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 10.dp, vertical = 7.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(LocalColor.current.background),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = channel.name.orEmpty())
    }
}

internal class DragTargetInfo {
    var isDragging: Boolean by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var dataToDrop by mutableStateOf<Any?>(null)
}

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

@Composable
fun <T> DragTarget(
    modifier: Modifier,
    dataToDrop: T,
    content: @Composable (() -> Unit)
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val currentState = LocalDragTargetInfo.current

    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition = it.localToWindow(Offset.Zero)
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(onDragStart = {
                currentState.dataToDrop = dataToDrop
                currentState.isDragging = true
                currentState.dragPosition = currentPosition + it
                currentState.draggableComposable = content
            }, onDrag = { change, dragAmount ->
                change.consumeAllChanges()
                currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)
            }, onDragEnd = {
                currentState.isDragging = false
                currentState.dragOffset = Offset.Zero
            }, onDragCancel = {
                currentState.dragOffset = Offset.Zero
                currentState.isDragging = false
            })
        }) {
        content()
    }
}

@Composable
fun LongPressDraggable(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val state = remember { DragTargetInfo() }
    CompositionLocalProvider(
        LocalDragTargetInfo provides state
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            content()
            if (state.isDragging) {
                var targetSize by remember {
                    mutableStateOf(IntSize.Zero)
                }
                Box(modifier = Modifier
                    .graphicsLayer {
                        val offset = (state.dragPosition + state.dragOffset)
                        scaleX = 0.9f
                        scaleY = 0.9f
                        alpha = if (targetSize == IntSize.Zero) 0f else .9f
                        translationX = offset.x.minus(targetSize.width / 2)
                        //Remove TopBar height
                        translationY = offset.y.minus(targetSize.height / 2 + 250.dp.value)
                    }
                    .onGloballyPositioned {
                        targetSize = it.size
                    }
                ) {
                    state.draggableComposable?.invoke()
//                    val elevation = animateDpAsState(16.dp)
//                    Column(
//                        modifier = Modifier
//                            .shadow(elevation.value)
//                    ) {
//                        state.draggableComposable?.invoke()
//                    }
                }
            }
        }
    }
}

@Composable
fun <T> DropTarget(
    modifier: Modifier,
    content: @Composable() (BoxScope.(isInBound: Boolean, data: T?, currentDropTargetPosition: Offset) -> Unit)
) {
    val dragInfo = LocalDragTargetInfo.current
    val dragPosition = dragInfo.dragPosition
    val dragOffset = dragInfo.dragOffset
    var isCurrentDropTarget by remember {
        mutableStateOf(false)
    }

    var currentDropTargetPosition by remember {
        mutableStateOf(Offset.Zero)
    }

    Box(modifier = modifier.onGloballyPositioned {
        it.boundsInWindow().let { rect ->
            currentDropTargetPosition = dragPosition + dragOffset
            isCurrentDropTarget = rect.contains(currentDropTargetPosition)
        }
    }) {
        val data =
            if (isCurrentDropTarget && !dragInfo.isDragging) dragInfo.dataToDrop as T? else null

        content(isCurrentDropTarget, data, currentDropTargetPosition)

        if (isCurrentDropTarget && !dragInfo.isDragging) {
            dragInfo.dataToDrop = null
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DragBetweenListScreenPreview() {
    FanciTheme {
        DragBetweenListScreenView(
            categoryList = listOf(
                Category(
                    id = "1",
                    name = "Title1",
                    channels = listOf(
                        Channel(
                            id = "1",
                            name = "Channel1"
                        ),
                        Channel(
                            id = "2",
                            name = "Channel2"
                        ),
                        Channel(
                            id = "3",
                            name = "Channel3"
                        )
                    )
                ),
                Category(
                    id = "2",
                    name = "Title2",
                    channels = listOf(
                        Channel(
                            id = "4",
                            name = "Channel4"
                        ),
                        Channel(
                            id = "5",
                            name = "Channel5"
                        ),
                        Channel(
                            id = "6",
                            name = "Channel6"
                        )
                    )
                )
            )
        ){}
    }
}