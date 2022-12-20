package com.cmoney.fanci.ui.screens.group.setting.role.add.dialog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.ui.screens.chat.dialog.onClose
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanci.ui.theme.White_262C34
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ColorPickerScreen(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    modifier: Modifier = Modifier,
    selectedColor: Color,
    onColorPick: (Color) -> Unit
) {

    val colorList = listOf(
        LocalColor.current.specialColor.red,
        LocalColor.current.specialColor.orange,
        LocalColor.current.specialColor.yellow,
        LocalColor.current.specialColor.green,
        LocalColor.current.specialColor.blueGreen,
        LocalColor.current.specialColor.blue,
        LocalColor.current.specialColor.purple,
        LocalColor.current.specialColor.pink
    )

    var selectPos = colorList.map {
        it.value
    }.indexOf(selectedColor.value)

    if (selectPos == -1) {
        selectPos = 0
    }

    val selectedIndex = remember {
        mutableStateOf(selectPos)
    }

    Scaffold(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(LocalColor.current.env_60)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 12.dp,
                    bottom = 12.dp
                )
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "選取顏色", fontSize = 17.sp, color = Color.White
                )

                Text(
                    modifier = Modifier.clickable {
                        onClose(coroutineScope, modalBottomSheetState)
                        onColorPick.invoke(colorList[selectedIndex.value])
                    },
                    text = "確定", fontSize = 17.sp, color = LocalColor.current.primary
                )
            }

            LazyVerticalGrid(
                modifier = Modifier
                    .background(LocalColor.current.background)
                    .padding(top = 30.dp, bottom = 30.dp),
                verticalArrangement = Arrangement.spacedBy(30.dp),
                columns = GridCells.Fixed(4)
            ) {
                itemsIndexed(colorList) { index, color ->
                    Box(
                        modifier = Modifier,
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable {
                                selectedIndex.value = index
                            })

                        val circleColor = if (selectedIndex.value == index) {
                            Color.White
                        } else {
                            Color.Transparent
                        }

                        Canvas(modifier = Modifier.size(57.dp)) {
                            drawCircle(
                                color = circleColor,
                                radius = 65f,
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    }
                }

            }

        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun ColorPickerScreenPreview() {
    FanciTheme {
        ColorPickerScreen(
            rememberCoroutineScope(),
            rememberModalBottomSheetState(
                ModalBottomSheetValue.Hidden,
                confirmStateChange = {
                    it != ModalBottomSheetValue.HalfExpanded
                }
            ),
            selectedColor = LocalColor.current.specialColor.orange
        ) {}
    }
}