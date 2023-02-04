package com.cmoney.kolfanci.ui.common

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.cmoney.kolfanci.R

@Composable
fun CircleImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
        contentDescription = null,
        placeholder = painterResource(id = R.drawable.resource_default)
    )
}