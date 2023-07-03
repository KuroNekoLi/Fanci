package com.cmoney.kolfanci.ui.screens.group.setting.group.channel.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.AuthBulliten
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChannelAccessOptionModel
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.EditChannelOpennessScreenDestination
import com.cmoney.kolfanci.ui.destinations.EditInputScreenDestination
import com.cmoney.kolfanci.ui.destinations.MemberAndRoleManageScreenDestination
import com.cmoney.kolfanci.ui.destinations.ShareAddRoleScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.group.channel.viewmodel.ChannelSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.TabScreen
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DeleteAlertDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.SaveConfirmDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.SelectedModel
import com.cmoney.kolfanci.ui.screens.shared.role.RoleItemScreen
import com.cmoney.kolfanci.ui.screens.shared.setting.SettingItemScreen
import com.cmoney.kolfanci.ui.screens.shared.toolbar.EditToolbarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

/**
 * 分類下 新增/編輯 頻道
 */
@Destination
@Composable
fun AddChannelScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    category: Category,
    channel: Channel? = null,
    viewModel: ChannelSettingViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<Group>,
    approvalResult: ResultRecipient<EditChannelOpennessScreenDestination, Boolean>,
    addRoleResult: ResultRecipient<ShareAddRoleScreenDestination, String>,
    permissionMemberResult: ResultRecipient<MemberAndRoleManageScreenDestination, SelectedModel>,
    setChannelNameResult: ResultRecipient<EditInputScreenDestination, String>
) {
    val TAG = "AddChannelScreen"
    val uiState = viewModel.uiState
    val showDeleteDialog = remember { mutableStateOf(false) }
    var showSaveTip by remember {
        mutableStateOf(false)
    }

    uiState.group?.let {
        KLog.i("TAG", "channel add.")
        resultNavigator.navigateBack(result = it)
    }

    //if Edit mode
    LaunchedEffect(Unit) {
        channel?.let {
            if (viewModel.channel == null) {
                viewModel.initChannel(it)
            }
        }
    }

    AddChannelScreenView(
        modifier,
        navigator,
        channel = channel,
        selectedIndex = uiState.tabSelected,
        channelName = uiState.channelName,
        isNeedApproval = uiState.isNeedApproval,
        fanciRole = uiState.channelRole,
        group = group,
        channelAccessTypeList = uiState.channelAccessTypeList,
        isLoading = uiState.isLoading,
        withDelete = (channel != null),
        uniqueUserCount = uiState.uniqueUserCount,
        onConfirm = {
            if (channel == null) {
                viewModel.addChannel(group, category.id.orEmpty(), it)
            } else {
                viewModel.editChannel(
                    group = group,
                    name = it
                )
            }
        },
        onTabClick = {
            viewModel.onChannelSettingTabSelected(it)
        },
        onRemoveRole = {
            viewModel.onRemoveRole(it)
        },
        onPermissionClick = { channelPermissionModel ->
            viewModel.onPermissionClick(channelPermissionModel)
        },
        onDeleteClick = {
            showDeleteDialog.value = true
        },
        onBack = {
            showSaveTip = true
        }
    )

    SaveConfirmDialogScreen(
        isShow = showSaveTip,
        onContinue = {
            showSaveTip = false
        },
        onGiveUp = {
            showSaveTip = false
            navigator.popBackStack()
        }
    )

    if (uiState.channelAccessTypeList.isEmpty()) {
        viewModel.fetchChannelPermissionList()
    }

    //點擊的權限
    uiState.clickPermissionMemberModel?.let {
        KLog.i(TAG, "clickPermissionMemberModel:$it")
        navigator.navigate(
            MemberAndRoleManageScreenDestination(
                group = group,
                topBarTitle = it.first.title.orEmpty(),
                selectedModel = it.second
            )
        )
        viewModel.dismissPermissionNavigator()
    }

    //DeleteDialog
    if (showDeleteDialog.value) {
        channel?.let {
            ShowDeleteAlert(
                channelName = channel.name.orEmpty(),
                onConfirm = {
                    showDeleteDialog.value = false
                    viewModel.deleteChannel(group, channel)
                },
                onCancel = {
                    showDeleteDialog.value = false
                }
            )
        }
    }

    //========== Result callback Start ==========
    setChannelNameResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                viewModel.setChannelName(result.value)
            }
        }
    }

    approvalResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                viewModel.setChannelApproval(result.value)
            }
        }
    }

    addRoleResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                viewModel.addChannelRole(result.value)
            }
        }
    }

    permissionMemberResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                viewModel.setPermissionMemberSelected(result.value)
            }
        }
    }
    //========== Result callback End ==========
}

