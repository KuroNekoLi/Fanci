package com.cmoney.fanci.extension

import android.os.Bundle
import androidx.navigation.NavHostController

/**
 * 返回指定的route並帶參數
 */
fun NavHostController.goBackRouteWithParams(
    route: String,
    autoPop: Boolean = true,
    callback: (Bundle.() -> Unit)? = null,
) {
    getBackStackEntry(route).arguments?.let {
        callback?.invoke(it)
    }
    if (autoPop) {
        popBackStack()
    }
}

/**
 * 回到上個頁面並帶參數
 */
fun NavHostController.goBackWithParams(
    autoPop: Boolean = true,
    callback: (Bundle.() -> Unit)? = null,
) {
    previousBackStackEntry?.arguments?.let {
        callback?.invoke(it)
    }
    if (autoPop) {
        popBackStack()
    }
}