package com.cmoney.fanci.ui.screens.follow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.screens.shared.ChannelBar
import kotlinx.coroutines.launch

class FollowViewModel: ViewModel() {

    private val _followData = MutableLiveData<List<FollowCategory>>()
    val followData: LiveData<List<FollowCategory>> = _followData

    init {
        viewModelScope.launch {
            _followData.value = mockFollowData()
        }
    }

    private fun mockFollowData(): List<FollowCategory> {
        return listOf(
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
    }

}