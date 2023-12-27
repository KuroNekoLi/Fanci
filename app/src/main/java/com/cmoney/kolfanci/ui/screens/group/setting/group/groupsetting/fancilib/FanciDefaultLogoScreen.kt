package com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.fancilib

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.toolbar.TopBarScreen
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

/**
 * 預設Logo圖庫
 */
@Destination
@Composable
fun FanciDefaultLogoScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    viewModel: GroupSettingViewModel = koinViewModel(),
    fanciResultNavigator: ResultBackNavigator<String>
) {
    val TAG = "FanciDefaultLogoScreen"
    val state = viewModel.uiState

    FanciDefaultLogoView(
        modifier = modifier,
        navController = navController,
        imageUrl = state.groupLogoLib,
        isLoading = state.isLoading
    ) {
        KLog.i(TAG, "image click:$it")
        fanciResultNavigator.navigateBack(it)
    }

    LaunchedEffect(Unit) {
        viewModel.fetchFanciLogoLib()
    }
}

@Composable
fun FanciDefaultLogoView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    imageUrl: List<String>,
    isLoading: Boolean = false,
    onImageClick: (String) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "Fanci圖庫",
                backClick = {
                    navController.popBackStack()
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(imageUrl.size) { index ->
                    AsyncImage(
                        model = imageUrl[index],
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable {
                                onImageClick.invoke(imageUrl[index])
                            },
                        contentScale = ContentScale.Fit,
                        contentDescription = null,
                        placeholder = painterResource(id = R.drawable.placeholder)
                    )
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(45.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Preview
@Composable
fun FanciDefaultLogoScreenPreview() {
    FanciDefaultLogoView(
        navController = EmptyDestinationsNavigator,
        imageUrl = listOf("1", "2", "3", "4"),
    ) {

    }
}