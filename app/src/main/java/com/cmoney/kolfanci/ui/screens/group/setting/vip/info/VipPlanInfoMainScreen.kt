package com.cmoney.kolfanci.ui.screens.group.setting.vip.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.ChannelAuthType
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.usecase.VipManagerUseCase
import com.cmoney.kolfanci.ui.destinations.EditInputScreenDestination
import com.cmoney.kolfanci.ui.destinations.VipPlanInfoEditChannelPermissionScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanPermissionModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanPermissionOptionModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.viewmodel.VipManagerViewModel
import com.cmoney.kolfanci.ui.screens.shared.TabScreen
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.member.MemberItemScreen
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
    vipNameResultRecipient: ResultRecipient<EditInputScreenDestination, String>,
    vipPermissionResultRecipient: ResultRecipient<VipPlanInfoEditChannelPermissionScreenDestination, VipPlanPermissionModel>
) {

    //目前選擇的 tab
    val selectedTabPosition by viewModel.manageTabPosition.collectAsState()

    //vip 權限資訊
    val vipPlanPermissionModels by viewModel.permissionModels.collectAsState()

    val vipPlanPermissionOptionModels by viewModel.permissionOptionModels.collectAsState()

    //vip plan
    val planModel by viewModel.vipPlanModel.collectAsState()

    //vip 方案資訊
    val planSourceList by viewModel.planSourceList.collectAsState()

    //vip 成員
    val vipMembers by viewModel.vipMembers.collectAsState()

    LaunchedEffect(Unit) {
        if (planModel == null) {
            viewModel.initVipPlanModel(vipPlanModel)
        }
    }

    VipPlanInfoMainScreenView(
        modifier = modifier,
        navController = navController,
        vipName = planModel?.name.orEmpty(),
        selectedTab = selectedTabPosition,
        onTabSelected = { position ->
            viewModel.onManageVipTabClick(position)
        },
        vipPlanPermissionModels = vipPlanPermissionModels.orEmpty(),
        vipPlanPermissionOptionModels = vipPlanPermissionOptionModels.orEmpty(),
        planSourceList = planSourceList,
        vipMembers = vipMembers
    )

    LaunchedEffect(Unit) {
        if (vipPlanPermissionModels == null) {
            viewModel.fetchPermissions(vipPlanModel = vipPlanModel)
        }
        if (vipPlanPermissionOptionModels == null) {
            viewModel.fetchPermissionOptions(vipPlanModel = vipPlanModel)
        }
    }

    //==================== Result callback ====================
    vipNameResultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                viewModel.setVipName(result.value)
            }
        }
    }
    vipPermissionResultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val newPermission = result.value
                viewModel.setPermission(permissionModel = newPermission)
            }
        }
    }
}

@Composable
private fun VipPlanInfoMainScreenView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    vipName: String,
    selectedTab: VipManagerViewModel.VipManageTabKind,
    onTabSelected: (Int) -> Unit,
    vipPlanPermissionModels: List<VipPlanPermissionModel>,
    vipPlanPermissionOptionModels: List<VipPlanPermissionOptionModel>,
    planSourceList: List<String>,
    vipMembers: List<GroupMember>
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
                        vipName = vipName,
                        planSourceList = planSourceList,
                        onVipNameClick = {
                            KLog.i(TAG, "onVipNameClick:$it")

                            with(AppUserLogger.getInstance()) {
                                log(Clicked.InfoName)
                                log(Page.GroupSettingsVIPINFVIPName)
                            }

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

                    LaunchedEffect(key1 = selectedTab) {
                        with(AppUserLogger.getInstance()) {
                            log(Clicked.ManageInfo)
                            log(Page.GroupSettingsVIPINF)
                        }
                    }
                }

                //權限
                VipManagerViewModel.VipManageTabKind.PERMISSION -> {
                    VipPlanInfoPermissionPage(
                        permissionModels = vipPlanPermissionModels,
                        onEditPermission = { index ->
                            AppUserLogger.getInstance().log(Clicked.PermissionsChannel)

                            val permission = vipPlanPermissionModels[index]
                            navController.navigate(
                                VipPlanInfoEditChannelPermissionScreenDestination(
                                    permissionModel = permission,
                                    permissionOptionModels = vipPlanPermissionOptionModels.toTypedArray()
                                )
                            )
                        }
                    )

                    LaunchedEffect(key1 = selectedTab) {
                        with(AppUserLogger.getInstance()) {
                            log(Clicked.ManagePermissions)
                            log(Page.GroupSettingsVIPPermission)
                        }
                    }
                }

                //成員
                VipManagerViewModel.VipManageTabKind.MEMBER -> {
                    VipMemberPage(
                        members = vipMembers
                    )

                    LaunchedEffect(key1 = selectedTab) {
                        with(AppUserLogger.getInstance()) {
                            log(Clicked.ManageMembers)
                            log(Page.GroupSettingsVIPMembers)
                        }
                    }
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

/**
 * 成員頁面
 *
 * @param members 會員清單
 */
@Composable
private fun VipMemberPage(
    members: List<GroupMember>
) {
    Column {
        Text(
            modifier = Modifier.padding(
                bottom = 20.dp,
                start = 24.dp,
                end = 24.dp
            ),
            text = stringResource(id = R.string.vip_member_description),
            fontSize = 14.sp,
            color = LocalColor.current.text.default_50
        )

        if (members.isEmpty()) {
            //Empty Screen
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(105.dp),
                    painter = painterResource(id = R.drawable.flower_box),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(LocalColor.current.text.default_30)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(id = R.string.vip_member_empty_description),
                    fontSize = 16.sp,
                    color = LocalColor.current.text.default_30
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(members) { member ->
                    MemberItemScreen(
                        groupMember = member,
                        isShowRemove = false,
                    )
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun VipMemberPagePreview() {
    FanciTheme {
        VipMemberPage(
            members = VipManagerUseCase.getVipPlanInfoMockData().members
        )
    }
}


@Preview(showBackground = true)
@Composable
fun VipPlanInfoScreenPreview() {
    FanciTheme {
        VipPlanInfoMainScreenView(
            navController = EmptyDestinationsNavigator,
            vipName = "高級學員",
            selectedTab = VipManagerViewModel.VipManageTabKind.MEMBER,
            onTabSelected = {
            },
            vipPlanPermissionModels = listOf(
                VipPlanPermissionModel(
                    id = "101",
                    name = "歡迎新朋友",
                    canEdit = false,
                    permissionTitle = "公開頻道",
                    authType = ChannelAuthType.basic
                ),
                VipPlanPermissionModel(
                    id = "102",
                    name = "健身肌肉男",
                    canEdit = true,
                    permissionTitle = "進階權限",
                    authType = ChannelAuthType.basic
                )
            ),
            vipPlanPermissionOptionModels = VipManagerUseCase.getVipPlanPermissionOptionsMockData(),
            planSourceList = emptyList(),
            vipMembers = emptyList()
        )
    }
}