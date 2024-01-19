package com.cmoney.kolfanci.ui.screens.shared.bottomSheet.audio

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.cmoney.kolfanci.ui.screens.media.audio.ProgressIndicator
import com.cmoney.kolfanci.ui.screens.media.audio.RecordingScreenEvent
import com.cmoney.kolfanci.ui.screens.media.audio.RecordingViewModel

/**
 * 確認錄音權限
 * 向使用者要求錄音權限，若成功，則顯示錄音的 BottomSheet
 * @param onDismissRequest 關閉的回調
 * 其餘參數參考 [RecordingScreen]
 */
@Composable
fun RecordAndPlayUIWithPermissionCheck(
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
    val context = LocalContext.current

    // State for RECORD_AUDIO permission
    var permissionToRecordAccepted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher for RECORD_AUDIO permission
    val requestRecordPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            permissionToRecordAccepted = isGranted
        }
    )


    LaunchedEffect(Unit) {
        if (!permissionToRecordAccepted) {
            requestRecordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    if (permissionToRecordAccepted) {
        AudioRecorderBottomSheet(
            isRecorderHintVisible = isRecorderHintVisible,
            progressIndicator = progressIndicator,
            time = time,
            isDeleteVisible = isDeleteVisible,
            isUploadVisible = isUploadVisible,
            progress = progress,
            onPlayingButtonClick = onPlayingButtonClick,
            onDelete = onDelete,
            onUpload = onUpload,
            onDismissRequest = onDismissRequest
        )
    }
}

/**
 * 錄音的Screen
 * @param recordingViewModel 錄音的viewModel
 * @param onUpload 按下上傳的回調
 * @param onDismissRequest 關掉視窗的回調
 */
@Composable
fun RecordScreen(
    recordingViewModel: RecordingViewModel,
    onUpload: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val recordingScreenState by recordingViewModel.recordingScreenState
    RecordAndPlayUIWithPermissionCheck(
        isRecorderHintVisible = recordingScreenState.isRecordHintVisible,
        progressIndicator = recordingScreenState.progressIndicator,
        time = recordingScreenState.currentTime,
        isDeleteVisible = recordingScreenState.isDeleteVisible,
        isUploadVisible = recordingScreenState.isUploadVisible,
        progress = recordingScreenState.progress,
        onPlayingButtonClick = { recordingViewModel.onEvent(RecordingScreenEvent.OnButtonClicked) },
        onDelete = { recordingViewModel.onEvent(RecordingScreenEvent.OnDelete) },
        onUpload = {
            recordingViewModel.onEvent(RecordingScreenEvent.OnUpload)
            onUpload()

        },
        onDismissRequest = {
            recordingViewModel.onEvent(RecordingScreenEvent.OnDismiss)
            onDismissRequest()
        }
    )
}