package com.cmoney.kolfanci.ui.screens.post.edit

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.ReSendFile
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.model.usecase.AttachmentController
import com.cmoney.kolfanci.model.viewmodel.AttachmentViewModel
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.screens.chat.ReSendFileDialog
import com.cmoney.kolfanci.ui.screens.post.edit.attachment.PostAttachmentScreen
import com.cmoney.kolfanci.ui.screens.post.edit.viewmodel.EditPostViewModel
import com.cmoney.kolfanci.ui.screens.post.edit.viewmodel.UiState
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
import com.cmoney.kolfanci.ui.screens.shared.CenterTopAppBar
import com.cmoney.kolfanci.ui.screens.shared.ChatUsrAvatarScreen
import com.cmoney.kolfanci.ui.screens.shared.bottomSheet.mediaPicker.FilePicker
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.PhotoPickDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.SaveConfirmDialogScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.xlogin.XLoginHelper
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * 建立/編輯 貼文
 *
 * @param channelId 頻道id
 * @param editPost 要編輯的貼文物件 (option)
 */
@Destination
@Composable
fun EditPostScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    channelId: String,
    editPost: BulletinboardMessage? = null,
    viewModel: EditPostViewModel = koinViewModel(
        parameters = {
            parametersOf(channelId)
        }
    ),
    attachmentViewModel: AttachmentViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<PostViewModel.BulletinboardMessageWrapper>
) {
    var showImagePick by remember { mutableStateOf(false) }

    var showFilePicker by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    val onSuccess by viewModel.postSuccess.collectAsState()

    var showSaveTip by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    //附加檔案
    val attachment by attachmentViewModel.attachmentList.collectAsState()

    //是否呈現上傳中畫面
    var isLoading by remember {
        mutableStateOf(false)
    }

    //User 輸入內容
    var inputContent by remember {
        mutableStateOf("")
    }

    //編輯貼文, 設定初始化資料
    LaunchedEffect(Unit) {
        editPost?.let { post ->
            post.content?.medias?.apply {
                attachmentViewModel.addAttachment(this)
            }
        }
    }

    //是否全部File上傳完成
    val attachmentUploadFinish by attachmentViewModel.uploadComplete.collectAsState()

    //有上傳失敗的檔案
    val hasUploadFailedFile by attachmentViewModel.uploadFailed.collectAsState()

    if (attachmentUploadFinish.first) {
        isLoading = false
        val text = attachmentUploadFinish.second?.toString().orEmpty()

        if (editPost != null) {
            viewModel.onUpdatePostClick(editPost, text, attachment)
        } else {
            viewModel.onPost(text, attachment)
        }

        attachmentViewModel.finishPost()
    }

    var reSendFileClick by remember {
        mutableStateOf<ReSendFile?>(null)
    }

    EditPostScreenView(
        modifier = modifier,
        editPost = editPost,
        onShowImagePicker = {
            AppUserLogger.getInstance().log(Clicked.PostSelectPhoto)
            showImagePick = true
        },
        onPostClick = { text ->
            inputContent = text
            AppUserLogger.getInstance().log(Clicked.PostPublish)
            isLoading = true
            attachmentViewModel.upload(
                other = text
            )
        },
        onBack = {
            showSaveTip = true
        },
        showLoading = isLoading,
        onAttachmentFilePicker = {
            showFilePicker = true
        },
        attachment = attachment,
        onDeleteAttach = {
            attachmentViewModel.removeAttach(it)
        },
        onResend = {
            reSendFileClick = it
        },
        onPreviewAttachmentClick = { uri ->
            AttachmentController.onAttachmentClick(
                navController = navController,
                uri = uri,
                context = context,
                attachmentType = attachmentViewModel.getAttachmentType(uri)
            )
        }
    )

    //Show image picker
    if (showImagePick) {
        PhotoPickDialogScreen(
            onDismiss = {
                showImagePick = false
            },
            onAttach = {
                showImagePick = false
                attachmentViewModel.attachment(it)
            }
        )
    }

    if (showFilePicker) {
        FilePicker(
            onAttach = {
                attachmentViewModel.attachment(it)
                showFilePicker = false
            },
            onNothing = {
                showFilePicker = false
            }
        )
    }

    uiState?.apply {
        when (uiState) {
            UiState.ShowEditTip -> {
                DialogScreen(
                    title = "文章內容空白",
                    subTitle = "文章內容不可以是空白的唷！",
                    onDismiss = { viewModel.dismissEditTip() }
                ) {
                    BlueButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        text = "修改",
                    ) {
                        run {
                            AppUserLogger.getInstance().log(Clicked.PostBlankPostEdit)
                            viewModel.dismissEditTip()
                        }
                    }
                }
            }

            else -> {}
        }
    }

    //on post success
    onSuccess?.apply {
        resultNavigator.navigateBack(
            PostViewModel.BulletinboardMessageWrapper(
                message = this,
                isPin = editPost != null
            )
        )
    }

    //上傳失敗 彈窗
    if (hasUploadFailedFile) {
        isLoading = false
        DialogScreen(
            title = stringResource(id = R.string.post_fail_title),
            subTitle = stringResource(id = R.string.post_fail_desc),
            onDismiss = {
                attachmentViewModel.clearUploadFailed()
            },
            content = {
                BlueButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = stringResource(id = R.string.back)
                ) {
                    attachmentViewModel.clearUploadFailed()
                }
            }
        )
    }

    //重新發送  彈窗
    reSendFileClick?.let { reSendFile ->
        val file = reSendFile.file
        ReSendFileDialog(
            reSendFile = reSendFile,
            onDismiss = {
                reSendFileClick = null
            },
            onRemove = {
                attachmentViewModel.removeAttach(file)
                reSendFileClick = null
            },
            onResend = {
                attachmentViewModel.onResend(
                    uploadFileItem = reSendFile,
                    other = inputContent
                )
                reSendFileClick = null
            }
        )
    }

    SaveConfirmDialogScreen(
        isShow = showSaveTip,
        title = "是否放棄編輯的內容？",
        content = "貼文尚未發布就離開，文字將會消失",
        onContinue = {
            AppUserLogger.getInstance().log(Clicked.PostContinueEditing)
            showSaveTip = false
        },
        onGiveUp = {
            AppUserLogger.getInstance().log(Clicked.PostDiscard)
            showSaveTip = false
            navController.popBackStack()
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun EditPostScreenView(
    modifier: Modifier = Modifier,
    editPost: BulletinboardMessage? = null,
    onShowImagePicker: () -> Unit,
    onAttachmentFilePicker: () -> Unit,
    onPostClick: (String) -> Unit,
    onBack: () -> Unit,
    showLoading: Boolean,
    attachment: List<Pair<AttachmentType, AttachmentInfoItem>>,
    onDeleteAttach: (Uri) -> Unit,
    onPreviewAttachmentClick: (Uri) -> Unit,
    onResend: (ReSendFile) -> Unit
) {
    val defaultContent = editPost?.content?.text.orEmpty()
    var textState by remember { mutableStateOf(defaultContent) }
    val focusRequester = remember { FocusRequester() }
    val showKeyboard = remember { mutableStateOf(true) }
    val keyboard = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopBarView(
                isEdit = (editPost != null),
                backClick = {
                    onBack.invoke()
                },
                postClick = {
                    onPostClick.invoke(textState)
                }
            )
        },
        backgroundColor = LocalColor.current.background
    ) { innerPadding ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(LocalColor.current.background)
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {

            Column {
                //Avatar
                ChatUsrAvatarScreen(
                    modifier = Modifier.padding(20.dp),
                    user = GroupMember(
                        thumbNail = XLoginHelper.headImagePath,
                        name = XLoginHelper.nickName
                    )
                )

                //Edit
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .focusRequester(focusRequester),
                    value = textState,
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = LocalColor.current.text.default_100,
                        backgroundColor = LocalColor.current.background,
                        cursorColor = LocalColor.current.primary,
                        disabledLabelColor = LocalColor.current.text.default_30,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    onValueChange = {
                        textState = it
                    },
                    shape = RoundedCornerShape(4.dp),
                    textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                    placeholder = {
                        Text(
                            text = "輸入你想說的話...",
                            fontSize = 16.sp,
                            color = LocalColor.current.text.default_30
                        )
                    }
                )

                //附加檔案
                PostAttachmentScreen(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 1.dp)
                        .background(MaterialTheme.colors.primary),
                    attachment = attachment,
                    onDelete = onDeleteAttach,
                    onClick = onPreviewAttachmentClick,
                    onAddImage = onShowImagePicker,
                    onResend = onResend
                )

                //Bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .background(LocalColor.current.env_100)
                        .padding(top = 10.dp, bottom = 10.dp, start = 18.dp, end = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    //Image picker
                    Row(
                        modifier.clickable {
                            onShowImagePicker.invoke()
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(id = R.drawable.gallery),
                            colorFilter = ColorFilter.tint(LocalColor.current.text.default_100),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = "圖片",
                            style = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                color = LocalColor.current.text.default_80
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(15.dp))

                    //File picker
                    Row(
                        modifier.clickable {
                            onAttachmentFilePicker.invoke()
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(id = R.drawable.file),
                            colorFilter = ColorFilter.tint(LocalColor.current.text.default_100),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = "檔案",
                            style = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                color = LocalColor.current.text.default_80
                            )
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        modifier = Modifier.size(25.dp),
                        onClick = {
                            showKeyboard.value = !showKeyboard.value
                        }) {
                        Icon(
                            ImageVector.vectorResource(id = R.drawable.keyboard),
                            null,
                            tint = if (showKeyboard.value) {
                                LocalColor.current.text.default_100
                            } else {
                                LocalColor.current.text.default_50
                            }
                        )
                    }
                }
            }

            if (showLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = colorResource(id = R.color.color_9920262F))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { },
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(45.dp),
                        color = LocalColor.current.primary
                    )
                }
            }
        }
    }

    LaunchedEffect(showKeyboard.value) {
        if (showKeyboard.value) {
            focusRequester.requestFocus()
            delay(100) // Make sure you have delay here
            keyboard?.show()
        } else {
            keyboard?.hide()
        }
    }
}