@Composable
fun AddChannelScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    selectedIndex: Int,
    channelName: String,
    isNeedApproval: Boolean,
    fanciRole: List<FanciRole>?,
    group: Group,
    channelAccessTypeList: List<ChannelAccessOptionModel>,
    isLoading: Boolean,
    withDelete: Boolean,
    uniqueUserCount: Int,
    onConfirm: (String) -> Unit,
    onTabClick: (Int) -> Unit,
    onRemoveRole: (FanciRole) -> Unit,
    onPermissionClick: (ChannelAccessOptionModel) -> Unit,
    onDeleteClick: () -> Unit,
    onBack: () -> Unit,
    channel: Channel?
) {
    val TAG = "AddChannelScreenView"
    val list = listOf(
        stringResource(id = R.string.style),
        stringResource(id = R.string.permission),
        stringResource(id = R.string.manager)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            if (channel != null) {
                TopBarScreen(
                    title = stringResource(id = R.string.edit_channel),
                    moreEnable = false,
                    backClick = {
                        KLog.i(TAG, "saveClick click.")
                        onConfirm.invoke(channelName)
                    }
                )
            }
            else {
                EditToolbarScreen(
                    title = stringResource(id = R.string.add_channel),
                    backClick = onBack,
                    saveClick = {
                        KLog.i(TAG, "saveClick click.")
                        onConfirm.invoke(channelName)
                    }
                )
            }
        }
    ) { padding ->

        val context = LocalContext.current

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(LocalColor.current.env_80)
        ) {
            Column(modifier = Modifier.weight(1f)) {

                if (Constant.isAddChannelPermission()) {
                    TabScreen(
                        modifier = Modifier
                            .padding(18.dp)
                            .height(40.dp),
                        selectedIndex = selectedIndex,
                        listItem = list,
                        onTabClick = {
                            onTabClick.invoke(it)
                        }
                    )
                }

                when (selectedIndex) {
                    //樣式
                    0 -> {
                        StyleTabScreen(
                            channelName,
                            withDelete,
                            onChannelNameClick = {
                                navigator.navigate(
                                    EditInputScreenDestination(
                                        defaultText = channelName,
                                        toolbarTitle = context.getString(R.string.channel_name),
                                        placeholderText = context.getString(R.string.input_channel_name),
                                        emptyAlertTitle = context.getString(R.string.channel_name_empty),
                                        emptyAlertSubTitle = context.getString(R.string.channel_name_empty_desc)
                                    )
                                )
                            },
                            onDeleteClick = onDeleteClick
                        )
                    }
                    //權限
                    1 -> {
                        PermissionTabScreen(
                            isNeedApproval, navigator,
                            uniqueUserCount = uniqueUserCount,
                            channelPermissionModel = channelAccessTypeList,
                            onPermissionClick = onPermissionClick
                        )
                    }
                    //管理員
                    2 -> {
                        ManagerTabScreen(
                            navigator = navigator,
                            group = group,
                            fanciRole = fanciRole,
                            onRemoveRole = {
                                onRemoveRole.invoke(it)
                            }
                        )
                    }

                    else -> {
                    }
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(size = 32.dp),
                    color = LocalColor.current.primary
                )
            }
        }
    }
}

/**
 * 樣式 Tab Screen
 */
