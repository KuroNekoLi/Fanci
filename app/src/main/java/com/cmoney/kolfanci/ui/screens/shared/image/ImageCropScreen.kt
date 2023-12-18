package com.cmoney.kolfanci.ui.screens.shared.image

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.shared.toolbar.EditToolbarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.stfalcon.imageviewer.StfalconImageViewer

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCropScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var positionInRootTopBar by remember { mutableStateOf(Offset.Zero) }

    var rootPositionInRoot by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }

    var positionInRoot by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }

    var screenshot by remember { mutableStateOf<Bitmap?>(null) }

    var saveClick by remember {
        mutableStateOf(false)
    }

    if (saveClick) {
        screenshot = CompositionScreenshot(LocalContext.current, rootPositionInRoot)
            .take(positionInRoot)
        saveClick = false
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                rootPositionInRoot = coordinates.boundsInRoot()
            },
        topBar = {
            EditToolbarScreen(
                title = stringResource(id = R.string.group_avatar),
                saveClick = {
                    saveClick = true
                },
                backClick = {}
            )
        },
        backgroundColor = LocalColor.current.env_80
    ) { innerPadding ->

        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset(0f, 0f)) }
        val imageBitmap = ImageBitmap.imageResource(id = R.drawable.follow_empty)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                    onDoubleClick = {
                        scale = 1f
                        offset = Offset(0f, 0f)
                    }
                )
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale *= zoom

                        //Limit scale range
                        scale = scale.coerceIn(0.5f, 10f)

                        offset += pan
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = imageBitmap,
                contentDescription = null,
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            )

            Column {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(colorResource(id = R.color.color_99000000))
                )

                val configuration = LocalConfiguration.current

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .border(width = 1.dp, color = Color.White)
                        .onGloballyPositioned { coordinates ->
                            positionInRootTopBar = coordinates.positionInRoot()

                            positionInRoot = coordinates.boundsInRoot()
                        }
                        .drawWithContent {
                            drawIntoCanvas { canvas ->
                                canvas.clipRect(
                                    0f,
                                    0f,
                                    configuration.screenWidthDp.toFloat(),
                                    120.dp.value
                                )
                            }

                            drawContent()
                        }
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(colorResource(id = R.color.color_99000000))
                )

                //TODO: 將 bitmap 存起來, 之後上傳給 server 用
                screenshot?.let { bitmap ->

                    StfalconImageViewer
                        .Builder(
                            context, listOf(bitmap)
                        ) { imageView, image ->
                            Glide
                                .with(context)
                                .load(image)
                                .into(imageView)
                        }
                        .show()

                    screenshot = null
                }
            }
        }
    }
}

@Composable
fun CompositionScreenshot(
    context: android.content.Context,
    rootPositionInRoot: androidx.compose.ui.geometry.Rect
): CompositionScreenshot {
    val density = LocalDensity.current
    val view = LocalView.current

    return remember {
        CompositionScreenshot(
            context,
            density,
            view,
            rootPositionInRoot
        )
    }
}

class CompositionScreenshot(
    context: android.content.Context,
    density: Density,
    view: android.view.View,
    boundsInRootWindow: androidx.compose.ui.geometry.Rect
) {
    private val bitmap = Bitmap.createBitmap(
        (boundsInRootWindow.width * density.density).toInt(),
        (boundsInRootWindow.height * density.density).toInt(),
        Bitmap.Config.ARGB_8888
    )

    private val canvas = android.graphics.Canvas(bitmap)

    init {
        val screenshotView = android.view.View(context)
        screenshotView.draw(canvas)
        view.draw(canvas)
    }

    fun take(rect: androidx.compose.ui.geometry.Rect): Bitmap {
        // Crop the screenshot to the specified region
        return Bitmap.createBitmap(
            bitmap,
            rect.left.toInt() + 5.dp.value.toInt(),
            rect.top.toInt() + 5.dp.value.toInt(),
            rect.width.toInt() - 10.dp.value.toInt(),
            rect.height.toInt() - 10.dp.value.toInt()
        )
    }
}

@Preview
@Composable
fun ImageCropScreenPreview() {
    FanciTheme {
        ImageCropScreen()
    }
}