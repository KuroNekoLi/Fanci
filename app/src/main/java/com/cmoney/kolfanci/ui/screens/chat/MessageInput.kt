package com.cmoney.kolfanci.ui.screens.chat

import android.net.Uri
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.MessageViewModel
import com.cmoney.kolfanci.ui.screens.shared.camera.ChooseImagePickDialog
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import org.koin.androidx.compose.koinViewModel

/**
 * 聊天室 輸入匡
 */
@Composable
fun MessageInput(
    onMessageSend: (text: String) -> Unit,
    onAttach: (Uri) -> Unit,
    showOnlyBasicPermissionTip: () -> Unit,
    viewModel: MessageViewModel = koinViewModel()
) {
    val openDialog = remember { mutableStateOf(false) }
    var textState by remember { mutableStateOf("") }

    var isShowSend by remember {
        mutableStateOf(false)
    }

    isShowSend = viewModel.uiState.imageAttach.isNotEmpty() || textState.isNotEmpty()

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
                .background(LocalColor.current.background)
                .clickable {
                    if (Constant.canPostMessage()) {
                        openDialog.value = true
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(19.dp),
                painter = painterResource(id = R.drawable.plus),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = Color.White)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(10.dp)
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                value = textState,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = LocalColor.current.inputText.input_100,
                    backgroundColor = LocalColor.current.inputFrame,
                    cursorColor = LocalColor.current.primary,
                    disabledLabelColor = LocalColor.current.text.default_30,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                onValueChange = {
                    textState = it
                    isShowSend = it.isNotEmpty()
                },
                shape = RoundedCornerShape(40.dp),
                maxLines = 5,
                textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                placeholder = {
                    Text(
                        text = if (Constant.canPostMessage()) {
                            "輸入你想說的話..."
                        } else {
                            "基本權限，無法與頻道成員互動"
                        },
                        fontSize = 16.sp,
                        color = LocalColor.current.inputText.input_30
                    )
                },
                enabled = Constant.canPostMessage()
            )

            if (!Constant.canPostMessage()) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        showOnlyBasicPermissionTip.invoke()
                    }
                )
            }
        }

        if (isShowSend && Constant.canPostMessage()) {
            IconButton(
                onClick = {
                    onMessageSend.invoke(textState)
                    textState = ""
                },
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp, end = 16.dp)
                    .size(41.dp)
                    .clip(CircleShape)
                    .background(LocalColor.current.primary),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.send),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }

    if (openDialog.value) {
        ChooseImagePickDialog(onDismiss = {
            openDialog.value = false
        }) {
            onAttach.invoke(it)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageInputPreview() {
    FanciTheme {
        MessageInput(
            {},
            {},
            {}
        )
    }
}