package com.cmoney.kolfanci.ui.screens.group.setting.vip

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.usecase.VipManagerUseCase
import com.cmoney.kolfanci.ui.destinations.VipPlanInfoMainScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.viewmodel.VipManagerViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.vip.VipPlanItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * VIP 方案管理
 *
 * @param group 要管理的社團
 */
@Destination
@Composable
fun VipManagerScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    viewModel: VipManagerViewModel = koinViewModel(
        parameters = {
            parametersOf(group)
        }
    )
) {
    val TAG = "VipManagerScreen"

    val vipPlanList by viewModel.vipPlanList.collectAsState()

    VipManagerScreenView(
        modifier = modifier,
        navController = navController,
        vipPlanList = vipPlanList,
        onPlanClick = { vipPlan ->
            KLog.i(TAG, "onPlanClick:$vipPlan")
            navController.navigate(
                VipPlanInfoMainScreenDestination(
                    group = group,
                    vipPlanModel = vipPlan
                )
            )
        }
    )

    LaunchedEffect(Unit) {
        viewModel.fetchVipPlan()
    }
}

@Composable
fun VipManagerScreenView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    vipPlanList: List<VipPlanModel>,
    onPlanClick: (VipPlanModel) -> Unit
) {
    val TAG = "VipManagerScreenView"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = stringResource(id = R.string.vip_manager),
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    navController.popBackStack()
                }
            )
        },
        backgroundColor = LocalColor.current.env_80
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            if (vipPlanList.isEmpty()) {
                //display empty screen
                EmptyVipPlanScreen()
            } else {
                //display plan list
                Text(
                    modifier = Modifier.padding(
                        top = 20.dp,
                        bottom = 20.dp,
                        start = 24.dp,
                        end = 24.dp
                    ),
                    text = stringResource(id = R.string.vip_plan_description),
                    fontSize = 14.sp,
                    color = LocalColor.current.text.default_50
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(vipPlanList) { plan ->
                        VipPlanItemScreen(
                            modifier = Modifier.fillMaxWidth(),
                            vipPlanModel = plan,
                            subTitle = stringResource(id = R.string.n_member).format(plan.memberCount),
                            endText = stringResource(id = R.string.manage),
                            onPlanClick = onPlanClick
                        )
                    }
                }
            }
        }
    }
}

/**
 * 沒有方案時呈現畫面
 */
@Composable
private fun EmptyVipPlanScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            modifier = Modifier.size(105.dp),
            painter = painterResource(id = R.drawable.flower_box), contentDescription = null
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.vip_plan_empty_description),
            fontSize = 16.sp,
            color = LocalColor.current.text.default_30
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VipManagerScreenPreview() {
    FanciTheme {
        VipManagerScreenView(
            navController = EmptyDestinationsNavigator,
            vipPlanList = VipManagerUseCase.getVipPlanMockData(),
            onPlanClick = {}
        )
    }
}