package com.cmoney.fanci.ui.screens.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.theme.Black_202327
import com.cmoney.fanci.ui.theme.Blue_4F70E5
import com.cmoney.fanci.ui.theme.White_494D54
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.socks.library.KLog
import java.io.File

/**
 * 聊天室 輸入匡
 */
@Composable
fun MessageInput(
    onMessageSend: (text: String) -> Unit,
    onAttach: (Uri) -> Unit
) {
    val openDialog = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(MaterialTheme.colors.primary),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp, start = 16.dp)
                .size(41.dp)
                .clip(CircleShape)
                .background(Black_202327)
                .clickable {
                    openDialog.value = true
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(19.dp),
                painter = painterResource(id = R.drawable.plus), contentDescription = null
            )
        }

        var textState by remember { mutableStateOf("") }
        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(10.dp),
            value = textState,
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                backgroundColor = Black_202327,
                cursorColor = Color.White,
                disabledLabelColor = Color.DarkGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            onValueChange = {
                textState = it
            },
            shape = RoundedCornerShape(40.dp),
            maxLines = 5,
            textStyle = TextStyle.Default.copy(fontSize = 16.sp),
            placeholder = { Text(text = "輸入你想說的話...", fontSize = 16.sp, color = White_494D54) }
        )

        IconButton(
            onClick = {
                onMessageSend.invoke(textState)
                textState = ""
            },
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp, end = 16.dp)
                .size(41.dp)
                .clip(CircleShape)
                .background(Blue_4F70E5),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.send),
                contentDescription = null,
                tint = Color.White
            )
        }
    }

    if (openDialog.value) {
        chooseImagePickDialog(onDismiss = {
            openDialog.value = false
        }) {
            onAttach.invoke(it)
        }
    }
}

/**
 * 附加圖片 Dialog
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun chooseImagePickDialog(
    onDismiss: () -> Unit,
    onAttach: (Uri) -> Unit,
) {
    val TAG = "chooseImagePickDialog"

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    val externalPermissionState =
        rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)

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

    AlertDialog(onDismissRequest = { onDismiss.invoke() },
        //Camera Button
        dismissButton = {
            Button(
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp, end = 10.dp)
                    .fillMaxWidth(),
                onClick = {
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
fun MessageInputPreview() {
    MessageInput(
        {},
        {}
    )
}