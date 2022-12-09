package com.cmoney.fanci.ui.screens.group.setting.channel

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanciapi.fanci.model.Group
import com.ramcosta.composedestinations.annotation.Destination

//@RootNavGraph(start = true) // sets this as the start destination of the default nav graph
//@Destination
//@Composable
//fun HomeScreen(
//    navigator: DestinationsNavigator
//) {
////    navigator.navigate(ChannelSettingScreenDestination())
//    rememberNavHostEngine()
//}

@Composable
fun ChannelSettingScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
//            TopBarScreen(
//                navController,
//                title = "頻道管理",
//                leadingEnable = true,
//                moreEnable = false
//            )
        }
    ) { innerPadding ->

    }
}

@Preview(showBackground = true)
@Composable
fun ChannelSettingScreenPreview() {
    FanciTheme {
//        ChannelSettingScreen()
    }
}