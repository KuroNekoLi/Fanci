package com.cmoney.fanci.utils

import android.util.Patterns
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
    }
}