package com.cmoney.kolfanci.ui.screens.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.cmoney.fanciapi.fanci.model.ChannelTabType
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.MessageViewModel
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import org.koin.androidx.compose.koinViewModel

/**
 * 聊天室 輸入匡
 */
@Composable
fun MessageInput(
    tabType: ChannelTabType = ChannelTabType.chatRoom,
    defaultText: String = "",
    onMessageSend: (text: String) -> Unit,
    showOnlyBasicPermissionTip: () -> Unit,
    viewModel: MessageViewModel = koinViewModel(),
    onAttachClick: () -> Unit
) {
    var textState by remember { mutableStateOf(defaultText) }

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
                        onAttachClick.invoke()
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

        /**
         * 是否要顯示不能輸入的遮罩
         */
        fun isShowMask(): Boolean {
            return when (tabType) {
                ChannelTabType.chatRoom -> {
                    !Constant.canPostMessage()
                }
                ChannelTabType.bulletinboard -> {
                    !Constant.canPostMessage() && !Constant.isCanReply()
                }
            }
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
                shape = RoundedCornerShape(25.dp),
                maxLines = 5,
                textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                placeholder = {
                    val hintText = when (tabType) {
                        ChannelTabType.chatRoom -> {
                            if (Constant.canPostMessage()) {
                                "輸入你想說的話..."
                            } else {
                                if(Constant.isBuffSilence()) {
                                    Constant.getChannelSilenceDesc()
                                }
                                else {
                                    "基本權限，無法與頻道成員互動"
                                }
                            }
                        }
                        ChannelTabType.bulletinboard -> {
                            if (Constant.canPostMessage() || Constant.isCanReply()) {
                                "輸入你想說的話..."
                            } else {
                                if(Constant.isBuffSilence()) {
                                    Constant.getChannelSilenceDesc()
                                }
                                else {
                                    "基本權限，無法與頻道成員互動"
                                }
                            }
                        }
                    }

                    Text(
                        text = hintText,
                        fontSize = 16.sp,
                        color = LocalColor.current.inputText.input_30
                    )
                },
                enabled = !isShowMask()
            )

            if (isShowMask()) {
                //不能打字的遮罩
                Box(modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        showOnlyBasicPermissionTip.invoke()
                    }
                )
            }
        }

        if (isShowSend && !isShowMask()) {
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
}

@Preview(showBackground = true)
@Composable
fun MessageInputPreview() {
    FanciTheme {
        MessageInput(
            onAttachClick = {},
            onMessageSend = {},
            showOnlyBasicPermissionTip = {}
        )
    }
}