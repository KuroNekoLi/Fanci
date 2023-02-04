package com.cmoney.kolfanci.ui.screens.group.setting.member.role.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.MainActivity
import com.cmoney.kolfanci.extension.showColorPickerDialogBottomSheet
import com.cmoney.kolfanci.extension.toColor
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.socks.library.KLog
import com.cmoney.kolfanci.R
/**
 * 樣式 View
 */
@Composable
fun StyleScreen(
    modifier: Modifier = Modifier,
    mainActivity: MainActivity,
    roleName: String,
    roleColor: com.cmoney.fanciapi.fanci.model.Color,
    showDelete: Boolean,
    onChange: (String, com.cmoney.fanciapi.fanci.model.Color) -> Unit,
    onDelete: () -> Unit
) {
    val TAG = "StyleView"
    val maxLength = 10

    val defaultColor = if (roleColor.hexColorCode.orEmpty().isEmpty()) {
        LocalColor.current.roleColor.colors.first()
    } else {
        roleColor
    }

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

        if (showDelete && Constant.MyGroupPermission.deleteRole == true) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                modifier = Modifier.padding(start = 24.dp, bottom = 10.dp),
                text = "刪除角色", fontSize = 14.sp, color = LocalColor.current.text.default_100
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(LocalColor.current.background)
                    .clickable {
                        onDelete.invoke()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "刪除角色", fontSize = 17.sp, color = LocalColor.current.specialColor.red)
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun StyleScreenPreview() {
    FanciTheme {
        StyleScreen(
            mainActivity = MainActivity(),
            roleName = "",
            roleColor = com.cmoney.fanciapi.fanci.model.Color(),
            showDelete = true,
            onDelete = {},
            onChange = { _, _ ->

            }
        )
    }
}