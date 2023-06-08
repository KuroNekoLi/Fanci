package com.cmoney.kolfanci.ui.screens.shared.member

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.common.CircleImage
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 會員 item ui
 *
 * @param groupMember 會員 model
 * @param isShowRemove 是否要移除功能
 */
@Composable
fun MemberItemScreen(
    modifier: Modifier = Modifier,
    groupMember: GroupMember,
    isShowRemove: Boolean = true,
    onMemberClick: ((GroupMember) -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalColor.current.background)
            .clickable(
                enabled = (onMemberClick != null)
            ) {
                onMemberClick?.invoke(groupMember)
            }
            .padding(start = 30.dp, end = 24.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MemberInfoItemScreen(
            modifier = Modifier.weight(1f),
            groupMember
        )

        if (isShowRemove) {
            Text(text = "移除", fontSize = 14.sp, color = LocalColor.current.primary)
        }
    }
}

/**
 * 大頭貼, 名字, vip icon, 代號顯示
 */
@Composable
fun MemberInfoItemScreen(
    modifier: Modifier = Modifier,
    groupMember: GroupMember
) {
    Row(
        modifier = modifier
    ) {
        CircleImage(
            modifier = Modifier
                .size(34.dp),
            imageUrl = groupMember.thumbNail.orEmpty()
        )

        Spacer(modifier = Modifier.width(15.dp))

        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                //名字
                Text(
                    text = groupMember.name.orEmpty(),
                    fontSize = 16.sp,
                    color = LocalColor.current.text.default_100
                )

                //是否為vip
                if (groupMember.isVip) {
                    Spacer(modifier = Modifier.width(6.dp))

                    Image(
                        modifier = Modifier.size(11.dp),
                        painter = painterResource(id = R.drawable.vip_diamond),
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.width(5.dp))

            //代號
            Text(
                text = "#%d".format(groupMember.serialNumber),
                fontSize = 12.sp,
                color = LocalColor.current.text.default_50
            )
        }
    }

}

@Preview
@Composable
fun MemberItemScreenPreview() {
    FanciTheme {
        MemberItemScreen(
            groupMember = GroupMember(
                thumbNail = "",
                name = "王力宏",
                serialNumber = 123456,
                roleInfos = listOf(
                    FanciRole(
                        name = "Role",
                        color = ""
                    )
                )
            ),
            onMemberClick = {}
        )
    }
}