package com.cmoney.fanci.ui.screens.follow

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cmoney.fanci.R
import com.cmoney.fanci.model.GroupModel
import com.cmoney.fanci.ui.screens.follow.model.GroupItem
import com.cmoney.fanci.ui.theme.Black_181C23
import com.cmoney.fanci.ui.theme.Black_282A2D

@Composable
fun DrawerMenuScreen(
    modifier: Modifier = Modifier,
    groupList: List<GroupItem>,
    onClick: (GroupItem) -> Unit,
    onSearch: () -> Unit
) {
    Column(
        modifier = modifier
            .background(Black_282A2D)
            .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 5.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 5.dp)
        ) {
            items(groupList) { item ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .size(55.dp)
                        .aspectRatio(1f)
                        .clickable {
                            onClick.invoke(item)
                        },
                    border = (if (item.isSelected) {
                        BorderStroke(2.dp, Color.White)
                    } else {
                        null
                    })
                ) {
                    AsyncImage(
                        model = item.groupModel.thumbnailImageUrl,
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        placeholder = painterResource(id = R.drawable.resource_default)
                    )
                }

                Spacer(modifier = Modifier.height(17.dp))
            }
        }

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Black_181C23),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = R.drawable.bell), contentDescription = null)
        }
        Spacer(modifier = Modifier.height(7.dp))

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Black_181C23)
                .clickable {
                    onSearch.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = R.drawable.search), contentDescription = null)
        }
        Spacer(modifier = Modifier.height(7.dp))

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Black_181C23),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = R.drawable.plus), contentDescription = null)
        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DrawerMenuScreenPreview() {
    DrawerMenuScreen(
        groupList = listOf(
            GroupItem(
                isSelected = true, groupModel = GroupModel(
                    groupId = "",
                    name = "",
                    description = "Description",
                    coverImageUrl = "",
                    thumbnailImageUrl = "",
                    emptyList()
                )
            )
        ),
        onClick = {}
    ) {

    }
}