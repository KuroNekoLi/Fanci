package com.cmoney.kolfanci.ui.screens.shared.bottomSheet.mediaPicker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.getCaptureUri
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.UploadFileItem
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.screens.chat.attachment.AttachImageDefault
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.socks.library.KLog
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * 使用附加檔案的環境
 */
sealed class AttachmentEnv {
    /**
     * 聊天
     */
    object Chat : AttachmentEnv()

    /**
     * 貼文
     */
    object Post : AttachmentEnv()
}

/**
 *  附加檔案 底部選單
 *  @param state 控制 bottom 是否出現
 *  @param attachmentEnv 使用附加檔案環境 (ex: 在聊天下使用 / 在貼文下使用)
 *  @param isOnlyPhotoSelector 是否只有圖片,相機 選擇功能
 *  @param selectedAttachment 已經選擇的檔案
 *  @param onAttach callback
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MediaPickerBottomSheet(
    modifier: Modifier = Modifier,
    state: ModalBottomSheetState,
    attachmentEnv: AttachmentEnv = AttachmentEnv.Chat,
    isOnlyPhotoSelector: Boolean = false,
    selectedAttachment: Map<AttachmentType, List<UploadFileItem>>,
    viewModel: MediaPickerBottomSheetViewModel = koinViewModel(),
    onAttach: (List<Uri>) -> Unit
) {
    val TAG = "MediaPickerBottomSheet"

    val coroutineScope = rememberCoroutineScope()

    var showPhotoPicker by remember {
        mutableStateOf(false)
    }

    var showTakePhoto by remember {
        mutableStateOf(false)
    }

    var showFilePicker by remember {
        mutableStateOf(false)
    }

    var showAlertDialog: Pair<String, String>? by remember {
        mutableStateOf(null)
    }

    fun hideBottomSheet() {
        coroutineScope.launch {
            state.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetState = state,
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        sheetContent = {
            MediaPickerBottomSheetView(
                modifier = modifier,
                isOnlyPhotoSelector = isOnlyPhotoSelector,
                onImageClick = {
                    viewModel.photoPickCheck(
                        selectedAttachment = selectedAttachment,
                        attachmentEnv = attachmentEnv,
                        onOpen = {
                            showPhotoPicker = true
                        },
                        onError = { title, desc ->
                            showAlertDialog = Pair(title, desc)
                        }
                    )
                },
                onCameraClick = {
                    viewModel.photoPickCheck(
                        selectedAttachment = selectedAttachment,
                        attachmentEnv = attachmentEnv,
                        onOpen = {
                            showTakePhoto = true
                        },
                        onError = { title, desc ->
                            showAlertDialog = Pair(title, desc)
                        }
                    )
                },
                onFileClick = {
                    viewModel.filePickCheck(
                        selectedAttachment = selectedAttachment,
                        attachmentEnv = attachmentEnv,
                        onOpen = {
                            showFilePicker = true
                        },
                        onError = { title, desc ->
                            showAlertDialog = Pair(title, desc)
                        }
                    )
                }
            )
        }
    ) {}

    //顯示錯誤彈窗
    showAlertDialog?.let {
        val title = it.first
        val desc = it.second

        DialogScreen(
            title = title,
            subTitle = desc,
            onDismiss = {
                showAlertDialog = null
            }
        ) {
            BlueButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = stringResource(id = R.string.back)
            ) {
                hideBottomSheet()
                showAlertDialog = null
            }
        }
    }

    //========== show all picker start==========
    if (showPhotoPicker) {
        val quantityLimit =
            AttachImageDefault.DEFAULT_QUANTITY_LIMIT - (selectedAttachment[AttachmentType.Image]?.size
                ?: 0)
        PicturePicker(
            quantityLimit = quantityLimit.coerceIn(1, AttachImageDefault.DEFAULT_QUANTITY_LIMIT),
            onAttach = {
                onAttach.invoke(it)
                showPhotoPicker = false
                hideBottomSheet()
            },
            onNothing = {
                showPhotoPicker = false
            }
        )
    }

    if (showTakePhoto) {
        TakePhoto(
            onAttach = {
                onAttach.invoke(it)
                showTakePhoto = false
                hideBottomSheet()
            },
            onNothing = {
                showTakePhoto = false
            }
        )
    }

    if (showFilePicker) {
        FilePicker(
            onAttach = {
                onAttach.invoke(it)
                showFilePicker = false
                hideBottomSheet()
            },
            onNothing = {
                showFilePicker = false
            }
        )
    }
    //========== show all picker end==========
}

@Composable
fun MediaPickerBottomSheetView(
    modifier: Modifier = Modifier,
    isOnlyPhotoSelector: Boolean = false,
    onImageClick: () -> Unit,
    onCameraClick: () -> Unit,
    onFileClick: () -> Unit
) {
    Column(
        modifier = modifier
            .background(color = LocalColor.current.env_80)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onImageClick.invoke()
                }
                .padding(
                    top = 30.dp,
                    bottom = 10.dp,
                    start = 24.dp,
                    end = 24.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.gallery),
                contentDescription = "gallery",
                tint = LocalColor.current.text.default_100
            )

            Spacer(modifier = Modifier.width(15.dp))

            Text(
                text = "上傳相片",
                style = TextStyle(fontSize = 17.sp, color = LocalColor.current.text.default_100)
            )
        }

        Divider(
            color = LocalColor.current.background,
            thickness = 1.dp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onCameraClick.invoke()
                }
                .padding(
                    top = 10.dp,
                    bottom = 10.dp,
                    start = 24.dp,
                    end = 24.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.camera),
                contentDescription = "camera",
                tint = LocalColor.current.text.default_100
            )

            Spacer(modifier = Modifier.width(15.dp))

            Text(
                text = "打開相機",
                style = TextStyle(fontSize = 17.sp, color = LocalColor.current.text.default_100)
            )
        }

        if (!isOnlyPhotoSelector) {
            Divider(
                color = LocalColor.current.background,
                thickness = 1.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onFileClick.invoke()
                    }
                    .padding(
                        top = 10.dp,
                        bottom = 30.dp,
                        start = 24.dp,
                        end = 24.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.file),
                    contentDescription = "file",
                    tint = LocalColor.current.text.default_100
                )

                Spacer(modifier = Modifier.width(15.dp))

                Text(
                    text = "上傳檔案",
                    style = TextStyle(fontSize = 17.sp, color = LocalColor.current.text.default_100)
                )
            }
        }
    }
}

/**
 * 啟動 圖片 選擇器
 */
