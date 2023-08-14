package com.cmoney.kolfanci.ui.screens.shared.dialog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.cmoney.kolfanci.extension.getCaptureUri
import com.cmoney.kolfanci.ui.common.GrayButton
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.socks.library.KLog

/**
 * 群組設定選擇圖片 彈窗
 * @param isShowFanciPic 是否呈現Fanci 預設圖庫
 */
@Composable
fun GroupPhotoPickDialogScreen(
    modifier: Modifier = Modifier,
    isShowFanciPic: Boolean = true,
    onDismiss: () -> Unit,
    onAttach: (Uri) -> Unit,
    onFanciClick: () -> Unit
) {
    val TAG = "GroupPhotoPickDialogScreen"
    val context = LocalContext.current

    val captureResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                captureUri?.let { uri ->
                    KLog.i(TAG, "get uri:$uri")
                    onAttach.invoke(uri)
                    onDismiss.invoke()
                }
            }
        }

    /**
     * 啟動相機頁面
     */
    fun startCameraPicker() {
        captureUri = context.getCaptureUri()
        val captureIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                MediaStore.EXTRA_OUTPUT,
                captureUri
            )
        captureResult.launch(captureIntent)
    }

    val choosePhotoLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { uri ->
                    KLog.i(TAG, "get uri:$uri")
                    onAttach.invoke(uri)
                    onDismiss.invoke()
                }
            }
        }

    /**
     * 啟動相簿選相片
     */
    fun startImagePicker() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setDataAndType(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "image/*"
        )
        choosePhotoLauncher.launch(intent)
    }

    Dialog(onDismissRequest = {
        onDismiss()
    }) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier.padding(bottom = 25.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GrayButton(
                    text = "從相簿中選取圖片",
                    shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                ) {
                    startImagePicker()
                }

                GrayButton(
                    text = "打開相機",
                    shape = if (isShowFanciPic) {
                        RoundedCornerShape(0.dp)
                    } else {
                        RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                    }
                ) {
                    startCameraPicker()
                }

                if (isShowFanciPic) {
                    GrayButton(
                        text = "從Fanci圖庫中選取圖片",
                        shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                    ) {
                        onFanciClick.invoke()
                    }
                }


                Spacer(modifier = Modifier.height(20.dp))

                GrayButton(
                    text = "返回"
                ) {
                    onDismiss()
                }
            }
        }
    }
}

private var captureUri: Uri? = null //Camera result callback

@Preview(showBackground = true)
@Composable
fun GroupPhotoPickDialogScreenPreview() {
    FanciTheme {
        GroupPhotoPickDialogScreen(
            onDismiss = {},
            onAttach = {}
        ) {}
    }
}