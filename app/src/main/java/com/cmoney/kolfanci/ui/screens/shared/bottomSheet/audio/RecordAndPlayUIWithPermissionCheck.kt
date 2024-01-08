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

/**
 * 確認錄音權限
 * 向使用者要求錄音權限，若成功，則顯示錄音的 BottomSheet
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