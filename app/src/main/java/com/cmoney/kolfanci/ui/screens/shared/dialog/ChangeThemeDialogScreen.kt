package com.cmoney.kolfanci.ui.screens.shared.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.theme.model.GroupTheme
import com.cmoney.kolfanci.ui.theme.DefaultThemeColor
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun ChangeThemeDialogScreen(
    modifier: Modifier = Modifier,
    groupTheme: GroupTheme,
    onConfirm: (GroupTheme) -> Unit,
    onDismiss: () -> Unit
) {
    DialogScreen(
        modifier = modifier,
        onDismiss = onDismiss,
        titleIconRes = com.cmoney.kolfanci.R.drawable.painter,
        title = "確定套用「%s」主題？".format(groupTheme.name),
        subTitle = "主題套用後，所有成員的社團主題將會同步更改。",
        content = {
            Column {
                Spacer(modifier = Modifier.height(10.dp))

                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = "確定",
                    borderColor = LocalColor.current.component.other,
                    textColor = LocalColor.current.text.default_100
                ) {
                    onConfirm.invoke(groupTheme)
                }

                Spacer(modifier = Modifier.height(20.dp))

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
        })
}

@Preview(showBackground = true)
@Composable
fun ChangeThemeDialogScreenPreview() {
    FanciTheme {
        ChangeThemeDialogScreen(
            groupTheme = GroupTheme(
                id = "",
                isSelected = true,
                theme = DefaultThemeColor,
                name = "",
                preview = emptyList()
            ),
            onConfirm = {},
            onDismiss = {}
        )
    }
}