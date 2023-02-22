package com.cmoney.kolfanci.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import com.cmoney.kolfanci.ui.theme.Blue_4F70E5
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

    var openGraphResult by remember {
        mutableStateOf(OpenGraphResult())
    }

    val context = LocalContext.current

    //Parse OG info
    val openGraphParser = OpenGraphParser(
        object : OpenGraphCallback {
            override fun onError(error: String) {
                isShowContent = false
            }

            override fun onPostResponse(result: OpenGraphResult) {
                isShowContent = true
                openGraphResult = result
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
                    model = openGraphResult.image,
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
                    text = openGraphResult.title.orEmpty(),
                    fontSize = 14.sp,
                    color = Blue_4F70E5,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    modifier = Modifier.padding(top = 5.6.dp),
                    text = openGraphResult.description.orEmpty(),
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
    MessageOGScreen(
        url = "https://www.youtube.com/watch?v=d8pBKXyEt_0"
    )
}