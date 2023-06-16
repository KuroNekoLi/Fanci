package com.cmoney.kolfanci.ui.screens.tutorial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.ui.theme.Black_1AFFFFFF
import com.cmoney.kolfanci.ui.theme.Black_242424
import com.cmoney.kolfanci.ui.theme.Blue_4F70E5
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TutorialScreen(modifier: Modifier = Modifier, onStart: () -> Unit) {
    rememberSystemUiController().setStatusBarColor(
        color = Black_242424,
        darkIcons = false
    )
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        backgroundColor = Black_242424
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            val pagerState = rememberPagerState()

            HorizontalPager(
                count = 3,
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) { page ->
                TutorialItemScreen(page = page,
                    isFinalPage = (page == 2),
                    onStart = {
                        onStart.invoke()
                    })
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(
                        RoundedCornerShape(30.dp)
                    )
                    .background(Black_1AFFFFFF)
                    .padding(2.dp)
            ) {
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .padding(5.dp),
                    activeColor = Blue_4F70E5,
                    inactiveColor = Black_1AFFFFFF
                )
            }

            Spacer(modifier = Modifier.height(80.dp))

        }
    }
}

@Preview(showBackground = true)
@Composable
fun TutorialScreenPreview() {
    FanciTheme {
        TutorialScreen(
            onStart = {}
        )
    }
}