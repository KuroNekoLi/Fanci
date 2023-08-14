package com.cmoney.kolfanci.extension

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.cmoney.kolfanci.model.viewmodel.GroupViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * 取得共用的 GroupViewModel
 *
 * @return ViewModelStoreOwner 為 Activity 的 GroupViewModel
 */
@Composable
fun globalGroupViewModel(): GroupViewModel {
    val context = LocalContext.current
    return koinViewModel(
        viewModelStoreOwner = (context as? ComponentActivity) ?: checkNotNull(LocalViewModelStoreOwner.current),
    )
}