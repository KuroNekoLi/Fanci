package com.cmoney.kolfanci.ui.screens.shared.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.socks.library.KLog
import java.io.File

/**
 * 附加圖片 Dialog
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChooseImagePickDialog(
    onDismiss: () -> Unit,
    onAttach: (Uri) -> Unit,
) {
    val TAG = "chooseImagePickDialog"

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
        if (captureUri == null) {
            captureUri =
                getCaptureUri(context)
        }
        val captureIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                MediaStore.EXTRA_OUTPUT,
                getCaptureUri(context)
            )
        captureResult.launch(captureIntent)
    }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA) { granted ->
        if (granted) {
            startCameraPicker()
        }
    }

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
        choosePhotoResult.launch(intent)
    }

    val externalPermissionState =
        rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE) { granted ->
            if (granted) {
                startImagePicker()
            }
        }

    AlertDialog(
        backgroundColor = LocalColor.current.env_80,
        onDismissRequest = { onDismiss.invoke() },
        //Camera Button
        dismissButton = {
            Button(
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp, end = 10.dp)
                    .fillMaxWidth(),
                onClick = {
                    if (cameraPermissionState.status.isGranted) {
                        startCameraPicker()
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                }) {
                Text("打開相機", fontSize = 17.sp, color = Color.White)
            }
        },
        //Image Picker Button
        confirmButton = {
            Button(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                    .fillMaxWidth(),
                onClick = {
                    if (externalPermissionState.status.isGranted) {
                        startImagePicker()
                    } else {
                        externalPermissionState.launchPermissionRequest()
                    }
                }) {
                Text("從相簿中選取圖片", fontSize = 17.sp, color = Color.White)
            }
        }
    )
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
fun ChooseImagePickDialogPreview() {
    FanciTheme {
        ChooseImagePickDialog(
            onDismiss = {}
        ) {}
    }
}