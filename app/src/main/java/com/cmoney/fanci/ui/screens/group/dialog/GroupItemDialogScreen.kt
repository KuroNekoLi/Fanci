package com.cmoney.fanci.ui.screens.group.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.theme.Blue_4F70E5
import com.cmoney.fanci.ui.theme.White_262C34

@Composable
fun GroupItemDialogScreen(modifier: Modifier = Modifier, onDismiss: () -> Unit) {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        Dialog(onDismissRequest = {
            openDialog.value = false
            onDismiss.invoke()
        }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .clip(RoundedCornerShape(25.dp))
                    .background(White_262C34)
            ) {
                Column {
                    AsyncImage(
                        model = "https://picsum.photos/${(100..400).random()}/${(100..400).random()}",
                        modifier = Modifier
                            .height(170.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        placeholder = painterResource(id = R.drawable.resource_default)
                    )

                    Text(
                        modifier = Modifier.padding(top = 15.dp, start = 110.dp),
                        text = "韓勾ㄟ\uD83C\uDDF0\uD83C\uDDF7金針菇討論區",
                        fontSize = 16.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(35.dp))

                    Text(
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 15.dp),
                        text = "這是金針菇的小家～大家都在這邊嘻嘻哈哈討論金針菇！我們都愛金針菇嘿嘿 這是金針菇的小家～大家都在這邊嘻嘻哈哈討論金針菇！我們都愛金針菇嘿嘿這是金針菇的小家～大家都在這邊嘻嘻哈哈討論金針菇！我們都愛金針菇嘿嘿",
                        fontSize = 17.sp, color = Color.White
                    )

                    Button(
                        modifier = modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent
                        ),
                        elevation = ButtonDefaults.elevation(0.dp),
                        onClick = { /*TODO*/ }) {
                        Text(text = "加入社團", fontSize = 16.sp, color = Blue_4F70E5)
                    }

                }

                AsyncImage(
                    model = "https://picsum.photos/${(100..400).random()}/${(100..400).random()}",
                    modifier = Modifier
                        .padding(top = 130.dp, start = 20.dp)
                        .size(75.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(25.dp)),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    placeholder = painterResource(id = R.drawable.resource_default)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JoinGroupDialogScreenPreview() {
    GroupItemDialogScreen {

    }
}