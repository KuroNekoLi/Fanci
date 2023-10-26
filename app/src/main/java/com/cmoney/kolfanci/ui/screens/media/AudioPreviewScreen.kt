package com.cmoney.kolfanci.ui.screens.media

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

/**
 * 音檔 預覽頁面
 */
@Destination
@Composable
fun AudioPreviewScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    uri: Uri
) {

    AudioPreviewScreenView(
        modifier = modifier,
        navController = navController,
        uri = uri
    )
}

@Composable
fun AudioPreviewScreenView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    uri: Uri
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = "音檔",
                backClick = {
                    navController.popBackStack()
                }
            )
        },
        backgroundColor = Color.Black
    ) { innerPadding ->

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SimplePlayer(
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember {
        mutableFloatStateOf(0f)
    }

    Column(
        modifier = modifier
            .background(
                color = colorResource(id = R.color.color_20262F)
            )
            .padding(
                top = 21.dp, start = 24.dp, end = 24.dp, bottom = 21.dp
            )
    ) {
        Text(
            modifier = Modifier.basicMarquee(
                iterations = Int.MAX_VALUE
            ),
            text = "跑馬燈跑馬燈跑馬燈跑馬燈跑馬燈跑馬燈跑馬跑馬燈跑馬燈跑馬燈跑馬燈跑馬燈跑馬燈跑馬跑馬燈跑馬燈跑馬燈跑馬燈跑馬燈跑馬燈跑馬",
            color = Color.White,
            fontSize = 16.sp,
            maxLines = 1,
        )

        Spacer(modifier = Modifier.height(21.dp))

        //Seekbar
        Slider(
            value = sliderPosition,
            colors = SliderDefaults.colors(
                thumbColor = LocalColor.current.primary,
                activeTrackColor = LocalColor.current.primary,
                inactiveTrackColor = LocalColor.current.background
            ),
            onValueChange = {
                sliderPosition = it
            })

        Spacer(modifier = Modifier.height(10.dp))

        //Time
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "00:00",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.White,
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "04:00",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.White,
                )
            )
        }

        //Controller
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            Image(
                modifier = Modifier.size(40.dp),
                painter = painterResource(id = R.drawable.quickplay_back),
                contentDescription = "play back"
            )

            Spacer(modifier = Modifier.width(24.dp))

            Image(
                modifier = Modifier.size(40.dp),
                painter = painterResource(id = R.drawable.play),
                contentDescription = "play back")

            Spacer(modifier = Modifier.width(24.dp))

            Image(
                modifier = Modifier.size(40.dp),
                painter = painterResource(id = R.drawable.quickplay_forward),
                contentDescription = "play forward"
            )
        }

    }

}

@Preview
@Composable
fun SimplePlayerPreview() {
    FanciTheme {
        SimplePlayer()
    }
}


@Preview
@Composable
fun AudioPreviewScreenPreview() {
    FanciTheme {
        AudioPreviewScreenView(
            uri = Uri.EMPTY,
            navController = EmptyDestinationsNavigator
        )
    }
}