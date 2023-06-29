package com.cmoney.kolfanci.ui.screens.shared.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.camera.ChooseImagePickDialog
import com.cmoney.kolfanci.ui.theme.*

/**
 * 社團 個人資料 設定
 */
@Composable
fun UserInfoSettingScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    state: UserInfoSettingState = rememberUserInfoSettingState(navController = navController)
) {
    Scaffold(
        modifier = modifier.background(
            MaterialTheme.colors.surface
        ),
        topBar = {
            TopBarScreen(
                title = "社團個人資料",
                backClick = {
                    state.navController.popBackStack()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "我在此社團的頭像", fontSize = 14.sp, color = Color.White)
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.clickable {
                state.openCameraDialog()
            }, contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape),
                    model = state.onAttachImage.value,
                    placeholder = painterResource(id = R.drawable.placeholder),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )

                Image(
                    modifier = Modifier.size(26.dp),
                    contentScale = ContentScale.Crop,
                    painter = painterResource(id = R.drawable.camera_setting),
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row {
                Text(text = "我在此社團的暱稱", fontSize = 14.sp, color = Color.White)
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = state.getCurrentInputLength(),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.caption,
                    color = White_BBBCBF
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                value = state.inputText.value,
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    backgroundColor = Black_202327,
                    cursorColor = Color.White,
                    disabledLabelColor = Color.DarkGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                onValueChange = {
                    state.onValueChange(it)
                },
                shape = RoundedCornerShape(4.dp),
                maxLines = 5,
                textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                placeholder = { Text(text = "輸入你的暱稱", fontSize = 16.sp, color = White_494D54) }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Blue_4F70E5),
                onClick = {
                }) {
                Text(text = "儲存", color = Color.White, fontSize = 16.sp)
            }
        }

        if (state.openCameraDialog.value) {
            ChooseImagePickDialog(onDismiss = {
                state.closeCameraDialog()
            }) {
                state.attachImage(it)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserInfoSettingScreenPreview() {
    FanciTheme {
        UserInfoSettingScreen(navController = rememberNavController())
    }
}