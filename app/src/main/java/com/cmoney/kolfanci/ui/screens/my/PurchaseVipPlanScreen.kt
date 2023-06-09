package com.cmoney.kolfanci.ui.screens.my

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.usecase.VipManagerUseCase
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel
import com.cmoney.kolfanci.ui.screens.shared.vip.VipPlanItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun PurchaseVipPlanScreen(
    modifier: Modifier = Modifier,
    vipPlanList: List<VipPlanModel>,
    onPlanClick: (VipPlanModel) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier
                .padding(
                    start = 25.dp,
                ),
            text = stringResource(id = R.string.already_purchases_plan), fontSize = 14.sp,
            color = LocalColor.current.text.default_100
        )

        Spacer(modifier = Modifier.height(10.dp))

        vipPlanList.forEach { plan ->
            Box {
                VipPlanItemScreen(
                    modifier = Modifier.fillMaxWidth(),
                    vipPlanModel = plan,
                    subTitle = plan.description,
                    onPlanClick = onPlanClick
                )

                Image(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp),
                    painter = painterResource(id = R.drawable.next),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.text.default_80)
                )
            }

            Spacer(modifier = Modifier.height(1.dp))
        }
    }
}

@Preview
@Composable
fun PurchaseVipPlanScreenPreview() {
    FanciTheme {
        PurchaseVipPlanScreen(
            vipPlanList = VipManagerUseCase.getVipPlanMockData(),
            onPlanClick = {}
        )
    }
}