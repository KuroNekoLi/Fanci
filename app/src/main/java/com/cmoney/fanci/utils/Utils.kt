package com.cmoney.fanci.utils

import android.util.Patterns
import com.cmoney.fanci.R
import com.cmoney.fanciapi.fanci.model.IEmojiCount
import java.text.SimpleDateFormat
import java.util.regex.Matcher

class Utils {
    companion object {
        /**
         * 根據 text 提出 url
         */
        fun extractLinks(text: String): List<String> {
            val links = mutableListOf<String>()
            val matcher: Matcher = Patterns.WEB_URL.matcher(text)
            while (matcher.find()) {
                val url = matcher.group()
                links.add(url)
            }
            return links
        }

        // TODO: 確認所有Emoji resource
        /**
         * @return resourceID, count
         */
        fun emojiMapping(emojiCount: IEmojiCount): List<Pair<Int, Int>> {
            val result = mutableListOf<Pair<Int, Int>>()
            emojiCount.counts?.forEach {
                val key = it.key
                val value = it.value
                val resourceId = when (key) {
                    "Angry" -> {
                        R.drawable.emoji_angry
                    }
                    "Cry" -> {
                        R.drawable.emoji_cry
                    }
                    else -> {
                        R.drawable.emoji_happy
                    }
                }
                result.add(Pair(resourceId, value))
            }
            return result
        }

        fun getDisplayTime(publishTime: Long): String {
            val sdf = SimpleDateFormat("yyyy/MM/dd")
            return sdf.format(publishTime)
        }
    }
}