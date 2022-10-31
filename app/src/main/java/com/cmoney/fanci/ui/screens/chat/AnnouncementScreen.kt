package com.cmoney.fanci.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.theme.Black_14171C
import com.cmoney.fanci.ui.theme.Blue_4F70E5
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.White_BBBCBF

const val AnnounceBundleKey = "AnnounceBundleKey"

/**
 * 設定 公告 訊息
 */
@Composable
fun AnnouncementScreen(
    navController: NavHostController,
    message: ChatMessageModel,
    onConfirm: (ChatMessageModel) -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                navController,
                title = "設定公告訊息",
                moreEnable = false
            ) {
                navController.popBackStack()
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Bottom
        ) {
            MessageContentScreen(
                modifier = Modifier
                    .weight(1f),
                messageModel = message,
                onMsgLongClick = {}
            ) {

            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Black_14171C)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "這則訊息公告後，將會覆蓋上一則公告", color = White_BBBCBF, fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(17.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Blue_4F70E5),
                        onClick = {
                            onConfirm.invoke(message)
                        }) {
                        Text(text = "將此訊息設為公告", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnnouncementScreenPreview() {
    FanciTheme {
        AnnouncementScreen(rememberNavController(), ChatRoomUseCase.imageType) {
            
        }
    }
}