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
     * 尚未加入任何社團的畫面
     */
    object NonGroup : From(parameterName = "NonGroup")

    /**
     * 熱門社團
     */
    object Hot : From(parameterName = "Hot")

    /**
     * 最新社團
     */
    object New : From(parameterName = "New")

    /**
     * 點擊『完全公開』
     */
    object Public : From(parameterName = "Public")

    /**
     * 點擊『不公開』
     */
    object NonPublic : From(parameterName = "NonPublic")

    /**
     * 新增審核題目
     */
    object AddQuestion : From(parameterName = "AddQuestion")

    /**
     * 略過新增
     */
    object Skip : From(parameterName = "Skip")

    /**
     * 建立社團名稱
     */
    object GroupName : From(parameterName = "GroupName")

    /**
     * 建立社團權限
     */
    object GroupOpenness : From(parameterName = "GroupOpenness")

    /**
     * 建立社團佈置
     */
    object GroupArrangement : From(parameterName = "GroupArrangement")

    /**
     * 任一篇貼文的「顯示更多」鍵
     */
    object Post : From(parameterName = "Post")

    /**
     * 任一篇留言的「顯示更多」鍵
     */
    object Comment : From(parameterName = "Comment")

    /**
     * 在貼文外層點擊圖片
     */
    object PostList : From(parameterName = "PostList")

    /**
     * 在貼文內層點擊圖片
     */
    object InnerLayer : From(parameterName = "InnerLayer")

    /**
     * 自己的貼文
     */
    object Poster : From(parameterName = "Poster")

    /**
     * 別人的貼文
     */
    object OthersPost : From(parameterName = "OthersPost")

    /**
     * 濫發廣告訊息
     */
    object Spam : From(parameterName = "Spam")

    /**
     * 傳送色情訊息
     */
    object SexualContent : From(parameterName = "SexualContent")

    /**
     * 騷擾行為
     */
    object Harassment : From(parameterName = "Harassment")

    /**
     * 內容與主題無關
     */
    object UnrelatedContent : From(parameterName = "UnrelatedContent")

    /**
     * 其他
     */
    object Other : From(parameterName = "Other")

    /**
     * 全部
     */
    object All : From(parameterName = "All")

    /**
     * 聊天
     */
    object Chat : From(parameterName = "Chat")

    /**
     * 邀請連結
     */
    object Link : From(parameterName = "Link")

    /**
     * 頻道排序
     */
    object Channel : From(parameterName = "Channel")

    /**
     * 分類排序
     */
    object Category : From(parameterName = "Category")

    /**
     * 新增頻道
     */
    object Create : From(parameterName = "Create")

    /**
     * 編輯頻道
     */
    object Edit : From(parameterName = "Edit")

    /**
     * 社團簡介頁
     */
    object GroupIntroduction : From(parameterName = "GroupIntroduction")

    /**
     * 建立社團｜社團圖示
     */
    object AddGroupIcon : From(parameterName = "AddGroupIcon")

    /**
     * 編輯社團｜社團圖示
     */
    object EditGroupIcon : From(parameterName = "EditGroupIcon")

    /**
     * 建立社團｜社團背景
     */
    object AddHomeBackground : From(parameterName = "AddHomeBackground")

    /**
     * 編輯社團｜社團背景
     */
    object EditHomeBackground : From(parameterName = "EditHomeBackground")

    /**
     * 主題預覽
     */
    object ThemeColor : From(parameterName = "ThemeColor")

    /**
     * 新增頻道
     */
    object AddChannel : From(parameterName = "AddChannel")

    /**
     * 頻道名稱
     */
    object ChannelName : From(parameterName = "ChannelName")

    /**
     * 頻道公開度
     */
    object ChannelOpenness : From(parameterName = "ChannelOpenness")

    /**
     * 不公開頻道基本/中階/進階權限新增成員
     */
    object ChannelAddMember : From(parameterName = "ChannelAddMember")

    /**
     * 不公開頻道基本/中階/進階權限新增角色
     */
    object ChannelAddRole : From(parameterName = "ChannelAddRole")

    /**
     * 不公開頻道基本/中階/進階權限新增VIP方案
     */
    object ChannelAddVIP : From(parameterName = "ChannelAddVIP")

    /**
     * 頻道管理員新增角色
     */
    object AdminAddRole : From(parameterName = "AdminAddRole")

    /**
     * 建立角色
     */
    object AddRole : From(parameterName = "AddRole")

    /**
     * 角色名稱(建立角色)
     */
    object RoleName : From(parameterName = "RoleName")

    /**
     * 角色名稱(編輯角色)
     */
    object EditName : From(parameterName = "EditName")

    /**
     * 新增成員(建立角色)
     */
    object RoleAddMember : From(parameterName = "RoleAddMember")

    /**
     * 頻道排序
     */
    object ChannelOrder : From(parameterName = "ChannelOrder")

    /**
     * 分類排序
     */
    object CategoryOrder : From(parameterName = "CategoryOrder")

    /**
     * 新增分類
     */
    object AddCategory : From(parameterName = "AddCategory")

    /**
     * 新增分類時輸入名稱
     */
    object KeyinCategoryName : From(parameterName = "KeyinCategoryName")

    /**
     * 編輯分類名稱
     */
    object EditCategoryName : From(parameterName = "EditCategoryName")

    /**
     * 建立社團｜新增審核題目
     */
    object CreatGroupAddQuestion : From(parameterName = "CreatGroup.AddQuestion")

    /**
     * 編輯社團｜新增審核題目
     */
    object GroupSettingsAddQuestion : From(parameterName = "GroupSettings.AddQuestion")

    /**
     * 所有成員_新增角色
     */
    object AllMembersAddRole : From(parameterName = "AllMembersAddRole")

    /**
     * VIP方案名稱
     */
    object VIPName : From(parameterName = "VIPName")

    /**
     * VIP方案選擇頻道權限
     */
    object VIPPermissions : From(parameterName = "VIPPermissions")

    /**
     * 頻道管理
     */
    object ChannelManagement : From(parameterName = "ChannelManagement")

    /**
     * 所有成員
     */
    object AllMembers : From(parameterName = "AllMembers")

    /**
     * 角色管理
     */
    object RoleManagement : From(parameterName = "RoleManagement")

}
