package com.cmoney.fanci.ui.screens.group.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanci.R
import com.cmoney.fanci.model.GroupModel
import com.cmoney.fanci.ui.common.GroupText
import com.cmoney.fanci.ui.theme.*
import com.cmoney.fanci.ui.theme.LocalColor
import com.socks.library.KLog

@Composable
fun GroupItemScreen(
    modifier: Modifier = Modifier,
    groupModel: GroupModel,
    onGroupItemClick: (GroupModel) -> Unit
) {
    val TAG = "GroupItemScreen"
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(LocalColor.current.background)
            .clickable {
                KLog.i(TAG, "click.")
                onGroupItemClick.invoke(groupModel)
            }
            .padding(15.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 20.dp)
        ) {
            GroupText(text = groupModel.name)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "私密社團・成員 1345 ", fontSize = 12.sp, color = LocalColor.current.text.default_50)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = groupModel.description,
                fontSize = 14.sp,
                color = LocalColor.current.text.default_80,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        AsyncImage(
            model = groupModel.thumbnailImageUrl,
            modifier = Modifier
                .size(55.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(18.dp)),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.resource_default)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GroupItemScreenPreview() {
    FanciTheme {
        GroupItemScreen(
            groupModel = GroupModel(
                groupId = "",
                name = "Hello",
                description = "Description",
                coverImageUrl = "",
                thumbnailImageUrl = "",
                categories = emptyList()
            )
        ) {}
    }
}