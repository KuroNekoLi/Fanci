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
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@Destination
@Composable
fun PdfPreviewScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    uri: Uri
) {
    val fileTitle = uri.getFileName(LocalContext.current)

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

//                val executor = Executors.newSingleThreadExecutor()
//                executor.execute {
//                    val input = URL("https://www.africau.edu/images/default/sample.pdf").openStream()
//                    pdfView.fromStream(input)
//                        .enableAnnotationRendering(true)
////                        .scrollHandle(DefaultScrollHandle(context))
//                        .load()
//                }

                pdfView.apply {
                    fromUri(uri)
                        .defaultPage(0)
                        .enableAnnotationRendering(true)
                        .scrollHandle(DefaultScrollHandle(context))
                        .spacing(10) // in dp
                        .load()
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