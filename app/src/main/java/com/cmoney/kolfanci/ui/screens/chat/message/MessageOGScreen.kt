package com.cmoney.kolfanci.ui.screens.chat.message

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.openUrl
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.kedia.ogparser.OpenGraphCacheProvider
import com.kedia.ogparser.OpenGraphCallback
import com.kedia.ogparser.OpenGraphParser
import com.kedia.ogparser.OpenGraphResult

/**
 * 根據 連結 取得 OG 訊息
 */
@Composable
fun MessageOGScreen(
    url: String,
    modifier: Modifier = Modifier
) {
    var isShowContent by remember {
        mutableStateOf(false)
    }

    var ogResult by remember {
        mutableStateOf(OpenGraphResult())
    }

    val context = LocalContext.current

    //Parse OG info
    val openGraphParser = OpenGraphParser(
        object : OpenGraphCallback {
            override fun onError(error: String) {
                isShowContent = false
            }

            override fun onPostResponse(openGraphResult: OpenGraphResult) {
                isShowContent = true
                ogResult = openGraphResult
            }
        },
        showNullOnEmpty = true,
        cacheProvider = OpenGraphCacheProvider(LocalContext.current)
    )
    openGraphParser.parse(url)

    if (isShowContent) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(9.dp))
                .background(LocalColor.current.background)
                .clickable {
                    context.openUrl(url)
                }
        ) {
            Column(
                modifier = Modifier
                    .width(230.dp)
                    .padding(10.dp)
            ) {
                AsyncImage(
                    model = ogResult.image,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    placeholder = painterResource(id = R.drawable.placeholder)
                )
                Text(
                    modifier = Modifier.padding(top = 5.dp),
                    text = ogResult.title.orEmpty(),
                    fontSize = 14.sp,
                    color = LocalColor.current.primary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    modifier = Modifier.padding(top = 5.6.dp),
                    text = ogResult.description.orEmpty(),
                    fontSize = 12.sp,
                    color = LocalColor.current.text.default_100,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageArticleScreenPreview() {
    FanciTheme {
        MessageOGScreen(
            url = "https://www.youtube.com/watch?v=d8pBKXyEt_0"
        )
    }
}