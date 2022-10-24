package com.cmoney.fanci.ui.screens.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cmoney.fanci.ui.theme.FanciTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 聊天室 區塊
 */
@Composable
fun MessageScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    viewModel: ChatRoomViewModel = viewModel()
) {
    Surface(
        color = MaterialTheme.colors.surface,
        modifier = modifier,
    ) {
        val followCategoryList = viewModel.message.observeAsState()

        val listState = rememberLazyListState()

        LazyColumn(state = listState) {
            followCategoryList.value?.apply {
                items(this) { message ->
                    MessageContentScreen(message)
                }
            }
        }

        LaunchedEffect(true) {
            CoroutineScope(Dispatchers.Main).launch {
                listState.scrollToItem(index = followCategoryList.value?.size ?: 0)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageScreenPreview() {
    FanciTheme {
        MessageScreen()
    }
}