package com.cmoney.kolfanci.ui.screens.shared.vip

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.usecase.VipManagerUseCase
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel
import com.cmoney.kolfanci.ui.screens.shared.CircleCheckedScreen
import com.cmoney.kolfanci.ui.screens.shared.toolbar.EditToolbarScreen
import com.cmoney.kolfanci.ui.screens.shared.vip.viewmodel.VipPlanViewModel
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun AddVipPlanScreen(
    modifier: Modifier = Modifier,
    authTitle: String,
    selectedVipPlanModels: Array<VipPlanModel>,
    viewModel: VipPlanViewModel = koinViewModel(),
    selectedCallback: ResultBackNavigator<VipPlanModel>
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val vipPlanModels by viewModel.vipPlanModels.collectAsState()

    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    AddVipPlanScreenView(
        modifier = modifier,
        authTitle = authTitle,
        isLoading = isLoading,
        vipPlanModels = vipPlanModels,
        onBack = {
            onBackPressedDispatcher?.onBackPressed()
        },
        onConfirm = { index ->
            vipPlanModels?.getOrNull(index)?.let { vipPlanModel ->
                selectedCallback.navigateBack(vipPlanModel)
            }
        }
    )

    LaunchedEffect(key1 = Unit) {
        if (vipPlanModels == null) {
            viewModel.fetchVipPlan(models = selectedVipPlanModels)
        }
    }
}

@Composable
private fun AddVipPlanScreenView(
    modifier: Modifier = Modifier,
    authTitle: String,
    vipPlanModels: List<VipPlanModel>?,
    isLoading: Boolean,
    onBack: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    val TAG = "AddVipPlanScreenView"

    var selectedIndex by remember {
        mutableStateOf(-1)
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            EditToolbarScreen(
                title = stringResource(id = R.string.add_blank_plan, authTitle),
                backClick = onBack,
                saveClick = {
                    KLog.i(TAG, "saveClick click.")
                    onConfirm.invoke(selectedIndex)
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            LoadingScreenView()
        } else if (vipPlanModels.isNullOrEmpty()) {
            EmptyVipPlanScreenView()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(vipPlanModels) { index, vipPlan ->
                        VipPlanModelItem(
                            vipPlanModel = vipPlan,
                            isChecked = index == selectedIndex,
                            onItemClick = {
                                selectedIndex = index
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingScreenView() {
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

@Composable
private fun EmptyVipPlanScreenView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(105.dp),
            painter = painterResource(id = R.drawable.flower_box),
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.vip_plan_empty_description),
            fontSize = 16.sp,
            color = LocalColor.current.text.default_30
        )
    }
}

@Composable
private fun VipPlanModelItem(
    modifier: Modifier = Modifier,
    vipPlanModel: VipPlanModel,
    isChecked: Boolean,
    onItemClick: () -> Unit
) {
    Row(
        modifier = modifier
            .background(LocalColor.current.background)
            .clickable {
                onItemClick()
            }
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(28.dp))
        Image(
            modifier = Modifier.size(28.dp),
            painter = painterResource(id = vipPlanModel.planIcon),
            contentDescription = "vip plan icon"
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
            val memberCount = stringResource(id = R.string.n_member, vipPlanModel.memberCount)
            Text(
                text = memberCount,
                fontSize = 12.sp,
                color = LocalColor.current.text.default_50
            )
        }
        CircleCheckedScreen(isChecked = isChecked)
    }
}

@Preview
@Composable
fun AddVipPlanScreenViewPreview() {
    FanciTheme {
       AddVipPlanScreenView(
           authTitle = "基本方案",
           vipPlanModels = VipManagerUseCase.getVipPlanMockData(),
           isLoading = false,
           onBack = {
           },
           onConfirm = {
           }
       )
    }
}

@Preview
@Composable
fun LoadingScreenViewPreview() {
    FanciTheme {
        LoadingScreenView()
    }
}

@Preview
@Composable
fun EmptyVipPlanScreenViewPreview() {
    FanciTheme {
        EmptyVipPlanScreenView()
    }
}

@Preview
@Composable
fun VipPlanModelItemPreview() {
    FanciTheme {
        Column {
            VipPlanModelItem(
                vipPlanModel = VipPlanModel(
                    id = "0",
                    name = "基本權限",
                    memberCount = 10,
                    planIcon = R.drawable.vip_diamond,
                    description = ""
                ),
                isChecked = true,
                onItemClick = {
                }
            )
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(LocalColor.current.env_80)
            )
            VipPlanModelItem(
                vipPlanModel = VipPlanModel(
                    id = "1",
                    name = "進階權限",
                    memberCount = 5,
                    planIcon = R.drawable.vip_diamond,
                    description = ""
                ),
                isChecked = false,
                onItemClick = {
                }
            )
        }
    }
}
