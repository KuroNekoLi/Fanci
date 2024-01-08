package com.cmoney.kolfanci.ui.screens.shared.bottomSheet.audio

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.media.audio.ProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecorderBottomSheet(
    isRecorderHintVisible: Boolean,
    progressIndicator: ProgressIndicator,
    time: String,
    isDeleteVisible: Boolean,
    isUploadVisible: Boolean,
    progress: Float,
    onPlayingButtonClick: () -> Unit,
    onDelete: () -> Unit,
    onUpload: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = colorResource(id = R.color.color_20262F)
    ) {
        TimerScreen(
            isRecorderHintVisible = isRecorderHintVisible,
            progressIndicator = progressIndicator,
            time = time,
            isDeleteVisible = isDeleteVisible,
            isUploadVisible = isUploadVisible,
            progress = progress,
            onPlayingButtonClick = onPlayingButtonClick,
            onDelete = onDelete,
            onUpload = onUpload,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 31.dp, bottom = 75.dp)
        )
    }
}