@Composable
private fun StyleTabScreen(
    textState: String,
    withDelete: Boolean,
    onChannelNameClick: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    Text(
        modifier = Modifier.padding(
            top = 20.dp,
            start = 24.dp,
            end = 24.dp,
            bottom = 20.dp
        ),
        text = stringResource(id = R.string.channel_name),
        fontSize = 14.sp,
        color = LocalColor.current.text.default_100
    )

    Row(
        modifier = Modifier
            .background(LocalColor.current.background)
            .clickable {
                onChannelNameClick.invoke(textState)
            }
            .padding(
                top = 10.dp,
                bottom = 10.dp,
                start = 25.dp,
                end = 10.dp
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (textState.isEmpty()) {
            Text(
                modifier = Modifier.weight(1f),
                text = "輸入頻道名稱",
                fontSize = 17.sp,
                color = LocalColor.current.text.default_30
            )
        } else {
            Text(
                modifier = Modifier.weight(1f),
                text = textState,
                fontSize = 17.sp,
                color = LocalColor.current.text.default_100
            )
        }

        Spacer(modifier = Modifier.width(5.dp))

        Image(
            painter = painterResource(id = R.drawable.next),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = LocalColor.current.text.default_80)
        )
    }

    if (withDelete && Constant.isCanDeleteChannel()) {
        Spacer(modifier = Modifier.height(35.dp))

        Text(
            modifier = Modifier.padding(start = 24.dp, bottom = 10.dp),
            text = stringResource(id = R.string.delete_channel),
            fontSize = 14.sp,
            color = LocalColor.current.text.default_100
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(LocalColor.current.background)
                .clickable {
                    onDeleteClick.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.delete_channel),
                fontSize = 17.sp,
                color = LocalColor.current.specialColor.red
            )

        }
    }
}

/**
 * 權限 Tab Screen
 */
@Composable
private fun PermissionTabScreen(
    isNeedApproval: Boolean,
    navigator: DestinationsNavigator,
    uniqueUserCount: Int,
    channelPermissionModel: List<ChannelAccessOptionModel>,
    onPermissionClick: (ChannelAccessOptionModel) -> Unit
) {
    val TAG = "PermissionTabScreen"
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(10.dp))

        SettingItemScreen(
            iconRes = R.drawable.lock,
            text = stringResource(id = R.string.channel_openness),
            onItemClick = {
                KLog.i(TAG, "onItemClick.")

                navigator.navigate(
                    EditChannelOpennessScreenDestination(
                        isNeedApproval = isNeedApproval
                    )
                )
            }
        ) {
            val publicText = if (isNeedApproval) {
                stringResource(id = R.string.not_public)
            } else {
                stringResource(id = R.string.full_public)
            }
            Text(
                text = publicText,
                fontSize = 17.sp,
                color = if (isNeedApproval) {
                    LocalColor.current.specialColor.red
                } else {
                    LocalColor.current.text.default_100
                }
            )
        }

        if (isNeedApproval) {
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = stringResource(id = R.string.channel_manage_count).format(uniqueUserCount),
                fontSize = 14.sp,
                color = LocalColor.current.text.default_50
            )
            Spacer(modifier = Modifier.height(15.dp))
            channelPermissionModel.forEach { it ->
                ChannelPermissionItem(it) { channelPermissionModel ->
                    KLog.i("PermissionTabScreen", "click:$channelPermissionModel")
                    onPermissionClick.invoke(channelPermissionModel)
                }
                Spacer(modifier = Modifier.height(1.dp))
            }

            Spacer(modifier = Modifier.height(15.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(id = R.drawable.permission_table),
                    contentScale = ContentScale.FillWidth,
                    contentDescription = null
                )
            }
        }

    }
}

/**
 * 頻道 權限 item
 */
@Composable
private fun ChannelPermissionItem(
    channelAccessOptionModel: ChannelAccessOptionModel,
    onClick: (ChannelAccessOptionModel) -> Unit
) {
    Row(
        modifier = Modifier
            .background(LocalColor.current.background)
            .clickable {
                onClick.invoke(channelAccessOptionModel)
            }
            .fillMaxWidth()
            .height(67.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = channelAccessOptionModel.title.orEmpty(),
                fontSize = 17.sp,
                color = LocalColor.current.text.default_100
            )
            val subTitle = remember(channelAccessOptionModel.bullitens) {
                channelAccessOptionModel.bullitens?.filter { bulliten ->
                    bulliten.title != null
                }?.joinToString("，") { bulliten ->
                    bulliten.title.orEmpty()
                }.orEmpty()
            }
            Text(
                text = subTitle,
                fontSize = 14.sp,
                color = LocalColor.current.text.default_50
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.next),
            contentDescription = "manage",
            tint = LocalColor.current.text.default_100
        )
    }
}

