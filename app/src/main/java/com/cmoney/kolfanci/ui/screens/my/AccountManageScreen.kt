package com.cmoney.kolfanci.ui.screens.my

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.findActivity
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.main.MainActivity
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.xlogin.XLoginHelper
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog

@Destination
@Composable
fun AccountManageScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator
) {
    val context = LocalContext.current

    //show 再次登出確認彈窗
    var showConfirmDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = "帳號管理",
                backClick = {
                    navController.popBackStack()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(LocalColor.current.env_80)
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .background(LocalColor.current.background)
                    .padding(
                        top = 10.dp,
                        bottom = 10.dp,
                        start = 25.dp,
                        end = 10.dp
                    )
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.component.other)
                )
                Spacer(modifier = Modifier.width(17.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = "帳號管理", fontSize = 17.sp, color = LocalColor.current.text.default_100
                )
                Text(
                    text = XLoginHelper.account,
                    fontSize = 14.sp,
                    color = LocalColor.current.text.default_100
                )
            }

            Spacer(modifier = Modifier.height(1.dp))

            Row(
                modifier = Modifier
                    .background(LocalColor.current.background)
                    .clickable {
                        KLog.i("AccountManageScreen", "Logout click.")
                        AppUserLogger.getInstance().log(Clicked.AccountManagementLogout)

                        showConfirmDialog = true
                    }
                    .padding(
                        top = 10.dp,
                        bottom = 10.dp,
                        start = 25.dp,
                        end = 10.dp
                    )
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.logout),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.component.other)
                )
                Spacer(modifier = Modifier.width(17.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = "登出帳號", fontSize = 17.sp, color = LocalColor.current.text.default_100
                )
                Image(
                    painter = painterResource(id = R.drawable.next),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.text.default_80)
                )
            }
        }
    }

    if (showConfirmDialog) {
        DialogScreen(
            title = "確定要登出帳號嗎？",
            subTitle = "你確定要將 登出帳號嗎？\n" +
                    "（帳號登出社團資料皆會保留）",
            onDismiss = {
                showConfirmDialog = false
            }) {
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = "確定登出",
                borderColor = LocalColor.current.component.other,
                textColor = Color.White
            ) {
                AppUserLogger.getInstance().log(Clicked.LogoutConfirmLogout)

                XLoginHelper.logOut(context)
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.findActivity().finish()
                context.startActivity(intent)
            }

            Spacer(modifier = Modifier.height(20.dp))

            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = "返回",
                borderColor = LocalColor.current.component.other,
                textColor = Color.White
            ) {
                AppUserLogger.getInstance().log(Clicked.LogoutReturn)
                showConfirmDialog = false
            }
        }
    }
    LaunchedEffect(key1 = Unit) {
        AppUserLogger.getInstance()
            .log(page = Page.MemberPageAccountManagement)
    }
}

@Preview(showBackground = true)
@Composable
fun AccountManageScreenPreview() {
    FanciTheme {
        AccountManageScreen(
            navController = EmptyDestinationsNavigator
        )
    }
}