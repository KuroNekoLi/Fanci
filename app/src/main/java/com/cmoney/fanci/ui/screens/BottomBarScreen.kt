package com.cmoney.fanci.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.model.MainTab
import com.cmoney.fanci.model.mainTabItems
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor

/**
 * 根據不同 Route 決定BottomBar 是否出現
 */
@Deprecated("no used.")
@Composable
fun BottomBarController(navController: NavHostController) {
    var showBottomBar by rememberSaveable { mutableStateOf(true) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    showBottomBar = when (navBackStackEntry?.destination?.route) {
        MainTab.FOLLOW.route -> true
        MainTab.NOTIFY.route -> true
        MainTab.MY.route -> true
        MainTab.ACTIVITY.route -> true
        else -> false
    }

    BottomBarScreen(navController)

//    AnimatedVisibility(
//        visible = showBottomBar,
//        enter = slideInVertically(initialOffsetY = { it }),
//        exit = slideOutVertically(targetOffsetY = { it })
//    ) {
//        BottomBar(navController)
//    }
}

@Composable
fun BottomBarScreen(navController: NavHostController) {
    Column {
        Spacer(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth()
                .background(LocalColor.current.env_100),
        )
        BottomNavigation(
            backgroundColor = LocalColor.current.env_100
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            mainTabItems.forEach { screen ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = screen.icon),
                            modifier = Modifier.padding(8.dp),
                            contentDescription = null
                        )
                    },
                    label = { Text(text = screen.title.uppercase()) },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    selectedContentColor = LocalColor.current.component.tabSelected,
                    unselectedContentColor = LocalColor.current.component.tabUnSelect,
                    onClick = {
                        navController.navigate(screen.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    FanciTheme {
        BottomBarScreen(rememberNavController())
    }
}