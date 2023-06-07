package com.cmoney.kolfanci.ui.screens.group.setting.vip.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.usecase.VipManagerUseCase
import com.cmoney.kolfanci.ui.destinations.EditInputScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanInfoModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanPermissionModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.viewmodel.VipManagerViewModel
import com.cmoney.kolfanci.ui.screens.shared.TabScreen
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.setting.SettingItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * 管理 VIP 方案 - 主畫面
 */
@Destination
@Composable
fun VipPlanInfoMainScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    vipPlanModel: VipPlanModel,
    viewModel: VipManagerViewModel = koinViewModel(
        parameters = {
            parametersOf(group)
        }
    ),
    vipNameResultRecipient: ResultRecipient<EditInputScreenDestination, String>
) {
    //目前選擇的 tab
    val selectedTabPosition by viewModel.manageTabPosition.collectAsState()

    //vip 方案資訊
    val vipPlanInfo by viewModel.planInfo.collectAsState()
    // vip 權限資訊
    val vipPlanPermissionModels by viewModel.permissionModels.collectAsState()

    vipPlanInfo?.let {
        VipPlanInfoMainScreenView(
            modifier = modifier,
            navController = navController,
            selectedTab = selectedTabPosition,
            vipPlanInfo = it,
            vipPlanPermissionModels = vipPlanPermissionModels.orEmpty(),
            onTabSelected = { position ->
                viewModel.onManageVipTabClick(position)
            }
        )
    }

    LaunchedEffect(Unit) {
        if (vipPlanInfo == null) {
            viewModel.fetchVipPlanInfo(vipPlanModel)
        }
        if (vipPlanPermissionModels == null) {
            viewModel.fetchPermissions(vipPlanModel = vipPlanModel)
        }
    }

    //==================== Result callback ====================
    vipNameResultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val vipName = result.value
                viewModel.setVipName(vipName)
            }
        }
    }
}

@Composable
private fun VipPlanInfoMainScreenView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    selectedTab: VipManagerViewModel.VipManageTabKind,
    onTabSelected: (Int) -> Unit,
    vipPlanInfo: VipPlanInfoModel,
    vipPlanPermissionModels: List<VipPlanPermissionModel>
) {
    val TAG = "VipManagerScreenView"
    val context = LocalContext.current

    val list = listOf(
        stringResource(id = R.string.information),
        stringResource(id = R.string.permission),
        stringResource(id = R.string.member)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = stringResource(id = R.string.manager_vip_plan),
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    navController.popBackStack()
                }
            )
        },
        backgroundColor = LocalColor.current.env_80
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabScreen(
                modifier = Modifier
                    .padding(18.dp)
                    .height(40.dp),
                selectedIndex = selectedTab.ordinal,
                listItem = list,
                onTabClick = {
                    onTabSelected.invoke(it)
                }
            )

            when (selectedTab) {
                //資訊
                VipManagerViewModel.VipManageTabKind.INFO -> {
                    VipInfoPage(
                        vipName = vipPlanInfo.name,
                        planSourceList = vipPlanInfo.planSourceDescList,
                        onVipNameClick = {
                            KLog.i(TAG, "onVipNameClick:$it")
                            navController.navigate(
                                EditInputScreenDestination(
                                    defaultText = it,
                                    toolbarTitle = context.getString(R.string.vip_name),
                                    placeholderText = context.getString(R.string.input_vip_name),
                                    emptyAlertTitle = context.getString(R.string.vip_name_empty),
                                    emptyAlertSubTitle = context.getString(R.string.vip_name_empty_desc)
                                )
                            )
                        }
                    )
                }

                //權限
                VipManagerViewModel.VipManageTabKind.PERMISSION -> {
                    VipPlanInfoPermissionPage(
                        permissionModels = vipPlanPermissionModels,
                        onEditPermission = { index ->
                            // TODO 跳轉編輯權限頁
                        }
                    )
                }

                //成員
                VipManagerViewModel.VipManageTabKind.MEMBER -> {
                    TODO()
                }
            }
        }
    }
}

/**
 * 方案 資訊
 *
 * @param vipName vip名稱
 * @param planSourceList 方案購買來源
 */
@Composable
private fun VipInfoPage(
    vipName: String,
    planSourceList: List<String>,
    onVipNameClick: (String) -> Unit
) {
    Column {
        Text(
            modifier = Modifier.padding(
                top = 20.dp,
                bottom = 20.dp,
                start = 24.dp,
                end = 24.dp
            ),
            text = stringResource(id = R.string.vip_manage_info_description),
            fontSize = 14.sp,
            color = LocalColor.current.text.default_50
        )

        //vip 名稱
        Text(
            modifier = Modifier.padding(start = 25.dp, bottom = 9.dp),
            text = stringResource(id = R.string.vip_name),
            fontSize = 14.sp,
            color = LocalColor.current.text.default_100
        )

        SettingItemScreen(
            text = vipName,
            onItemClick = {
                onVipNameClick.invoke(vipName)
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        //訂閱方案
        Text(
            modifier = Modifier.padding(start = 25.dp, bottom = 9.dp),
            text = stringResource(id = R.string.subscription_plan),
            fontSize = 14.sp,
            color = LocalColor.current.text.default_100
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(planSourceList) { planSource ->
                SettingItemScreen(
                    text = planSource,
                    withRightArrow = false
                )
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun VipPlanInfoScreenPreview() {
    FanciTheme {
        VipPlanInfoMainScreenView(
            navController = EmptyDestinationsNavigator,
            selectedTab = VipManagerViewModel.VipManageTabKind.INFO,
            vipPlanInfo = VipManagerUseCase.getVipPlanInfoMockData(),
            vipPlanPermissionModels = listOf(
                VipPlanPermissionModel(
                    name = "歡迎新朋友",
                    canEdit = false,
                    permissionTitle = "公開頻道",
                    authType = "basic"
                ),
                VipPlanPermissionModel(
                    name = "健身肌肉男",
                    canEdit = true,
                    permissionTitle = "進階權限",
                    authType = "basic"
                )
            ),
            onTabSelected = {
            }
        )
    }
}