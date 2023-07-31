package com.cmoney.kolfanci.ui.screens.shared.vip

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Group
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
    group: Group,
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
            viewModel.fetchVipPlan(
                models = selectedVipPlanModels,
                groupId = group.id.orEmpty()
            )
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
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    itemsIndexed(vipPlanModels) { index, vipPlanModel ->
                        VipPlanItemScreen(
                            modifier = modifier.fillMaxWidth(),
                            vipPlanModel = vipPlanModel,
                            endContent = {
                                CircleCheckedScreen(isChecked = index == selectedIndex)
                            },
                            onPlanClick = {
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
            contentDescription = null,
            colorFilter = ColorFilter.tint(LocalColor.current.text.default_30)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.vip_plan_empty_description),
            fontSize = 16.sp,
            color = LocalColor.current.text.default_30
        )
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
