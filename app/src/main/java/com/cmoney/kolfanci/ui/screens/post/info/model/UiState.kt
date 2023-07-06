package com.cmoney.kolfanci.ui.screens.post.info.model

sealed interface UiState {
    object ShowLoading : UiState
    object DismissLoading : UiState
}