package com.cmoney.fanci.ui.screens.follow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.screens.shared.ChannelBar
import kotlinx.coroutines.launch

class FollowViewModel : ViewModel() {

    private val _followData = MutableLiveData<FollowGroup>()
    val followData: LiveData<FollowGroup> = _followData

    init {
        viewModelScope.launch {
            _followData.value = mockFollowData()
        }
    }

    data class FollowGroup(
        val groupName: String,
        val avatar: String,
        val category: List<FollowCategory>
    )

    private fun mockFollowData(): FollowGroup {
        return FollowGroup(
            groupName = "韓勾ㄟ金針菇",
            avatar = "https://picsum.photos/400/400",
            category = listOf(
                FollowCategory(
                    categoryTitle = "分類1",
                    listOf(
                        ChannelBar(
                            icon = R.drawable.message,
                            channelTitle = "\uD83D\uDC4F｜歡迎新朋友"
                        ),
                        ChannelBar(
                            icon = R.drawable.message,
                            channelTitle = "\uD83D\uDC4F｜歡迎新朋友"
                        ),
                        ChannelBar(
                            icon = R.drawable.message,
                            channelTitle = "\uD83D\uDC4F｜歡迎新朋友"
                        )
                    )
                ),
                FollowCategory(
                    categoryTitle = "分類2",
                    listOf(
                        ChannelBar(
                            icon = R.drawable.message,
                            channelTitle = "\uD83D\uDC4F｜歡迎新朋友"
                        ),
                        ChannelBar(
                            icon = R.drawable.message,
                            channelTitle = "\uD83D\uDC4F｜歡迎新朋友"
                        ),
                        ChannelBar(
                            icon = R.drawable.message,
                            channelTitle = "\uD83D\uDC4F｜歡迎新朋友"
                        )
                    )
                ),
                FollowCategory(
                    categoryTitle = "分類3",
                    listOf(
                        ChannelBar(
                            icon = R.drawable.message,
                            channelTitle = "\uD83D\uDC4F｜歡迎新朋友"
                        ),
                        ChannelBar(
                            icon = R.drawable.message,
                            channelTitle = "\uD83D\uDC4F｜歡迎新朋友"
                        ),
                        ChannelBar(
                            icon = R.drawable.message,
                            channelTitle = "\uD83D\uDC4F｜歡迎新朋友"
                        )
                    )
                ),
                FollowCategory(
                    categoryTitle = "分類4",
                    listOf(
                        ChannelBar(
                            icon = R.drawable.message,
                            channelTitle = "\uD83D\uDC4F｜歡迎新朋友"
                        ),
                        ChannelBar(
                            icon = R.drawable.message,
                            channelTitle = "\uD83D\uDC4F｜歡迎新朋友"
                        ),
                        ChannelBar(
                            icon = R.drawable.message,
                            channelTitle = "\uD83D\uDC4F｜歡迎新朋友"
                        )
                    )
                )
            )
        )
    }

}