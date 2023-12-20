package com.cmoney.kolfanci.ui.screens.media

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.cmoney.kolfanci.extension.getFileName
import com.cmoney.kolfanci.extension.isURL
import com.cmoney.kolfanci.ui.screens.shared.toolbar.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.xlogin.XLoginHelper
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import java.net.URL
import java.util.concurrent.Executors

@Destination
@Composable
fun PdfPreviewScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    uri: Uri,
    title: String = ""
) {
    val fileTitle = if (uri.isURL()) {
        title
    } else {
        uri.getFileName(LocalContext.current)
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
    ) { innerPadding ->
        AndroidView(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            factory = { context ->
                val pdfView = PDFView(context, null)

                if (uri.isURL()) {
                    val executor = Executors.newSingleThreadExecutor()
                    executor.execute {
                        val url = URL(uri.toString())
                        val conn = url.openConnection()
                        conn.setRequestProperty(
                            "Authorization",
                            "Bearer " + XLoginHelper.accessToken
                        )
                        val input = conn.getInputStream()

                        pdfView.fromStream(input)
                            .enableAnnotationRendering(true)
//                        .scrollHandle(DefaultScrollHandle(context))
                            .load()
                    }
                } else {
                    pdfView.apply {
                        fromUri(uri)
                            .defaultPage(0)
                            .enableAnnotationRendering(true)
                            .scrollHandle(DefaultScrollHandle(context))
                            .spacing(10) // in dp
                            .load()
                    }
                }

                pdfView
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PdfPreviewScreenPreview() {
    FanciTheme {
        PdfPreviewScreen(
            navController = EmptyDestinationsNavigator,
            uri = Uri.EMPTY
        )
    }

}