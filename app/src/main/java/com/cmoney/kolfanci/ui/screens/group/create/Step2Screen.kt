package com.cmoney.kolfanci.ui.screens.group.create

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.screens.group.setting.group.openness.QuestionItem
import com.cmoney.kolfanci.ui.theme.Color_29787880
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 設定權限
 */
@Composable
fun Step2Screen(
    isNeedApproval: Boolean,
    onSwitchApprove: (Boolean) -> Unit,
    onAddQuestion: () -> Unit,
    question: List<String>,
    onEditClick: (String) -> Unit,
    onNext: () -> Unit,
    onPre: () -> Unit
) {
    Spacer(modifier = Modifier.height(20.dp))
    Column {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LocalColor.current.background)
                    .padding(top = 10.dp, bottom = 10.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = R.drawable.lock),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.component.other)
                )
                Spacer(modifier = Modifier.width(19.dp))

                Text(
                    modifier = Modifier.weight(1f),
                    text = "社團公開度", fontSize = 17.sp, color = LocalColor.current.text.default_100
                )

                val publicText = if (isNeedApproval) {
                    "不公開"
                } else {
                    "公開"
                }

                Text(
                    text = publicText,
                    fontSize = 17.sp,
                    color = LocalColor.current.specialColor.red
                )

                Spacer(modifier = Modifier.width(17.dp))

                Switch(
                    modifier = Modifier.size(51.dp, 31.dp),
                    checked = isNeedApproval,
                    onCheckedChange = {
                        onSwitchApprove.invoke(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = LocalColor.current.primary,
                        checkedTrackAlpha = 1f,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color_29787880,
                        uncheckedTrackAlpha = 1f
                    ),
                )
            }

            Text(
                modifier = Modifier.padding(top = 20.dp, start = 16.dp),
                text = "*社團無論公開與否，都能被搜尋到*",
                fontSize = 14.sp,
                color = LocalColor.current.text.default_50
            )

            if (isNeedApproval) {
                Text(
                    modifier = Modifier.padding(top = 25.dp, start = 16.dp),
                    text = "審核問題",
                    fontSize = 14.sp,
                    color = LocalColor.current.text.default_50
                )

                Spacer(modifier = Modifier.height(15.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LocalColor.current.background)
                ) {
                    question.forEach {
                        QuestionItem(question = it, onClick = {
                            onEditClick.invoke(it)
                        })
                        Spacer(modifier = Modifier.height(1.dp))
                    }

                    BorderButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .background(LocalColor.current.background)
                            .padding(top = 8.dp, bottom = 8.dp, start = 24.dp, end = 24.dp),
                        text = "新增審核題目",
                        borderColor = LocalColor.current.text.default_50,
                        textColor = LocalColor.current.text.default_100
                    ) {
                        onAddQuestion.invoke()
                    }
                }
            }


            Spacer(modifier = Modifier.weight(1f))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp)
                .background(LocalColor.current.env_100),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(25.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                BorderButton(
                    modifier = Modifier.weight(1f),
                    text = "上一步",
                    borderColor = LocalColor.current.text.default_50,
                    textColor = LocalColor.current.text.default_100
                ) {
                    onPre.invoke()
                }

                Spacer(modifier = Modifier.width(27.dp))

                BlueButton(
                    modifier = Modifier.weight(1f),
                    text = "下一步"
                ) {
                    onNext.invoke()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Step2ScreenPreview() {
    FanciTheme {
        Step2Screen(
            question = emptyList(),
            isNeedApproval = false,
            onSwitchApprove = {},
            onAddQuestion = {},
            onEditClick = {},
            onNext = {},
            onPre = {}
        )
    }
}