package com.cmoney.kolfanci.ui.screens.group.setting.member.role.sort

import android.os.Parcelable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.screens.group.setting.member.role.viewmodel.RoleManageViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.role.RoleItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.parcelize.Parcelize
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.koin.androidx.compose.koinViewModel

@Parcelize
data class SortedRole(
    val roleList: List<FanciRole>
): Parcelable

@Destination
@Composable
fun RoleSortScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    roleList: Array<FanciRole>,
    viewModel: RoleManageViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<SortedRole>
) {
    val uiState = viewModel.uiState

    RoleSortScreenView(
        modifier = modifier,
        navigator = navigator,
        roleList = roleList.toList()
    ) {
        viewModel.sortRole(
            groupId = group.id.orEmpty(),
            roleList = it
        )
    }

    //Sort complete back pre screen
    uiState.fanciRole?.let {
        resultNavigator.navigateBack(SortedRole(it))
    }
}

@Composable
private fun RoleSortScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    roleList: List<FanciRole>,
    onSave: (List<FanciRole>) -> Unit
) {
    val data = remember { mutableStateOf(roleList) }
    val state = rememberReorderableLazyListState(onMove = { from, to ->
        data.value = data.value.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    })

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "重新排列",
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    navigator.popBackStack()
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                state = state.listState,
                modifier = Modifier
                    .weight(1f)
                    .reorderable(state)
                    .detectReorderAfterLongPress(state),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(data.value, { it }) { item ->
                    ReorderableItem(state, key = item) { isDragging ->
                        val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
                        Column(
                            modifier = Modifier
                                .shadow(elevation.value)
                        ) {
                            RoleItemScreen(
                                index = 0,
                                fanciRole = item,
                                isSortMode = true,
                                onEditClick = {
                                }
                            )

                        }
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
                BlueButton(text = "儲存") {
                    onSave.invoke(data.value)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoleSortScreenPreview() {
    FanciTheme {
        RoleSortScreenView(
            navigator = EmptyDestinationsNavigator,
            roleList = listOf(
                FanciRole(
                    name = "角色1"
                ),
                FanciRole(
                    name = "角色2",
                    color = "FF38B035"
                )
            )
        ){}
    }
}