package com.cmoney.kolfanci.ui.screens.shared.member

import FlowRow
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.cmoney.kolfanci.extension.isVip
import com.cmoney.kolfanci.ui.common.CircleImage
import com.cmoney.kolfanci.ui.screens.group.setting.member.all.RoleItem
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun HorizontalMemberItemScreen(
    modifier: Modifier = Modifier,
    groupMember: GroupMember,
    isShowRoleInfo: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleImage(
            modifier = Modifier
                .size(34.dp),
            imageUrl = groupMember.thumbNail.orEmpty()
        )

        Spacer(modifier = Modifier.width(15.dp))

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = groupMember.name.orEmpty(),
                    fontSize = 16.sp,
                    color = LocalColor.current.text.default_100
                )

                //是否為vip
                if (groupMember.isVip()) {
                    Spacer(modifier = Modifier.width(6.dp))

                    Image(
                        modifier = Modifier.size(11.dp),
                        painter = painterResource(id = R.drawable.vip_diamond),
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                //代號
                Text(
                    text = "#%d".format(groupMember.serialNumber),
                    fontSize = 12.sp,
                    color = LocalColor.current.text.default_50
                )
            }

            if (isShowRoleInfo && groupMember.roleInfos?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.width(7.dp))

                //使用者擁有權限
                FlowRow(
                    horizontalGap = 5.dp,
                    verticalGap = 5.dp,
                ) {
                    groupMember.roleInfos?.let {
                        repeat(it.size) { index ->
                            RoleItem(
                                it[index]
                            )
                        }
                    }
                }
            }

        }
    }
}

@Preview
@Composable
fun HorizontalMemberItemScreenPreview() {
    FanciTheme {
        HorizontalMemberItemScreen(
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
            isShowRoleInfo = true
        )
    }
}