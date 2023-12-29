package com.cmoney.kolfanci.utils

import android.util.Patterns
import androidx.annotation.DrawableRes
import com.cmoney.fanciapi.fanci.model.Emojis
import com.cmoney.fanciapi.fanci.model.IEmojiCount
import com.cmoney.fanciapi.fanci.model.ReportReason
import com.cmoney.kolfanci.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
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

        /**
         * Chat room message display send time.
         */
        fun getDisplayTime(publishTime: Long): String {
            val sdf = SimpleDateFormat("a hh:mm E")
            return sdf.format(publishTime)
        }

        fun getTimeGroupByKey(publishTime: Long): String {
            val sdf = SimpleDateFormat("yyyy/MM/dd (E)")
            return sdf.format(publishTime)
        }

        fun getSearchDisplayTime(publishTime: Long): String {
            val sdf = SimpleDateFormat("yyyy.MM.dd")
            return sdf.format(publishTime)
        }

        fun timesMillisToDate(timestamp: Long): String {
            val calendar = Calendar.getInstance(Locale.TAIWAN).clone() as Calendar
            val timePassed = System.currentTimeMillis() - timestamp
            return when {
                timePassed / (1000 * 60 * 60 * 24) >= 2 -> {
                    SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN).format(Date(timestamp))
                }

                timePassed / (1000 * 60 * 60 * 24) >= 1 -> {
                    "昨天"
                }

                timePassed / (1000 * 60 * 60) > 0 -> {
                    StringBuilder().append(timePassed / (1000 * 60 * 60)).append("小時前")
                        .toString()
                }

                timePassed / (1000 * 60) > 0 -> {
                    StringBuilder().append(timePassed / (1000 * 60)).append("分鐘前").toString()
                }

                else -> {
                    "剛剛"
                }
            }
        }

        fun timeMillisToMinutesSeconds(timestamp: Long): String {
            val minutes = timestamp / 1000 / 60
            val seconds = (timestamp - (1000 * 60 * minutes)) / 1000
            return String.format("%02d:%02d", minutes, seconds)
        }

        /**
         * 加密 邀請碼
         *
         * @param input groupId
         * @param key xor key(default: 1357)
         * @param bits full bits count
         *
         * @return 加密過後字串 length = bits / 4
         */
        fun encryptInviteCode(input: Int, key: Int = 1357, bits: Int = 32): String {
            val binaryString = Integer.toBinaryString(input)
            val length = binaryString.length

            val finalString = if (length < bits) {
                "0".repeat(bits - length) + binaryString
            } else if (length > bits) {
                binaryString.substring(length - bits)
            } else {
                binaryString
            }

            val xorResult = performXOR(finalString, key)
            val decimal = xorResult.toInt(2)
            val output = Integer.toHexString(decimal)
            val outputLen = output.length
            val mustLen = bits / 4

            return if (outputLen < mustLen) "0".repeat(mustLen - outputLen) + output else output
        }

        /**
         * 解密 邀請碼
         *
         * @param input 加密字串
         * @param key xor key(default: 1357)
         * @param bits full bits count
         *
         * @return groupId
         */
        fun decryptInviteCode(input: String, key: Int = 1357, bits: Int = 32): Int? {
            try {
                val decimal = input.toInt(16)
                val binaryString = Integer.toBinaryString(decimal)
                val length = binaryString.length

                val finalString = if (length < bits) {
                    "0".repeat(bits - length) + binaryString
                } else if (length > bits) {
                    binaryString.substring(length - bits)
                } else {
                    binaryString
                }

                val xorResult = performXOR(finalString, key)
                return xorResult.toInt(2)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        private fun performXOR(binaryString: String, xorKey: Int): String {
            val adjustedXORKey = Integer.toBinaryString(xorKey).padStart(binaryString.length, '0')

            val result = StringBuilder()
            for (i in binaryString.indices) {
                val bit1 = binaryString[i]
                val bit2 = adjustedXORKey[i]
                result.append(if (bit1 == bit2) '0' else '1')
            }
            return result.toString()
        }

        /**
         * 比較兩個Long時間是否在同一分鐘
         */
        fun areTimestampsInSameMinute(timestamp1: Long, timestamp2: Long): Boolean {
            val dateFormat = SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault())

            val dateStr1 = dateFormat.format(Date(timestamp1))
            val dateStr2 = dateFormat.format(Date(timestamp2))

            return dateStr1 == dateStr2
        }
    }
}