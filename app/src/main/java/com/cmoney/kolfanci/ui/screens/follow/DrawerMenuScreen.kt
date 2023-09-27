package com.cmoney.kolfanci.ui.screens.follow

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.follow.model.GroupItem
import com.cmoney.kolfanci.ui.screens.shared.item.RedDotItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun DrawerMenuScreen(
    modifier: Modifier = Modifier,
    groupList: List<GroupItem>,
    onClick: (GroupItem) -> Unit,
    onPlusClick: () -> Unit,
    onProfile: () -> Unit,
    onNotification: () -> Unit
) {
    Column(
        modifier = modifier
            .background(LocalColor.current.env_100)
            .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 5.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 5.dp)
        ) {
            items(groupList) { item ->
                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.BottomEnd
                ) {
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
                            placeholder = painterResource(id = R.drawable.placeholder)
                        )
                    }

                    //TODO: api data
                    RedDotItemScreen(
                        text = "99+"
                    )
                }

                Spacer(modifier = Modifier.height(17.dp))
            }
        }

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(LocalColor.current.env_80)
                .clickable {
                    onNotification.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.menu_bell),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = LocalColor.current.primary)
            )
        }

        Spacer(modifier = Modifier.height(17.dp))

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(LocalColor.current.env_80)
                .clickable {
                    onPlusClick.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.menu_plus),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = LocalColor.current.primary)
            )
        }
        Spacer(modifier = Modifier.height(17.dp))

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(LocalColor.current.env_80)
                .clickable {
                    onProfile.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.menu_profile),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = LocalColor.current.primary)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}

@Preview
@Composable
fun DrawerMenuScreenPreview() {
    FanciTheme {
        DrawerMenuScreen(
            groupList = listOf(
                GroupItem(
                    isSelected = true, groupModel = Group(
                        id = "",
                        name = "",
                        description = "Description",
                        coverImageUrl = "",
                        thumbnailImageUrl = "",
                        categories = emptyList()
                    )
                )
            ),
            onClick = {},
            onPlusClick = {},
            onProfile = {},
            onNotification = {}
        )
    }
}