package com.cmoney.kolfanci.ui.screens.my

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.BuildConfig
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.openCustomTab
import com.cmoney.kolfanci.ui.screens.shared.setting.SettingItemScreen

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        Text(
            modifier = Modifier
                .padding(
                    start = 25.dp,
                ),
            text = stringResource(id = R.string.about_us),
            fontSize = 14.sp,
            color = LocalColor.current.text.default_100
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier = Modifier.background(LocalColor.current.background)) {

            //服務條款
            SettingItemScreen(
                modifier = Modifier.padding(bottom = 1.dp),
                text = stringResource(id = R.string.service_guide_line),
                onItemClick = {
                    context.openCustomTab(Uri.parse(context.getString(R.string.terms_of_service_url)))
                }
            )

            //隱私權政策
            SettingItemScreen(
                modifier = Modifier.padding(bottom = 1.dp),
                text = stringResource(id = R.string.policy),
                onItemClick = {
                    context.openCustomTab(Uri.parse(context.getString(R.string.privacy_policy_url)))
                }
            )

            //著作權條款
            SettingItemScreen(
                modifier = Modifier.padding(bottom = 1.dp),
                text = stringResource(id = R.string.editor_policy),
                onItemClick = {
                    context.openCustomTab(Uri.parse(context.getString(R.string.copyright_policy_url)))
                }
            )

            //意見回饋
            SettingItemScreen(
                modifier = Modifier.padding(bottom = 1.dp),
                text = stringResource(id = R.string.feedback),
                onItemClick = {
                    context.openCustomTab(Uri.parse(context.getString(R.string.feedback_url)))
                }
            )

            //系統版本
            SettingItemScreen(
                modifier = Modifier.padding(bottom = 1.dp),
                text = stringResource(id = R.string.system_version),
                withRightArrow = false,
                otherContent = {
                    Text(
                        text = BuildConfig.VERSION_NAME,
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
fun AboutScreenPreview() {
    FanciTheme {
        AboutScreen()
    }
}