package com.cmoney.kolfanci.ui.screens.shared.role

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.extension.toColor
import com.cmoney.kolfanci.ui.theme.Blue_4F70E5
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.kolfanci.R

/**
 * 社團角色 Item
 * @param index 排名第幾
 * @param fanciRole 角色 Model
 * @param isShowIndex 是否要呈現 排名
 */
@Composable
fun RoleItemScreen(
    modifier: Modifier = Modifier,
    index: Int,
    isShowIndex: Boolean = false,
    fanciRole: FanciRole,
    editText: String = "編輯",
    isSortMode: Boolean = false,
    onEditClick: (FanciRole) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalColor.current.background)
            .clickable {
                if (!isSortMode) {
                    onEditClick.invoke(fanciRole)
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isShowIndex) {
            Text(
                modifier = Modifier.padding(
                    top = 20.dp,
                    bottom = 20.dp,
                    start = 30.dp,
                    end = 16.dp
                ),
                text = "NO.%d".format(index),
                fontSize = 14.sp,
                color = LocalColor.current.text.default_100
            )
        }

        val roleColor = if (fanciRole.color?.isNotEmpty() == true) {
            var roleColor = LocalColor.current.specialColor.red

            val filterList = LocalColor.current.roleColor.colors.filter {
                it.name == fanciRole.color
            }

            if (filterList.isNotEmpty()) {
                filterList[0].hexColorCode?.let {
                    roleColor = it.toColor()
                }
            }
            roleColor
        } else {
            LocalColor.current.specialColor.red
        }

        val imageModifier = if (isShowIndex) {
            Modifier.size(25.dp)
        } else {
            Modifier
                .padding(top = 20.dp, bottom = 20.dp, start = 30.dp, end = 16.dp)
                .size(25.dp)
        }

        Image(
            modifier = imageModifier,
            contentScale = ContentScale.FillBounds,
            painter = painterResource(id = R.drawable.rule_manage),
            colorFilter = ColorFilter.tint(color = roleColor),
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = fanciRole.name.orEmpty(),
                fontSize = 16.sp,
                color = LocalColor.current.text.default_100
            )

            Text(
                text = "%d 位成員".format(fanciRole.userCount ?: 0),
                fontSize = 12.sp,
                color = LocalColor.current.component.other
            )
        }

        if (!isSortMode) {
            Text(
                modifier = Modifier.padding(end = 24.dp),
                text = editText, fontSize = 14.sp,
                color = LocalColor.current.primary
            )
        } else {
            Image(
                modifier = Modifier.padding(end = 24.dp),
                painter = painterResource(id = R.drawable.menu),
                colorFilter = ColorFilter.tint(color = LocalColor.current.primary),
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoleItemScreenPreview() {
    FanciTheme {
        RoleItemScreen(
            index = 1,
            fanciRole = FanciRole()
        ) {}
    }
}