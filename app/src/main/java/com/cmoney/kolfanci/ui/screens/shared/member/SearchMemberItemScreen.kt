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
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.isVip
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.common.CircleImage
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 *  搜尋結果 user item
 *
 * @param groupMember 會員 model
 */
@Composable
fun SearchMemberItemScreen(
    modifier: Modifier = Modifier,
    groupMember: GroupMember,
    subTitle: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 24.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchMemberInfoItemScreen(
            modifier = Modifier.weight(1f),
            subTitle = subTitle,
            groupMember = groupMember
        )

        Text(text = "查看", fontSize = 14.sp, color = LocalColor.current.primary)
    }
}

@Composable
fun SearchMemberInfoItemScreen(
    modifier: Modifier = Modifier,
    subTitle: String,
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
                if (groupMember.isVip()) {
                    Spacer(modifier = Modifier.width(6.dp))

                    Image(
                        modifier = Modifier.size(11.dp),
                        painter = painterResource(id = R.drawable.vip_diamond),
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = subTitle,
                fontSize = 12.sp,
                color = LocalColor.current.text.default_50
            )
        }
    }
}

@Preview
@Composable
fun SearchMemberItemScreenPreview() {
    FanciTheme {
        SearchMemberItemScreen(
            groupMember = MockData.mockGroupMember,
            subTitle = "聊天・2023.01.13"
        )
    }
}