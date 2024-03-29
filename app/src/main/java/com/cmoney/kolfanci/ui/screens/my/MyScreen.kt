package com.cmoney.kolfanci.ui.screens.my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.AccountManageScreenDestination
import com.cmoney.kolfanci.ui.destinations.AvatarNicknameChangeScreenDestination
import com.cmoney.kolfanci.ui.screens.shared.toolbar.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.xlogin.XLoginHelper
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

/**
 * 會員中心
 */
@Destination
@Composable
fun MyScreen(
    navController: DestinationsNavigator,
    viewModel: MyScreenViewModel = koinViewModel()
) {
    val TAG = "MyScreen"

    var isShowPurchasePlanTip by remember {
        mutableStateOf(false)
    }

    val vipPlan by viewModel.userVipPlan.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = "會員中心",
                backClick = {
                    AppUserLogger.getInstance().log(Clicked.MemberPageHome)
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

            //購買的vip方案
            if (vipPlan.isNotEmpty() && Constant.isAppNotInReview()) {
                PurchaseVipPlanScreen(
                    modifier = Modifier.padding(top = 15.dp),
                    vipPlanList = vipPlan,
                    onPlanClick = {
                        KLog.i(TAG, "onPlanClick:$it")
                        isShowPurchasePlanTip = true
                    }
                )
            }

            //帳號資料
            AccountInfoScreen(
                modifier = Modifier.padding(top = 15.dp),
                account = XLoginHelper.account,
                accountNumber = Constant.MyInfo?.serialNumber.toString(),
                onChangeAvatarClick = {
                    AppUserLogger.getInstance().log(Clicked.MemberPageAvatarAndNickname)
                    navController.navigate(AvatarNicknameChangeScreenDestination)
                },
                onAccountManageClick = {
                    AppUserLogger.getInstance().log(Clicked.MemberPageAccountManagement)
                    navController.navigate(AccountManageScreenDestination)
                }
            )

            //關於我們
            AboutScreen(modifier = Modifier.padding(top = 25.dp))
        }
    }

    if (isShowPurchasePlanTip) {
        DialogScreen(
            title = stringResource(id = R.string.contact_personal_service),
            subTitle = stringResource(id = R.string.contact_official_mail),
            onDismiss = { isShowPurchasePlanTip = false }
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = stringResource(id = R.string.copy_mail),
                borderColor = LocalColor.current.text.default_50,
                textColor = LocalColor.current.text.default_100,
                onClick = {
                    isShowPurchasePlanTip = false
                    viewModel.onVipDialogClick()
                    Unit
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = stringResource(id = R.string.back),
                borderColor = LocalColor.current.text.default_50,
                textColor = LocalColor.current.text.default_100,
                onClick = {
                    isShowPurchasePlanTip = false
                    Unit
                }
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getUserVipPlan()
        AppUserLogger.getInstance()
            .log(page = Page.MemberPage)
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