@Composable
private fun TopBarView(
    isEdit: Boolean,
    backClick: (() -> Unit)? = null,
    postClick: (() -> Unit)? = null
) {
    CenterTopAppBar(
        leading = {
            IconButton(
                modifier = Modifier.size(80.dp, 40.dp),
                onClick = {
                    backClick?.invoke()
                }) {
                Icon(
                    ImageVector.vectorResource(id = R.drawable.white_close),
                    null,
                    tint = Color.White
                )
            }
        },
        title = {
            Text(
                if (isEdit) {
                    "編輯貼文"
                } else {
                    "發布貼文"
                }, fontSize = 17.sp, color = Color.White
            )
        },
        backgroundColor = LocalColor.current.env_100,
        contentColor = Color.White,
        trailing = {
            Button(
                modifier = Modifier.padding(end = 10.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = LocalColor.current.primary),
                onClick = {
                    postClick?.invoke()
                }) {
                Text(
                    text = "發布",
                    color = LocalColor.current.text.other,
                    fontSize = 16.sp
                )
            }
        }
    )
}


@Preview
@Composable
fun EditPostScreenPreview() {
    FanciTheme {
        EditPostScreenView(
            onShowImagePicker = {},
            onAttachmentFilePicker = {},
            onPostClick = {},
            onBack = {},
            showLoading = false,
            attachment = emptyList(),
            onDeleteAttach = {},
            onPreviewAttachmentClick = {},
            onResend = {}
        )
    }
}