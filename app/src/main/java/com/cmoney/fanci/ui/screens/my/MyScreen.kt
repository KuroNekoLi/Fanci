package com.cmoney.fanci.ui.screens.my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanci.ui.screens.my.state.MyScreenState
import com.cmoney.fanci.ui.screens.my.state.rememberMyScreenState
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor

sealed class MyCallback {
    object ChangeAvatar: MyCallback()
}


@Composable
fun MyScreen(
    myScreenState: MyScreenState = rememberMyScreenState(),
    onClick: (MyCallback) -> Unit
) {
    val TAG = "MyScreen"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = myScreenState.scaffoldState,
        topBar = {
            TopBarScreen(
                title = "我的",
                leadingEnable = false,
                trailingEnable = false,
                moreEnable = false,
                moreClick = {
                },
                backClick = {
                    myScreenState.navController.popBackStack()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .background(LocalColor.current.env_80)
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            //大頭貼
            MyInfoScreen(
                modifier = Modifier.padding(20.dp)
            )

            //帳號資料
            AccountInfoScreen(
                modifier = Modifier.padding(top = 15.dp),
                onChangeAvatarClick = {
                    onClick.invoke(MyCallback.ChangeAvatar)
                }
            )

            //社團設定
            GroupSettingScreen(
                modifier = Modifier.padding(top = 25.dp)
            )

            //關於我們
            AboutScreen(modifier = Modifier.padding(top = 25.dp))
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MyScreenPreview() {
    FanciTheme {
        MyScreen {
        }
    }
}