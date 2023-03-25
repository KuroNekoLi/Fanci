package com.cmoney.kolfanci.ui.screens.group.setting.group.channel.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.*
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.destinations.EditChannelOpennessScreenDestination
import com.cmoney.kolfanci.destinations.MemberAndRoleManageScreenDestination
import com.cmoney.kolfanci.destinations.ShareAddRoleScreenDestination
import com.cmoney.kolfanci.extension.showToast
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.screens.group.setting.group.channel.viewmodel.ChannelSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.SettingItemScreen
import com.cmoney.kolfanci.ui.screens.shared.TabScreen
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DeleteAlertDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.SelectedModel
import com.cmoney.kolfanci.ui.screens.shared.role.RoleItemScreen
import com.cmoney.kolfanci.ui.theme.Color_80FFFFFF
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
    permissionMemberResult: ResultRecipient<MemberAndRoleManageScreenDestination, SelectedModel>
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val showDeleteDialog = remember { mutableStateOf(false) }

    uiState.group?.let {
        KLog.i("TAG", "channel add.")
        resultNavigator.navigateBack(result = it)
    }

    //Edit mode
    channel?.let {
        if (viewModel.channel == null) {
            viewModel.initChannel(it)
        }
    }

    AddChannelScreenView(
        modifier,
        navigator,
        topBarTitle = (if ((channel != null)) {
            "頻道設定"
        } else {
            "新增頻道"
        }),
        buttonTitle = (if ((channel != null)) {
            "儲存變更"
        } else {
            "建立"
        }),
        withDelete = (channel != null),
        channelName = uiState.channelName,
        fanciRole = uiState.channelRole,
        group = group,
        selectedIndex = uiState.tabSelected,
        isNeedApproval = uiState.isNeedApproval,
        channelAccessTypeList = uiState.channelAccessTypeList,
        isLoading = uiState.isLoading,
        onConfirm = {
            if (it.isNotEmpty()) {
                if (channel == null) {
                    viewModel.addChannel(group, category.id.orEmpty(), it)
                } else {
                    viewModel.editChannel(
                        group = group,
                        channel = channel,
                        name = it
                    )
                }
            } else {
                context.showToast("請輸入頻道名稱")
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
        onChannelNameInput = {
            viewModel.setChannelName(it)
        },
        onDeleteClick = {
            showDeleteDialog.value = true
        }
    )

    if (uiState.channelAccessTypeList.isEmpty()) {
        viewModel.fetchChannelPermissionList()
    }

    uiState.clickPermissionMemberModel?.let {
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
            showDeleteAlert(
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
    topBarTitle: String = "新增頻道",
    buttonTitle: String = "建立",
    selectedIndex: Int,
    channelName: String,
    isNeedApproval: Boolean,
    fanciRole: List<FanciRole>?,
    group: Group,
    channelAccessTypeList: List<ChannelAccessOptionModel>,
    isLoading: Boolean,
    withDelete: Boolean,
    onConfirm: (String) -> Unit,
    onTabClick: (Int) -> Unit,
    onRemoveRole: (FanciRole) -> Unit,
    onPermissionClick: (ChannelAccessOptionModel) -> Unit,
    onChannelNameInput: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    val list = listOf("樣式", "權限", "管理員")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = topBarTitle,
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    navigator.popBackStack()
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(LocalColor.current.env_80)
        ) {
            Column(modifier = Modifier.weight(1f)) {

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

                when (selectedIndex) {
                    //樣式
                    0 -> {
                        StyleTabScreen(
                            channelName,
                            withDelete,
                            onValueChange = {
                                onChannelNameInput.invoke(it)
                            },
                            onDeleteClick = onDeleteClick
                        )
                    }
                    //權限
                    1 -> {
                        PermissionTabScreen(
                            isNeedApproval, navigator,
                            channelPermissionModel = channelAccessTypeList,
                            group = group,
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

            //========== 儲存 ==========
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .background(LocalColor.current.env_100),
                contentAlignment = Alignment.Center
            ) {
                BlueButton(
                    text = buttonTitle
                ) {
                    onConfirm.invoke(channelName)
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
    onValueChange: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    val maxLength = 10
    Row(
        modifier = Modifier.padding(
            top = 20.dp,
            start = 24.dp,
            end = 24.dp,
            bottom = 20.dp
        )
    ) {
        Text(
            text = "頻道名稱",
            fontSize = 14.sp,
            color = LocalColor.current.text.default_100
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "%d/%d".format(textState.length, maxLength),
            fontSize = 14.sp,
            color = LocalColor.current.text.default_50
        )
    }

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = textState,
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
                onValueChange.invoke(it)
            }
        },
        maxLines = 1,
        textStyle = TextStyle.Default.copy(fontSize = 16.sp),
        placeholder = {
            Text(
                text = "輸入頻道名稱",
                fontSize = 16.sp,
                color = LocalColor.current.text.default_30
            )
        }
    )

    if (withDelete) {
        Spacer(modifier = Modifier.height(35.dp))

        Text(
            modifier = Modifier.padding(start = 24.dp, bottom = 10.dp),
            text = "刪除頻道", fontSize = 14.sp, color = LocalColor.current.text.default_100
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
            Text(text = "刪除頻道", fontSize = 17.sp, color = LocalColor.current.specialColor.red)

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
    group: Group,
    channelPermissionModel: List<ChannelAccessOptionModel>,
    onPermissionClick: (ChannelAccessOptionModel) -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(10.dp))

        SettingItemScreen(
            iconRes = R.drawable.lock,
            text = "頻道公開度",
            onItemClick = {
                navigator.navigate(
                    EditChannelOpennessScreenDestination(
                        isNeedApproval = isNeedApproval
                    )
                )
            }
        ) {
            val publicText = if (isNeedApproval) {
                "不公開"
            } else {
                "完全公開"
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
            Text(text = "管理頻道成員（0人）", fontSize = 14.sp, color = Color_80FFFFFF)
            Spacer(modifier = Modifier.height(15.dp))
            channelPermissionModel.forEach { it ->
                ChannelPermissionItem(it) { channelPermissionModel ->
                    KLog.i("PermissionTabScreen", "click:$channelPermissionModel")
                    onPermissionClick.invoke(channelPermissionModel)
                }
                Spacer(modifier = Modifier.height(1.dp))
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
            .padding(top = 20.dp, bottom = 20.dp, start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier.size(35.dp),
                model = channelAccessOptionModel.icon,
                placeholder = painterResource(id = R.drawable.placeholder),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = channelAccessOptionModel.title.orEmpty(),
                fontSize = 14.sp,
                color = Color.White
            )
        }

        Column(
            modifier = Modifier.weight(2f),
            verticalArrangement = Arrangement.Center
        ) {
            for (item in channelAccessOptionModel.bullitens.orEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        modifier = Modifier.size(14.dp),
                        model = item.icon,
                        placeholder = painterResource(id = R.drawable.placeholder),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(text = item.title.orEmpty(), fontSize = 14.sp, color = Color_80FFFFFF)
                }
            }
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "編輯成員", fontSize = 14.sp, color = LocalColor.current.primary
            )
        }
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
            text = "選擇角色來建立頻道管理員：擁有該角色之成員，即可針對相對應的權限，進行頻道管理。",
            fontSize = 14.sp,
            color = LocalColor.current.component.other
        )

        BorderButton(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .height(40.dp),
            text = "新增角色",
            borderColor = Color.White
        ) {
            navigator.navigate(
                ShareAddRoleScreenDestination(
                    group = group,
                    buttonText = "新增",
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
private fun showDeleteAlert(
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
            onValueChange = {},
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
            isNeedApproval = true,
            withDelete = true,
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
            onConfirm = {},
            onTabClick = {},
            group = Group(),
            isLoading = true,
            channelName = "",
            fanciRole = emptyList(),
            onRemoveRole = {},
            onPermissionClick = {},
            onChannelNameInput = {},
            onDeleteClick = {}
        )
    }
}