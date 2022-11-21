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

        /**
         * @return resourceID, count
         */
        fun emojiMapping(emojiCount: IEmojiCount): List<Pair<Int, Int>> {
            val result = mutableListOf<Pair<Int, Int>>()

            emojiCount.money?.let {
                result.add(Pair(R.drawable.emoji_money, it))
            }

            emojiCount.shock?.let {
                result.add(Pair(R.drawable.emoji_shock, it))
            }

            emojiCount.laugh?.let {
                result.add(Pair(R.drawable.emoji_laugh, it))
            }

            emojiCount.angry?.let {
                result.add(Pair(R.drawable.emoji_angry, it))
            }

            emojiCount.think?.let {
                result.add(Pair(R.drawable.emoji_think, it))
            }

            emojiCount.money?.let {
                result.add(Pair(R.drawable.emoji_cry, it))
            }

            emojiCount.money?.let {
                result.add(Pair(R.drawable.emoji_like, it))
            }
            return result
        }

        fun getDisplayTime(publishTime: Long): String {
            val sdf = SimpleDateFormat("yyyy/MM/dd")
            return sdf.format(publishTime)
        }
    }
}