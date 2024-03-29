package com.cmoney.kolfanci.ui.screens.post.edit

import android.net.Uri
import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.Glide
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.screens.chat.attachment.ChatRoomAttachImageScreen
import com.cmoney.kolfanci.ui.screens.post.edit.viewmodel.EditPostViewModel
import com.cmoney.kolfanci.ui.screens.post.edit.viewmodel.UiState
import com.cmoney.kolfanci.ui.screens.shared.toolbar.CenterTopAppBar
import com.cmoney.kolfanci.ui.screens.shared.ChatUsrAvatarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.PhotoPickDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.SaveConfirmDialogScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.facebook.bolts.Task.Companion.delay
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * 編輯完成後的回傳資料
 */
@Parcelize
data class BaseEditMessageScreenResult(
    val editMessage: BulletinboardMessage,  //編輯後的訊息
    val isComment: Boolean,      //是否為留言
    val commentId: String = ""   //回覆留言的留言id
) : Parcelable

/**
 * 編輯 留言 or 回覆
 *
 * @param channelId 頻道id
 * @param commentId 回覆留言的留言id
 * @param editMessage 欲編輯的message model
 * @param isComment 留言 or 回覆
 */
@Destination
@Composable
fun BaseEditMessageScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    channelId: String,
    commentId: String = "",
    editMessage: BulletinboardMessage,
    viewModel: EditPostViewModel = koinViewModel(
        parameters = {
            parametersOf(channelId)
        }
    ),
    isComment: Boolean, //區分留言 or 回覆
    resultNavigator: ResultBackNavigator<BaseEditMessageScreenResult>
) {
    var showImagePick by remember { mutableStateOf(false) }

    //TODO, 改為統一上傳格式
    val attachImages by viewModel.attachImages.collectAsState()

    val uiState by viewModel.uiState.collectAsState()

    val onSuccess by viewModel.postSuccess.collectAsState()

    var showSaveTip by remember {
        mutableStateOf(false)
    }

    //編輯貼文, 設定初始化資料
    LaunchedEffect(Unit) {
        viewModel.editPost(editMessage)
    }

    BaseEditMessageScreenView(
        modifier = modifier,
        editPost = editMessage,
        onShowImagePicker = {
            showImagePick = true
        },
        attachImages = attachImages,
        onDeleteImage = {
            viewModel.onDeleteImageClick(it)
        },
        onPostClick = { text ->
            viewModel.onUpdatePostClick(editMessage, text, emptyList())
        },
        onBack = {
            showSaveTip = true
        },
        showLoading = (uiState == UiState.ShowLoading),
        isComment = isComment
    )

    //Show image picker
    if (showImagePick) {
        PhotoPickDialogScreen(
            onDismiss = {
                showImagePick = false
            },
            onAttach = {
                showImagePick = false
                viewModel.addAttachImage(it)
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
            BaseEditMessageScreenResult(
                editMessage = this,
                isComment = isComment,
                commentId = commentId
            )
        )
    }

    SaveConfirmDialogScreen(
        isShow = showSaveTip,
        title = "是否放棄編輯的內容？",
        content = "尚未發布就離開，文字將會消失",
        onContinue = {
            showSaveTip = false
        },
        onGiveUp = {
            showSaveTip = false
            navController.popBackStack()
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun BaseEditMessageScreenView(
    modifier: Modifier = Modifier,
    editPost: BulletinboardMessage,
    onShowImagePicker: () -> Unit,
    attachImages: List<Uri>,
    onDeleteImage: (Uri) -> Unit,
    onPostClick: (String) -> Unit,
    onBack: () -> Unit,
    showLoading: Boolean,
    isComment: Boolean
) {
    val defaultContent = editPost.content?.text.orEmpty()
    var textState by remember { mutableStateOf(defaultContent) }
    val focusRequester = remember { FocusRequester() }
    val showKeyboard = remember { mutableStateOf(true) }
    val keyboard = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopBarView(
                isComment = isComment,
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
                    user = editPost.author ?: GroupMember()
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

                //Attach Image
                if (attachImages.isNotEmpty()) {
                    ChatRoomAttachImageScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LocalColor.current.env_100),
                        imageAttach = attachImages.map {
                            AttachmentInfoItem(
                                uri = it
                            )
                        },
                        onDelete = {
                            onDeleteImage.invoke(it.uri)
                        },
                        onAdd = {
                            onShowImagePicker.invoke()
                        },
                        onResend = {
                        },
                        onClick = { uri ->
                            StfalconImageViewer
                                .Builder(
                                    context, listOf(uri)
                                ) { imageView, image ->
                                    Glide
                                        .with(context)
                                        .load(image)
                                        .into(imageView)
                                }
                                .show()
                        }
                    )
                }

                //Bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .background(LocalColor.current.env_100)
                        .padding(top = 10.dp, bottom = 10.dp, start = 18.dp, end = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        modifier = Modifier.size(25.dp),
                        onClick = {
                            onShowImagePicker.invoke()
                        }) {
                        Icon(
                            ImageVector.vectorResource(id = R.drawable.gallery),
                            null,
                            tint = LocalColor.current.text.default_100
                        )
                    }
                }
            }

            if (showLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(45.dp),
                    color = LocalColor.current.primary
                )
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
    isComment: Boolean,
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
                if (isComment) {
                    "編輯留言"
                } else {
                    "編輯回覆"
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


@Preview(showBackground = true)
@Composable
fun BaseEditScreenPreview() {
    FanciTheme {
        BaseEditMessageScreenView(
            editPost = BulletinboardMessage(),
            onShowImagePicker = {},
            attachImages = emptyList(),
            onDeleteImage = {},
            onPostClick = {},
            onBack = {},
            showLoading = false,
            isComment = true
        )
    }
}