package com.cmoney.fanci.ui.screens.group.create

import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.ui.screens.group.create.state.CreateGroupState
import com.cmoney.fanci.ui.screens.group.create.state.rememberCreateGroupState
import com.cmoney.fanci.ui.screens.shared.TopBarScreen

@Composable
fun CreateGroupScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    state: CreateGroupState = rememberCreateGroupState()
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBarScreen(
                state.navController,
                title = "建立社團",
                leadingEnable = true,
                leadingIcon = Icons.Filled.ArrowBack ,
                trailingEnable = true,
                moreEnable = false,
                moreClick = null
            )
        }
    ) {

    }
}

@Preview(showBackground = true)
@Composable
fun CreateGroupScreenPreview() {
    CreateGroupScreen(navController = rememberNavController())
}