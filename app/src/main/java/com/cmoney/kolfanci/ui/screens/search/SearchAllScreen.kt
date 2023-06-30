package com.cmoney.kolfanci.ui.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import com.cmoney.kolfanci.ui.theme.FanciTheme

@Composable
fun SearchAllScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        //TODO
    }
}

@Preview(showBackground = true)
@Composable
fun SearchAllScreenPreview() {
    FanciTheme {
        SearchAllScreen()
    }
}