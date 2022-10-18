package com.cmoney.fanci.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.model.mainTabItems
import com.cmoney.fanci.ui.theme.Black_14171C
import com.cmoney.fanci.ui.theme.Blue_4F70E5
import com.cmoney.fanci.ui.theme.White_494D54

@Composable
fun BottomBar(navController: NavHostController) {
    Column {
        Spacer(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary),
        )
        BottomNavigation(
            backgroundColor = MaterialTheme.colors.primary
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
                    selectedContentColor = Blue_4F70E5,
                    unselectedContentColor = White_494D54,
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
    val navController = rememberNavController()
    BottomBar(navController)
}