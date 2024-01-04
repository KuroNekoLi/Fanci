package com.cmoney.kolfanci.ui.screens.shared.bottomSheet.audio

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecorderBottomSheet(onDismissRequest: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
//        modifier = Modifier.height(400.dp),
        containerColor = colorResource(id = R.color.color_20262F)
    ) {
        // Sheet content
//        Button(onClick = {
//            scope.launch { sheetState.hide() }.invokeOnCompletion {
//                if (!sheetState.isVisible) {
//                    onDismissRequest()
//                }
//            }
//        }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
//            Text("Hide bottom sheet")
//        }
        TimerScreen(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 31.dp, bottom = 75.dp)
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AudioRecorderBottomSheetPreview(){
//    AudioRecorderBottomSheet()
//}