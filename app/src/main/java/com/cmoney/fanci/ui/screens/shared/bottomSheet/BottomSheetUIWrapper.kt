package com.cmoney.fanci.ui.screens.shared.bottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetUIWrapper(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .clip(RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp))
            .background(Color.White)
    ) {
        Box(Modifier.padding(top = 25.dp)) {
            content()
        }

        Divider(
            color = Color.Gray,
            thickness = 5.dp,
            modifier = Modifier
                .padding(top = 15.dp)
                .align(Alignment.TopCenter)
                .width(80.dp)
                .clip(RoundedCornerShape(50.dp))
        )

//        CloseButton(
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .padding(10.dp)
//        ) {
//            coroutineScope.launch {
//                modalBottomSheetState.hide()
//            }
//        }
    }
}
