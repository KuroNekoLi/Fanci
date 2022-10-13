package com.cmoney.fanci

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.databinding.MyFragmentLayoutBinding
import com.cmoney.fanci.ui.theme.Black_14171C
import com.cmoney.fanci.ui.theme.Blue_4F70E5
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.White_494D54

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FanciTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) { innerPadding ->
        MyAppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview(){
//    MainScreen()
//}

@Composable
fun BottomBar(navController: NavHostController) {
    Column {
        Spacer(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth()
                .background(Black_14171C),
        )
        BottomNavigation(
            backgroundColor = Black_14171C,
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

@Composable
fun MyAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: MainTab = MainTab.FOLLOW
) {
    var pos by remember { mutableStateOf(0) }

    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier,
    ) {

        mainTabItems.forEach { mainTab ->
            when (mainTab) {
                MainTab.EXPLORE -> {
                    composable(MainTab.EXPLORE.route) {
                        AndroidViewBinding(MyFragmentLayoutBinding::inflate) {
                        }
                    }
                }
                MainTab.FOLLOW -> {
                    composable(MainTab.FOLLOW.route) {
                        PageDisplay(MainTab.FOLLOW.title, pos) {
                            pos += 1
                        }
                    }
                }
                MainTab.MY -> {
                    composable(MainTab.MY.route) {
                        PageDisplay(MainTab.MY.title, pos) {
                            pos += 1
                        }
                    }
                }
                MainTab.NOTIFY -> {
                    composable(MainTab.NOTIFY.route) {
                        PageDisplay(MainTab.NOTIFY.title, pos) {
                            pos += 1
                        }
                    }
                }
                else -> {
                    composable(MainTab.FOLLOW.route) {
                        PageDisplay(MainTab.FOLLOW.title, pos) {
                            pos += 1
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PageDisplay(name: String, pos: Int, onClick: () -> Unit) {
    Column {
        Text(text = "Hello $name!$pos")
        Button(onClick = {
            onClick.invoke()
        }) {
            Text(text = "Click me")
        }
    }
}