package com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.logo

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.shared.dialog.SaveConfirmDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.toolbar.EditToolbarScreen
import com.cmoney.kolfanci.utils.PhotoUtils
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.smarttoolfactory.cropper.ImageCropper
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import com.smarttoolfactory.cropper.settings.CropType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Destination
@Composable
fun LogoCropperScreen(
    photoUri: Uri,
    logoResultNavigator: ResultBackNavigator<Uri>,
){
    val handleSize: Float = LocalDensity.current.run { 20.dp.toPx() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val cropProperties by remember {
        mutableStateOf(
            CropDefaults.properties(
                overlayRatio = 1f,
                cropType = CropType.Static,
                cropOutlineProperty = CropOutlineProperty(
                    OutlineType.Rect,
                    RectCropShape(0, "Rect")
                ),
                handleSize = handleSize,
                aspectRatio = AspectRatio(375/120f)
            )
        )
    }
    val cropStyle by remember { mutableStateOf(CropDefaults.style(drawGrid = false)) }
    var showSaveTip by remember {
        mutableStateOf(false)
    }
    var imageBitmapLarge = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.follow_empty
    )

    var imageBitmap by remember { mutableStateOf(imageBitmapLarge) }
    Glide.with(context)
        .asBitmap()
        .load(photoUri)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                Log.i("LinLi", "onResourceReady: $resource")
                imageBitmap = resource.asImageBitmap()
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // 可以在這裡處理清除資源的邏輯
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)

            }
        })
    val crop = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            EditToolbarScreen(
                title = stringResource(id = R.string.group_logo),
                saveClick = {
                    Log.i("LinLi", "saveClick: ")
                    crop.value = true
                },
                backClick = {
                    showSaveTip = true
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                ImageCropper(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    imageBitmap = imageBitmap,
                    contentDescription = "Image Cropper",
                    cropStyle = cropStyle,
                    cropProperties = cropProperties,
                    crop = crop.value,
                    onCropStart = {},
                    onCropSuccess = { cropResultBitmap ->
                            coroutineScope.launch{
                                Log.i("LinLi", "coroutineScope launched: ")
                                val resultUrl = withContext(Dispatchers.IO){
                                    Log.i("LinLi", "coroutineScope launched2: ")
                                    PhotoUtils.saveBitmapAndGetUri(context,cropResultBitmap)
                                }
                                if (resultUrl != null) {
                                    Log.i("LinLi", "resultUrl: ")
                                    logoResultNavigator.navigateBack(resultUrl)
                                }else{
                                    Log.i("LinLi", "resultUrl is null: ")
                                }
                            }

                    },
                )
                SaveConfirmDialogScreen(
                    isShow = showSaveTip,
                    onContinue = {
                        showSaveTip = false
                    },
                    onGiveUp = {
                        showSaveTip = false
                        logoResultNavigator.navigateBack()
                    }
                )
            }
        }
    }
}
//@Preview(showBackground = true)
//@Composable
//fun LogoCropperScreenPreview(){
//    LogoCropperScreen(logoResultNavigator =  emptyResultback)
//}