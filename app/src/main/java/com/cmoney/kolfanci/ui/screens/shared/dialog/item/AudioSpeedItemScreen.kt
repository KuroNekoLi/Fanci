package com.cmoney.kolfanci.ui.screens.shared.dialog.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.BanPeriodOption
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.screens.shared.vip.getVipDiamondInlineContent
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun AudioSpeedItemScreen(
    modifier: Modifier = Modifier,
    onClick: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val options = stringArrayResource(id = R.array.audio_speed_options)
    val context = LocalContext.current
    val innerClickFunc = rememberUpdatedState { option: String ->
        when (option) {
            context.getString(R.string.audio_speed_0_25) -> {
                onClick.invoke(0.25f)
            }
            context.getString(R.string.audio_speed_0_5) -> {
                onClick.invoke(0.5f)
            }
            context.getString(R.string.audio_speed_0_75) -> {
                onClick.invoke(0.75f)
            }
            context.getString(R.string.audio_speed_1) -> {
                onClick.invoke(1f)
            }
            context.getString(R.string.audio_speed_1_25) -> {
                onClick.invoke(1.25f)
            }
            context.getString(R.string.audio_speed_1_5) -> {
                onClick.invoke(1.5f)
            }
            context.getString(R.string.audio_speed_1_75) -> {
                onClick.invoke(1.75f)
            }
        }
    }
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(options) { option ->
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = option,
                borderColor = LocalColor.current.component.other,
                textColor = LocalColor.current.text.default_100
            ) {
                innerClickFunc.value.invoke(option)
            }
        }
        item {
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = "取消",
                borderColor = LocalColor.current.component.other,
                textColor = LocalColor.current.text.default_100
            ) {
                onDismiss.invoke()
            }
        }
    }
}

@Preview
@Composable
fun AudioSpeedItemScreenPreview() {
    FanciTheme {
        AudioSpeedItemScreen(
            onClick = {},
            onDismiss = {}
        )
    }
}