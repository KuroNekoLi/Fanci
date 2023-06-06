package com.cmoney.kolfanci.ui.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 共用 Tab 畫面
 */
@Composable
fun TabScreen(
    modifier: Modifier = Modifier,
    listItem: List<String>,
    selectedIndex: Int,
    onTabClick: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier
            .padding(1.dp)
            .clip(RoundedCornerShape(35)),
        indicator = {
            Box {}
        },
        backgroundColor = LocalColor.current.env_100
    ) {
        listItem.forEachIndexed { index, text ->
            val selected = selectedIndex == index
            Column {
                Spacer(modifier = Modifier.height(3.dp))
                Tab(
                    modifier = if (selected) Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(35))
                        .background(
                            LocalColor.current.env_60
                        )
                    else Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(35))
                        .background(
                            Color.Transparent
                        ),
                    selected = selected,
                    onClick = {
                        onTabClick.invoke(index)
                    },
                    text = {
                        Text(
                            text = text,
                            color = LocalColor.current.text.default_100,
                            fontSize = 14.sp
                        )
                    }
                )
                Spacer(modifier = Modifier.height(3.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TabScreenPreview() {
    FanciTheme {
        TabScreen(
            modifier = Modifier
                .padding(18.dp)
                .height(40.dp)
                ,
            listItem = listOf("樣式", "權限", "管理員"),
            selectedIndex = 1,
            onTabClick = {}
        )
    }
}