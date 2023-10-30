package com.cmoney.kolfanci.ui.screens.media

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.cmoney.kolfanci.extension.getFileName
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.tools_android.extension.toFile
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@Destination
@Composable
fun TextPreviewScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    uri: Uri
) {
    val fileTitle = uri.getFileName(LocalContext.current)
    val file = uri.toFile(LocalContext.current)
    val context = LocalContext.current

    var showText by remember {
        mutableStateOf(false)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showText = true
        } else {
            // Permission Denied: Do something
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = fileTitle.orEmpty(),
                backClick = {
                    navController.popBackStack()
                }
            )
        },
        backgroundColor = Color.Black
    ) {
        it

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                showText = true
            }

            else -> {
                launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (showText) {
            TextShowScreen(
                text = file?.readText().orEmpty()
            )
        }
    }
}

@Composable
private fun TextShowScreen(modifier: Modifier = Modifier, text: String) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = text,
            fontSize = 16.sp,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TextPreviewScreenPreview() {
    FanciTheme {
        TextPreviewScreen(
            navController = EmptyDestinationsNavigator,
            uri = Uri.EMPTY
        )
    }

}