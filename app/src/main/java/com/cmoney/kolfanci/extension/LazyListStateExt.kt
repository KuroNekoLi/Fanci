package com.cmoney.kolfanci.extension

import androidx.compose.foundation.lazy.LazyListState

fun LazyListState.isScrolledToTop() = firstVisibleItemScrollOffset == 0