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

@Composable
fun RecordAndPlayUIWithPermissionCheck(onDismissRequest: () -> Unit) {
    val context = LocalContext.current
    var permissionToRecordAccepted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            permissionToRecordAccepted = isGranted
        }
    )

    LaunchedEffect(Unit) {
        if (!permissionToRecordAccepted) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    if (permissionToRecordAccepted) {
        AudioRecorderBottomSheet(
            onDismissRequest = onDismissRequest
        )
    }
}