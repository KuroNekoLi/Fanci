package com.cmoney.kolfanci.ui.screens.chat.dialog

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.ReportReason
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.screens.group.search.apply.ApplyForGroupScreenPreview
import com.cmoney.kolfanci.ui.screens.shared.ChatUsrAvatarScreen
import com.cmoney.kolfanci.ui.theme.*

/**
 * 檢舉用戶 彈窗
 */
@Composable
fun ReportUserDialogScreen(
    user: GroupMember,
    onConfirm: (ReportReason) -> Unit,
    onDismiss: () -> Unit
) {
    val openDialog = remember { mutableStateOf(true) }

    val showReason = remember {
        mutableStateOf(false)
    }

    val dialogHeight = remember {
        mutableStateOf(IntrinsicSize.Min)
    }

    val reportReasonMap = remember {
        hashMapOf(
            "濫發廣告訊息" to ReportReason.spamAds,
            "傳送色情訊息" to ReportReason.adultContent,
            "騷擾行為" to ReportReason.harass,
            "內容與主題無關" to ReportReason.notRelated,
            "其他" to ReportReason.other,
            "取消檢舉" to null
        )
    }

    val reportLogMap = remember {
        hashMapOf(
            "濫發廣告訊息" to From.Spam,
            "傳送色情訊息" to From.SexualContent,
            "騷擾行為" to From.Harassment,
            "內容與主題無關" to From.UnrelatedContent,
            "其他" to From.Other,
            "取消檢舉" to null
        )
    }

    val reportReason =
        remember {
            listOf("濫發廣告訊息", "傳送色情訊息", "騷擾行為", "內容與主題無關", "其他", "取消檢舉")
        }

    if (openDialog.value) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = {
                openDialog.value = false
                onDismiss.invoke()
            }) {
            Box(
                modifier = Modifier
                    .padding(23.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(LocalColor.current.env_80)
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.report),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(9.dp))
                        Text(
                            text = "向管理員檢舉此用戶",
                            fontSize = 19.sp,
                            color = LocalColor.current.text.default_100
                        )
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(
                        text = "送出檢舉要求給管理員，會由社團管理員決定是否該對此用戶進行限制。",
                        fontSize = 17.sp,
                        color = LocalColor.current.text.default_100
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(LocalColor.current.background)
                            .padding(start = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        ChatUsrAvatarScreen(user = user)
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    //檢舉原因
                    if (showReason.value) {
                        reportReason.forEachIndexed { index, reason ->
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                border = BorderStroke(1.dp, LocalColor.current.text.default_100),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = LocalColor.current.env_80
                                ),
                                onClick = {
                                    if (reportReasonMap[reason] == null) {
                                        AppUserLogger.getInstance()
                                            .log(Clicked.ReportReasonCancelReport)

                                        onDismiss.invoke()
                                    } else {
                                        AppUserLogger.getInstance()
                                            .log(Clicked.ReportReasonReason, reportLogMap[reason])

                                        onConfirm.invoke(reportReasonMap[reason]!!)
                                    }
                                }) {
                                Text(
                                    text = reason,
                                    fontSize = 16.sp,
                                    color = LocalColor.current.text.default_100
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    } else {

                        BorderButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            text = "確定檢舉",
                            textColor = LocalColor.current.specialColor.red,
                            borderColor = LocalColor.current.text.default_50
                        ) {
                            AppUserLogger.getInstance().log(Clicked.ReportConfirmReport)
                            dialogHeight.value = IntrinsicSize.Max
                            showReason.value = true
                            Unit
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        BorderButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            text = "取消",
                            textColor = LocalColor.current.text.default_100,
                            borderColor = LocalColor.current.text.default_50
                        ) {
                            AppUserLogger.getInstance().log(Clicked.ReportCancel)
                            openDialog.value = false
                            onDismiss.invoke()
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportUserDialogScreenPreview() {
    FanciTheme {
        ReportUserDialogScreen(
            GroupMember(
                thumbNail = "https://pickaface.net/gallery/avatar/unr_sample_161118_2054_ynlrg.png",
                name = "Hello"
            ),
            {

            }
        ) {

        }
    }
}