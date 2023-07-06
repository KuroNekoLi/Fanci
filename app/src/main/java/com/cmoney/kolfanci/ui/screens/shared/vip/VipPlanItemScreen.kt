package com.cmoney.kolfanci.ui.screens.shared.vip

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel
import com.cmoney.kolfanci.ui.screens.shared.CircleCheckedScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 呈現 vip 方案 item
 *
 * @param modifier 元件外框的 Modifier
 * @param vipPlanModel 方案 model
 * @param paddingValues 元件間隔，預設為 start 28.dp end 24.dp
 * @param rowHeight 元件高度，預設為 60 dp
 * @param subTitle 複標題，預設使用 [VipPlanModel.description] 顯示
 * @param endText 元件尾部的文字
 * @param onPlanClick 當 item 元件被點擊時
 */
@Composable
fun VipPlanItemScreen(
    modifier: Modifier = Modifier,
    vipPlanModel: VipPlanModel,
    paddingValues: PaddingValues = PaddingValues(start = 28.dp, end = 24.dp),
    rowHeight: Dp = 60.dp,
    subTitle: String = vipPlanModel.description,
    endText: String? = null,
    onPlanClick: ((VipPlanModel) -> Unit)? = null
) {
    VipPlanItemScreen(
        modifier = modifier,
        vipPlanModel = vipPlanModel,
        subTitle = subTitle,
        paddingValues = paddingValues,
        rowHeight = rowHeight,
        endContent = {
            if (endText != null) {
                Text(
                    text = endText,
                    fontSize = 14.sp,
                    color = LocalColor.current.primary
                )
            }
        },
        onPlanClick = onPlanClick
    )
}

/**
 * 呈現 vip 方案 item
 *
 * @param modifier 元件外框的 Modifier
 * @param vipPlanModel 方案 model
 * @param paddingValues 元件間隔，預設為 start 28.dp
 * @param rowHeight 元件高度，預設為 60 dp
 * @param subTitle 複標題，預設使用 [VipPlanModel.description] 顯示
 * @param endContent 尾部區域內容
 * @param onPlanClick 當 item 元件被點擊時
 */
@Composable
fun VipPlanItemScreen(
    modifier: Modifier = Modifier,
    vipPlanModel: VipPlanModel,
    paddingValues: PaddingValues = PaddingValues(start = 28.dp),
    rowHeight: Dp = 60.dp,
    subTitle: String = vipPlanModel.description,
    endContent: @Composable (() -> Unit)? = null,
    onPlanClick: ((VipPlanModel) -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .background(LocalColor.current.background)
            .clickable(enabled = onPlanClick != null) {
                onPlanClick?.invoke(vipPlanModel)
            }
            .height(height = rowHeight)
            .padding(paddingValues = paddingValues)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(25.dp),
            painter = painterResource(id = vipPlanModel.planIcon),
            contentDescription = "vip sign"
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = vipPlanModel.name,
                fontSize = 16.sp,
                color = LocalColor.current.text.default_100
            )

            Text(
                text = subTitle,
                fontSize = 12.sp,
                color = LocalColor.current.component.other
            )
        }
        endContent?.invoke()
    }
}

@Preview
@Composable
fun VipPlanScreenPreview() {
    FanciTheme {
        LazyColumn(
            modifier = Modifier.background(LocalColor.current.env_80),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                VipPlanItemScreen(
                    modifier = Modifier
                        .fillMaxWidth(),
                    vipPlanModel = VipPlanModel(
                        id = "1",
                        name = "高級學員",
                        memberCount = 10,
                        description = ""
                    ),
                    subTitle = stringResource(id = R.string.n_member).format(10),
                    endText = "管理",
                    onPlanClick = {}
                )
            }
            item {
                VipPlanItemScreen(
                    modifier = Modifier
                        .fillMaxWidth(),
                    vipPlanModel = VipPlanModel(
                        id = "1",
                        name = "高級學員",
                        memberCount = 10,
                        description = ""
                    ),
                    subTitle = stringResource(id = R.string.n_member).format(10),
                    endContent = {
                        CircleCheckedScreen(isChecked = true)
                    },
                    onPlanClick = {}
                )
            }
            item {
                VipPlanItemScreen(
                    modifier = Modifier
                        .fillMaxWidth(),
                    vipPlanModel = VipPlanModel(
                        id = "1",
                        name = "高級學員",
                        memberCount = 10,
                        description = ""
                    ),
                    subTitle = stringResource(id = R.string.n_member).format(10),
                    endContent = {
                    },
                    onPlanClick = null
                )
            }
        }
    }
}