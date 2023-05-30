package com.cmoney.kolfanci.ui.screens.my

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.shared.setting.SettingItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun AccountInfoScreen(
    modifier: Modifier = Modifier,
    account: String,
    accountNumber: String,
    onChangeAvatarClick: () -> Unit,
    onAccountManageClick: () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier
                .padding(
                    start = 25.dp,
                ),
            text = stringResource(id = R.string.account_info), fontSize = 14.sp,
            color = LocalColor.current.text.default_100
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column {
            //頭像與暱稱
            SettingItemScreen(
                modifier = Modifier.padding(bottom = 1.dp),
                text = stringResource(id = R.string.avatar_nickname),
                onItemClick = onChangeAvatarClick
            )

            //帳號管理
            SettingItemScreen(
                modifier = Modifier.padding(bottom = 1.dp),
                text = stringResource(id = R.string.manage_account),
                onItemClick = onAccountManageClick,
                otherContent = {
                    Text(
                        text = account,
                        fontSize = 14.sp,
                        color = LocalColor.current.text.default_100
                    )
                }
            )

            //使用者編號
            SettingItemScreen(
                modifier = Modifier.padding(bottom = 1.dp),
                text = stringResource(id = R.string.user_number),
                withRightArrow = false,
                otherContent = {
                    Text(
                        text = accountNumber,
                        fontSize = 14.sp,
                        color = LocalColor.current.text.default_100
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountInfoScreenPreview() {
    FanciTheme {
        AccountInfoScreen(
            account = "Account",
            accountNumber = "12345",
            onAccountManageClick = {},
            onChangeAvatarClick = {}
        )
    }
}