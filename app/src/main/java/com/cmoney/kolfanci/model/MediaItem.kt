package com.cmoney.kolfanci.model

import android.net.Uri
import com.google.common.net.MediaType

data class MediaItem(
    val uri: Uri,
    val mediaType: MediaType
)
