package com.cmoney.fanci.ui.screens.group.setting.role.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.MainActivity
import com.cmoney.fanci.R
import com.cmoney.fanci.extension.showColorPickerDialogBottomSheet
import com.cmoney.fanci.extension.toColor
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.socks.library.KLog

/**
 * 樣式 View
 */
@Composable
fun StyleScreen(
    modifier: Modifier = Modifier,
    mainActivity: MainActivity,
    roleName: String,
    roleColor: com.cmoney.fanciapi.fanci.model.Color,
    onChange: (String, com.cmoney.fanciapi.fanci.model.Color) -> Unit
) {
    val TAG = "StyleView"
    val maxLength = 10

//    var textState by remember { mutableStateOf("") }

    val defaultColor = if (roleColor.hexColorCode.orEmpty().isEmpty()) {
        LocalColor.current.roleColor.colors.first()
    }
    else {
        roleColor
    }

//    val selectRoleColor = remember {
//        mutableStateOf(
//            if (roleColor.hexColorCode.orEmpty().isEmpty()) {
//                defaultColor
//            }
//            else {
//                roleColor
//            }
//        )
//    }

    Column(modifier = modifier.background(LocalColor.current.env_80)) {
        Row(
            modifier = Modifier.padding(
                top = 20.dp,
                start = 24.dp,
                end = 24.dp,
                bottom = 20.dp
            )
        ) {
            Text(
                text = "角色組名稱",
                fontSize = 14.sp,
                color = LocalColor.current.text.default_100
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "%d/%d".format(roleName.length, maxLength),
                fontSize = 14.sp,
                color = LocalColor.current.text.default_50
            )
        }

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = roleName,
            colors = TextFieldDefaults.textFieldColors(
                textColor = LocalColor.current.text.default_100,
                backgroundColor = LocalColor.current.background,
                cursorColor = LocalColor.current.primary,
                disabledLabelColor = LocalColor.current.text.default_30,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            onValueChange = {
                if (it.length <= maxLength) {
                    onChange.invoke(it, defaultColor)
                }
            },
            maxLines = 1,
            textStyle = TextStyle.Default.copy(fontSize = 16.sp),
            placeholder = {
                Text(
                    text = "輸入角色名稱",
                    fontSize = 16.sp,
                    color = LocalColor.current.text.default_30
                )
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            modifier = Modifier.padding(start = 24.dp, bottom = 10.dp),
            text = "角色顯示顏色", fontSize = 14.sp, color = LocalColor.current.text.default_100
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(LocalColor.current.background)
                .clickable {
                    mainActivity.showColorPickerDialogBottomSheet(
                        selectedColor = defaultColor
                    ) {
                        KLog.i(TAG, "color pick:$it")
                        onChange.invoke(roleName, it)
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                modifier = Modifier.padding(start = 25.dp),
                painter = painterResource(id = R.drawable.rule_manage),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    color = defaultColor.hexColorCode.orEmpty().toColor()
                )
            )

            Text(
                modifier = Modifier.padding(start = 17.dp),
                text = defaultColor.displayName.orEmpty(),
                fontSize = 17.sp,
                color = LocalColor.current.text.default_100
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StyleScreenPreview() {
    FanciTheme {
        StyleScreen(mainActivity = MainActivity(),
            roleName = "",
            roleColor = com.cmoney.fanciapi.fanci.model.Color()
        ) { _, _ ->
        }
    }
}