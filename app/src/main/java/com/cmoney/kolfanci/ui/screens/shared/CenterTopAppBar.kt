package com.cmoney.kolfanci.ui.screens.shared

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

private val AppBarHeight = 56.dp
private val AppBarHorizontalPadding = 4.dp
private val ContentPadding = PaddingValues(
    start = AppBarHorizontalPadding,
    end = AppBarHorizontalPadding
)

@Composable
fun CenterTopAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
    contentPadding: PaddingValues = ContentPadding,
    navController: NavController? = null,
    leading: @Composable (RowScope.() -> Unit)? = null,
    trailing: @Composable (RowScope.() -> Unit)? = null,
    title: @Composable () -> Unit,
) {
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        elevation = elevation,
        modifier = modifier
    ) {

        var leftSectionWidth = 0.dp
        var rightSectionWidth = 0.dp
        var titlePadding by remember { mutableStateOf(PaddingValues()) }

        val calculateTitlePadding = fun() {
            val dx = leftSectionWidth - rightSectionWidth
            var start = 0.dp
            var end = 0.dp
            if (dx < 0.dp) start += dx else end += dx
            titlePadding = PaddingValues(start = start, end = end)
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(contentPadding)
                .height(AppBarHeight),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // leading
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                with(LocalDensity.current) {
                    Row(
                        Modifier
                            .fillMaxHeight()
                            .onGloballyPositioned { coordinates ->
                                val width = coordinates.size.width.toDp()
                                if (width != leftSectionWidth) {
                                    leftSectionWidth = width
                                    calculateTitlePadding()
                                }
                            },
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        content = leading ?: {
                            val previous = navController?.previousBackStackEntry
                            if (previous != null) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(Icons.Filled.ArrowBack, null)
                                }
                            }
                        }
                    )
                }
            }

            // title
            Row(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(titlePadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                ProvideTextStyle(value = MaterialTheme.typography.h6) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                        content = title
                    )
                }
            }

            // trailing
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                with(LocalDensity.current) {
                    Row(
                        Modifier
                            .fillMaxHeight()
                            .onGloballyPositioned { coordinates ->
                                val width = coordinates.size.width.toDp()
                                if (width != rightSectionWidth) {
                                    rightSectionWidth = width
                                    calculateTitlePadding()
                                }
                            },
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        content = trailing ?: {}
                    )
                }
            }
        }
    }
}

// 因为标题是在布局位置确定后再次调整的，所以预览时看到标题不是居中的，实际运行App后才能
// 看到居中效果。
@Preview
@Composable
private fun DefaultPreview() {
    CenterTopAppBar(
        leading = {
            IconButton(onClick = { }) {
                Icon(Icons.Filled.ArrowBack, null)
            }
        },
        title = { Text("TitleBar Center") }
    )
}