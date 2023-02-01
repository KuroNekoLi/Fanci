package com.cmoney.fanci.ui.screens.group.setting.group.channel.sort

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanciapi.fanci.model.Group
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

// TODO:  
@Destination
@Composable
fun SortChannelScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group
) {

}

@Composable
private fun SortChannelScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator
) {
//    var list by remember { mutableStateOf(listOf("A", "B", "C", "D", "E", "F", "G")) }
//    LazyColumn {
//        item {
//            Button(onClick = { list = list.shuffled() }) {
//                Text("Shuffle")
//            }
//        }
//
//        items(list, key = { it }) {t ->
//            Text("Item $t", Modifier.animateItemPlacement())
//        }
//    }
}


@Preview(showBackground = true)
@Composable
fun SortChannelScreenPreview() {
    FanciTheme {
        SortChannelScreenView(
            navigator = EmptyDestinationsNavigator
        )
    }


}