/**
 * 管理員 Tab
 */
@Composable
private fun ManagerTabScreen(
    navigator: DestinationsNavigator,
    group: Group,
    fanciRole: List<FanciRole>?,
    onRemoveRole: (FanciRole) -> Unit
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier.padding(20.dp),
            text = stringResource(id = R.string.channel_manager_description),
            fontSize = 14.sp,
            color = LocalColor.current.component.other
        )

        BorderButton(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .height(40.dp),
            text = stringResource(id = R.string.add_role),
            borderColor = Color.White
        ) {
            navigator.navigate(
                ShareAddRoleScreenDestination(
                    group = group,
                    existsRole = fanciRole.orEmpty().toTypedArray()
                )
            )
        }

        Spacer(modifier = Modifier.height(1.dp))

        fanciRole?.let { roleList ->
            repeat(roleList.size) {
                RoleItemScreen(
                    modifier = Modifier
                        .fillMaxWidth(),
                    index = it,
                    isShowIndex = false,
                    fanciRole = roleList[it],
                    editText = "移除",
                    onEditClick = {
                        onRemoveRole.invoke(it)
                    }
                )
                Spacer(modifier = Modifier.height(1.dp))
            }
        }
    }
}

@Composable
private fun ShowDeleteAlert(
    channelName: String,
    onConfirm: () -> Unit, onCancel: () -> Unit
) {
    DeleteAlertDialogScreen(
        title = "確定刪除頻道「%s」".format(channelName),
        subTitle = "頻道刪除後，內容將會完全消失。",
        onConfirm = onConfirm,
        onCancel = onCancel
    )
}


//==================== Preview ====================
@Preview(showBackground = true)
@Composable
fun StyleTabScreenPreview() {
    FanciTheme {
        StyleTabScreen(
            "",
            true,
            onChannelNameClick = {},
            onDeleteClick = {}
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AddChannelScreenPreview() {
    FanciTheme {
        AddChannelScreenView(
            navigator = EmptyDestinationsNavigator,
            selectedIndex = 0,
            channelName = "",
            isNeedApproval = true,
            fanciRole = emptyList(),
            group = Group(),
            channelAccessTypeList = listOf(
                ChannelAccessOptionModel(
                    authType = "basic",
                    title = "基本權限",
                    icon = "https://cm-39.s3-ap-northeast-1.amazonaws.com/images/2f9d6a01-d4cd-4190-8907-67adc9e177af.png",
                    bullitens = listOf(
                        AuthBulliten(
                            title = "觀看頻道內容",
                            icon = "https://cm-39.s3-ap-northeast-1.amazonaws.com/images/14086db2-34b7-43b9-8c68-8874cd6ab44d.png",
                            isEnabled = true
                        ),
                        AuthBulliten(
                            title = "與頻道成員互動",
                            icon = "https://cm-39.s3-ap-northeast-1.amazonaws.com/images/0b135eba-cbeb-4f0f-9c4a-dde85757a9e1.png",
                            isEnabled = false
                        )
                    )
                )
            ),
            isLoading = true,
            withDelete = true,
            uniqueUserCount = 0,
            onConfirm = {},
            onTabClick = {},
            onRemoveRole = {},
            onPermissionClick = {},
            onDeleteClick = {},
            onBack = {},
            channel = null
        )
    }
}

@Preview
@Composable
fun ChannelPermissionItemPreview() {
    FanciTheme {
        ChannelPermissionItem(
            channelAccessOptionModel = ChannelAccessOptionModel(
                authType = "",
                title = "基本權限",
                icon = null,
                bullitens = listOf(
                    AuthBulliten(
                        title = "可以進入此頻道",
                        icon = null,
                        isEnabled = true
                    )
                )
            ),
            onClick = {
            }
        )
    }
}