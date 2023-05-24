package com.cmoney.kolfanci.ui.screens.my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.ui.destinations.AccountManageScreenDestination
import com.cmoney.kolfanci.ui.destinations.AvatarNicknameChangeScreenDestination
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.xlogin.XLoginHelper
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

/**
 * 會員中心
 */
@Destination
@Composable
fun MyScreen(
    navController: DestinationsNavigator,
) {
    val TAG = "MyScreen"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = "會員中心",
                leadingEnable = true,
                trailingEnable = false,
                moreEnable = false,
                leadingIcon = ImageVector.vectorResource(id = R.drawable.house),
                moreClick = {
                },
                backClick = {
                    navController.popBackStack()
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
            //大頭貼/暱稱
            MyInfoScreen(
                modifier = Modifier.padding(20.dp),
                avatarUrl = XLoginHelper.headImagePath,
                name = XLoginHelper.nickName
            )

            //帳號資料
            AccountInfoScreen(
                modifier = Modifier.padding(top = 15.dp),
                account = XLoginHelper.account,
                accountNumber = Constant.MyInfo?.serialNumber.toString(),
                onChangeAvatarClick = {
                    navController.navigate(AvatarNicknameChangeScreenDestination)
                },
                onAccountManageClick = {
                    navController.navigate(AccountManageScreenDestination)
                }
            )

            //TODO 暫時不顯示
//            //社團設定
//            GroupSettingScreen(
//                modifier = Modifier.padding(top = 25.dp)
//            )
//
//            //關於我們
//            AboutScreen(modifier = Modifier.padding(top = 25.dp))
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MyScreenPreview() {
    FanciTheme {
        MyScreen(
            navController = EmptyDestinationsNavigator
        )
    }
}