package com.cmoney.fanci.ui.screens.shared.role

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
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.FanciRole

/**
 * 社團角色 Item
 * @param index 排名第幾
 * @param fanciRole 角色 Model
 */
@Composable
fun RoleItemScreen(
    modifier: Modifier = Modifier,
    index: Int,
    fanciRole: FanciRole,
    onEditClick: (FanciRole) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalColor.current.background)
            .clickable {
                onEditClick.invoke(fanciRole)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(top = 20.dp, bottom = 20.dp, start = 30.dp, end = 16.dp),
            text = "NO.%d".format(index),
            fontSize = 14.sp,
            color = LocalColor.current.component.other
        )

        val roleColor = if (fanciRole.color?.isNotEmpty() == true) {
            Color(fanciRole.color.orEmpty().toLong(16))
        } else {
            LocalColor.current.specialColor.red
        }

        Image(
            modifier = Modifier.size(25.dp),
            contentScale = ContentScale.FillBounds,
            painter = painterResource(id = R.drawable.rule_manage),
            colorFilter = ColorFilter.tint(color = roleColor),
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Test", fontSize = 16.sp, color = Color.White)
            Text(text = "3位成員", fontSize = 12.sp, color = LocalColor.current.component.other)
        }

        Text(
            modifier = Modifier.padding(end = 24.dp),
            text = "編輯", fontSize = 14.sp, color = LocalColor.current.primary
        )
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