package com.cmoney.fancylog.model.data

/**
 * 事件參數 From
 * 
 * @property parameterName 參數名稱
 */
sealed class From(val parameterName: String) {
    fun asParameters(): Map<String, String> {
        return mapOf("from" to parameterName)
    }
    /**
     * 在貼文外層點擊圖片
     */
    object PostList : From(parameterName = "PostList")

    /**
     * 自己的貼文
     */
    object Poster : From(parameterName = "Poster")

    /**
     * 打開相機
     */
    object OpenCamera : From(parameterName = "OpenCamera")

    /**
     * 濫發廣告訊息
     */
    object Spam : From(parameterName = "Spam")

    /**
     * 全部
     */
    object All : From(parameterName = "All")

    /**
     * 已登入畫面
     */
    object Login : From(parameterName = "Login")

    /**
     * 尚未加入任何社團的畫面
     */
    object NonGroup : From(parameterName = "NonGroup")

    /**
     * 從相簿選擇
     */
    object Album : From(parameterName = "Album")

    /**
     * 社團名稱頁
     */
    object GroupName : From(parameterName = "GroupName")

    /**
     * 頻道管理
     */
    object ChannelManagement : From(parameterName = "ChannelManagement")

}