@Composable
fun PicturePicker(
    quantityLimit: Int = AttachImageDefault.getQuantityLimit(),
    onAttach: (List<Uri>) -> Unit,
    onNothing: () -> Unit
) {
    val TAG = "PicturePicker"
    val choosePhotoLauncher = if (quantityLimit == 1) {
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { photoUri ->
            KLog.i(TAG, "get uri: $photoUri")
            if (photoUri != null) {
                onAttach.invoke(listOf(photoUri))
            } else {
                onNothing.invoke()
            }
        }
    } else {
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxItems = quantityLimit)) { photoUris ->
            KLog.i(TAG, "get uris:${photoUris.joinToString { it.toString() }}")
            if (photoUris.isNotEmpty()) {
                onAttach.invoke(photoUris)
            } else {
                onNothing.invoke()
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        choosePhotoLauncher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}

/**
 * 拍照
 */
@Composable
fun TakePhoto(
    onAttach: (List<Uri>) -> Unit,
    onNothing: () -> Unit
) {
    val TAG = "TakePhoto"
    val context = LocalContext.current
    var captureUri: Uri? = null //Camera result callback
    val captureResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                captureUri?.let { uri ->
                    KLog.i(TAG, "get uri:$uri")
                    onAttach.invoke(listOf(uri))
                } ?: run { onNothing.invoke() }
            } else {
                onNothing.invoke()
            }
        }

    LaunchedEffect(key1 = Unit) {
        captureUri = context.getCaptureUri()
        val captureIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                MediaStore.EXTRA_OUTPUT,
                captureUri
            )
        captureResult.launch(captureIntent)
    }
}

/**
 * 選擇檔案
 */
@Composable
fun FilePicker(
    onAttach: (List<Uri>) -> Unit,
    onNothing: () -> Unit
) {
    val filePickerLaunch =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { uri ->
                    onAttach.invoke(listOf(uri))
                } ?: run { onNothing.invoke() }
            } else {
                onNothing.invoke()
            }
        }

    LaunchedEffect(key1 = Unit) {
        val intent = Intent()
        intent.type = "*/*"
        intent.putExtra(
            Intent.EXTRA_MIME_TYPES,
            arrayOf("image/*", "application/pdf", "application/txt", "audio/*", "text/plain")
        )

        intent.action = Intent.ACTION_GET_CONTENT
        filePickerLaunch.launch(intent)
    }
}

@Preview
@Composable
fun MediaPickerBottomSheetPreview() {
    FanciTheme {
        MediaPickerBottomSheetView(
            onImageClick = {},
            onCameraClick = {},
            onFileClick = {}
        )
    }
}
