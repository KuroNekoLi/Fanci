package com.cmoney.fanci.ui.screens.shared.dialog

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
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
import androidx.core.content.FileProvider
import com.cmoney.fanci.ui.common.GrayButton
import com.cmoney.fanci.ui.theme.FanciTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.socks.library.KLog
import java.io.File

/**
 * 群組設定選擇圖片 彈窗
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GroupPhotoPickDialogScreen(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onAttach: (Uri) -> Unit,
) {
    val TAG = "GroupPhotoPickDialogScreen"

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val externalPermissionState =
        rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    val choosePhotoResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { uri ->
                    KLog.i(TAG, "get uri:$uri")
                    onAttach.invoke(uri)
                    onDismiss.invoke()
                }
            }
        }

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

    val context = LocalContext.current

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
                    if (externalPermissionState.status.isGranted) {
                        val intent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        intent.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*"
                        )
                        choosePhotoResult.launch(intent)
                    } else {
                        externalPermissionState.launchPermissionRequest()
                    }
                }

                GrayButton(
                    text = "打開相機",
                    shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                ) {
                    if (cameraPermissionState.status.isGranted) {
                        if (captureUri == null) {
                            captureUri = getCaptureUri(context)
                        }
                        val captureIntent =
                            Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                getCaptureUri(context)
                            )
                        captureResult.launch(captureIntent)
                    } else {
                        cameraPermissionState.launchPermissionRequest()
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

private fun getCaptureUri(context: Context): Uri {
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File(storageDir, "captureImage.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}

@Preview(showBackground = true)
@Composable
fun GroupPhotoPickDialogScreenPreview() {
    FanciTheme {
        GroupPhotoPickDialogScreen(
            onDismiss = {}
        ) {}
    }
}