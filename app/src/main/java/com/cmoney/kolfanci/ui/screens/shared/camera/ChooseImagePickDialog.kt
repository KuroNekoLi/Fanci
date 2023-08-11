package com.cmoney.kolfanci.ui.screens.shared.camera

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import com.cmoney.kolfanci.extension.getCaptureUri
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.socks.library.KLog

/**
 * 附加圖片 Dialog
 */
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
        captureUri = context.getCaptureUri()
        val captureIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                MediaStore.EXTRA_OUTPUT,
                captureUri
            )
        captureResult.launch(captureIntent)
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
                    startCameraPicker()
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
                    startImagePicker()
                }) {
                Text("從相簿中選取圖片", fontSize = 17.sp, color = Color.White)
            }
        }
    )
}

private var captureUri: Uri? = null //Camera result callback

@Preview(showBackground = true)
@Composable
fun ChooseImagePickDialogPreview() {
    FanciTheme {
        ChooseImagePickDialog(
            onDismiss = {}
        ) {}
    }
}