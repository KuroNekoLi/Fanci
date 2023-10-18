package com.cmoney.kolfanci.ui.screens.shared.bottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MediaPickerBottomSheet(
    modifier: Modifier = Modifier,
    state: ModalBottomSheetState,
    onImageOrVideoClick: () -> Unit,
    onFileClick: () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = state,
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        sheetContent = {
            MediaPickerBottomSheetView(
                modifier = modifier,
                onImageOrVideoClick = onImageOrVideoClick,
                onFileClick = onFileClick
            )
        }
    ) {}
}

@Composable
fun MediaPickerBottomSheetView(
    modifier: Modifier = Modifier,
    onImageOrVideoClick: () -> Unit,
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
                    onImageOrVideoClick.invoke()
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
            color = Color.Gray,
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

@Preview
@Composable
fun MediaPickerBottomSheetPreview() {
    FanciTheme {
        MediaPickerBottomSheetView(
            onImageOrVideoClick = {},
            onFileClick = {}
        )
    }
}
