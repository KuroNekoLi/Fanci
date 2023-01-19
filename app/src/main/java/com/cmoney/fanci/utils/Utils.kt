package com.cmoney.fanci.utils

import android.util.Patterns
import androidx.annotation.DrawableRes
import com.cmoney.fanci.R
import com.cmoney.fanciapi.fanci.model.Emojis
import com.cmoney.fanciapi.fanci.model.IEmojiCount
import com.cmoney.fanciapi.fanci.model.ReportReason
import java.text.SimpleDateFormat
import java.util.regex.Matcher

class Utils {
    companion object {

        /**
         * 根據顯示文案 回傳對應 ReportReason model
         *
         * @param reasonText 檢舉文案
         * @return ReportReason model
         */
        fun getReportReason(reasonText: String): ReportReason {
            return when (reasonText) {
                "濫發廣告訊息" -> ReportReason.spamAds
                "傳送色情訊息" -> ReportReason.adultContent
                "騷擾行為" -> ReportReason.harass
                "內容與主題無關" -> ReportReason.notRelated
                else -> {
                    ReportReason.other
                }
            }
        }

        /**
         * 取得 檢舉文案
         * @param reportReason ReportReason model
         * @return display text
         */
        fun getReportReasonShowText(reportReason: ReportReason): String {
            return when (reportReason) {
                ReportReason.spamAds -> "濫發廣告訊息"
                ReportReason.adultContent -> "傳送色情訊息"
                ReportReason.harass -> "騷擾行為"
                ReportReason.notRelated -> "內容與主題無關"
                else -> "其他"
            }
        }

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
         * 將Emoji 圖檔 轉換對應Server Key
         * @param resourceId emoji 圖檔
         */
        fun emojiResourceToServerKey(@DrawableRes resourceId: Int): Emojis {
            return when (resourceId) {
                R.drawable.emoji_money -> {
                    Emojis.money
                }
                R.drawable.emoji_shock -> {
                    Emojis.shock
                }
                R.drawable.emoji_laugh -> {
                    Emojis.laugh
                }
                R.drawable.emoji_angry -> {
                    Emojis.angry
                }
                R.drawable.emoji_think -> {
                    Emojis.think
                }
                R.drawable.emoji_cry -> {
                    Emojis.cry
                }
                R.drawable.emoji_like -> {
                    Emojis.like
                }
                else -> Emojis.like
            }
        }

        /**
         * 將Server 資料 map 對應圖檔
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

            emojiCount.cry?.let {
                result.add(Pair(R.drawable.emoji_cry, it))
            }

            emojiCount.like?.let {
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