package com.cmoney.kolfanci.model.analytics.data

/**
 * 頁面
 *
 * @property eventName 事件名稱
 */
sealed class Page(val eventName: String) {
    /**
     * 首頁社團
     */
    object Home: Page("Home")

    /**
     * 會員頁
     */
    object MemberPage: Page("MemberPage") {

    }

    object AvatarAndNickname: Page("avatar_and_nickname")

    object NotLoggedInPage: Page("NotLoggedInPage")

    object Avatar: Page("Avatar")

    object Nickname: Page("Nickname")

    object AccountManagement: Page("AccountManagement")

    object Logout: Page("Logout")

    object DeleteAccount: Page("DeleteAccount")

    object TermsOfService: Page("TermsOfService")

    object PrivacyPolicy: Page("PrivacyPolicy")

    object CopyrightPolicy: Page("CopyrightPolicy")

    object Feedback: Page("Feedback")
}