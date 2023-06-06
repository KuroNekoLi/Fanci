package com.cmoney.kolfanci.ui.screens.shared.vip

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 呈現 vip 方案 item
 *
 * @param vipPlanModel 方案 model
 * @param endText 尾巴文字
 */
@Composable
fun VipPlanScreen(
    modifier: Modifier = Modifier,
    vipPlanModel: VipPlanModel,
    endText: String = "管理",
    onPlanClick: (VipPlanModel) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalColor.current.background)
            .clickable {
                onPlanClick.invoke(vipPlanModel)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 20.dp, start = 30.dp, end = 16.dp)
                .size(25.dp),
            painter = painterResource(id = vipPlanModel.planIcon),
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = vipPlanModel.name,
                fontSize = 16.sp,
                color = LocalColor.current.text.default_100
            )

            Text(
                text = "%d 位成員".format(vipPlanModel.memberCount),
                fontSize = 12.sp,
                color = LocalColor.current.component.other
            )
        }

        Text(
            modifier = Modifier.padding(end = 24.dp),
            text = endText,
            fontSize = 14.sp,
            color = LocalColor.current.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VipPlanScreenPreview() {
    FanciTheme {
        VipPlanScreen(
            vipPlanModel = VipPlanModel(
                name = "高級學員",
                memberCount = 10
            ),
            onPlanClick = {}
        )
    }
}