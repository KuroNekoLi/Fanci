package com.cmoney.fancylog.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 事件參數 From
 * 
 * @property parameterName 參數名稱
 */
sealed class From(val parameterName: String) : Parcelable {
    fun asParameters(): Map<String, String> {
        return mapOf("from" to parameterName)
    }

    /**
     * 尚未加入任何社團的畫面
     */
    @Parcelize
    object NonGroup : From(parameterName = "NonGroup")

    /**
     * 熱門社團
     */
    @Parcelize
    object Hot : From(parameterName = "Hot")

    /**
     * 最新社團
     */
    @Parcelize
    object New : From(parameterName = "New")

    /**
     * 側欄
     */
    @Parcelize
    object SideBar : From(parameterName = "SideBar")

    /**
     * 點擊『完全公開』
     */
    @Parcelize
    object Public : From(parameterName = "Public")

    /**
     * 點擊『不公開』
     */
    @Parcelize
    object NonPublic : From(parameterName = "NonPublic")

    /**
     * 新增審核題目
     */
    @Parcelize
    object AddQuestion : From(parameterName = "AddQuestion")

    /**
     * 略過新增
     */
    @Parcelize
    object Skip : From(parameterName = "Skip")

    /**
     * 建立社團名稱
     */
    @Parcelize
    object GroupName : From(parameterName = "GroupName")

    /**
     * 建立社團權限
     */
    @Parcelize
    object GroupOpenness : From(parameterName = "GroupOpenness")

    /**
     * 建立社團佈置
     */
    @Parcelize
    object GroupArrangement : From(parameterName = "GroupArrangement")

    /**
     * 通知中心
     */
    @Parcelize
    object Notification : From(parameterName = "Notification")

    /**
     * 自己的貼文
     */
    @Parcelize
    object Poster : From(parameterName = "Poster")

    /**
     * 別人的貼文
     */
    @Parcelize
    object OthersPost : From(parameterName = "OthersPost")

    /**
     * 濫發廣告訊息
     */
    @Parcelize
    object Spam : From(parameterName = "Spam")

    /**
     * 傳送色情訊息
     */
    @Parcelize
    object SexualContent : From(parameterName = "SexualContent")

    /**
     * 騷擾行為
     */
    @Parcelize
    object Harassment : From(parameterName = "Harassment")

    /**
     * 內容與主題無關
     */
    @Parcelize
    object UnrelatedContent : From(parameterName = "UnrelatedContent")

    /**
     * 其他
     */
    @Parcelize
    object Other : From(parameterName = "Other")

    /**
     * 任一篇貼文的「顯示更多」鍵
     */
    @Parcelize
    object Post : From(parameterName = "Post")

    /**
     * 任一篇留言的「顯示更多」鍵
     */
    @Parcelize
    object Comment : From(parameterName = "Comment")

    /**
     * 在貼文外層點擊點點點
     */
    @Parcelize
    object PostList : From(parameterName = "PostList")

    /**
     * 在貼文內層點擊點點點
     */
    @Parcelize
    object InnerLayer : From(parameterName = "InnerLayer")

    /**
     * 在訊息點擊圖片
     */
    @Parcelize
    object Message : From(parameterName = "Message")

    /**
     * 全部
     */
    @Parcelize
    object All : From(parameterName = "All")

    /**
     * 聊天
     */
    @Parcelize
    object Chat : From(parameterName = "Chat")

    /**
     * 邀請連結
     */
    @Parcelize
    object Link : From(parameterName = "Link")

    /**
     * 新增分類
     */
    @Parcelize
    object Create : From(parameterName = "Create")

    /**
     * 編輯分類
     */
    @Parcelize
    object Edit : From(parameterName = "Edit")

    /**
     * 頻道排序
     */
    @Parcelize
    object Channel : From(parameterName = "Channel")

    /**
     * 分類排序
     */
    @Parcelize
    object Category : From(parameterName = "Category")

    /**
     * 社團簡介頁
     */
    @Parcelize
    object GroupIntroduction : From(parameterName = "GroupIntroduction")

    /**
     * 建立社團｜社團圖示
     */
    @Parcelize
    object AddGroupIcon : From(parameterName = "AddGroupIcon")

    /**
     * 編輯社團｜社團圖示
     */
    @Parcelize
    object EditGroupIcon : From(parameterName = "EditGroupIcon")

    /**
     * 建立社團｜社團背景
     */
    @Parcelize
    object AddHomeBackground : From(parameterName = "AddHomeBackground")

    /**
     * 編輯社團｜社團背景
     */
    @Parcelize
    object EditHomeBackground : From(parameterName = "EditHomeBackground")

    /**
     * 新增頻道
     */
    @Parcelize
    object AddChannel : From(parameterName = "AddChannel")

    /**
     * 頻道名稱
     */
    @Parcelize
    object ChannelName : From(parameterName = "ChannelName")

    /**
     * 頻道版面
     */
    @Parcelize
    object ChannelLayout : From(parameterName = "ChannelLayout")

    /**
     * 頻道公開度
     */
    @Parcelize
    object ChannelOpenness : From(parameterName = "ChannelOpenness")

    /**
     * 不公開頻道基本/中階/進階權限新增成員
     */
    @Parcelize
    object ChannelAddMember : From(parameterName = "ChannelAddMember")

    /**
     * 不公開頻道基本/中階/進階權限新增角色
     */
    @Parcelize
    object ChannelAddRole : From(parameterName = "ChannelAddRole")

    /**
     * 不公開頻道基本/中階/進階權限新增VIP方案
     */
    @Parcelize
    object ChannelAddVIP : From(parameterName = "ChannelAddVIP")

    /**
     * 頻道管理員新增角色
     */
    @Parcelize
    object AdminAddRole : From(parameterName = "AdminAddRole")

    /**
     * 建立角色
     */
    @Parcelize
    object AddRole : From(parameterName = "AddRole")

    /**
     * 角色名稱(建立角色)
     */
    @Parcelize
    object RoleName : From(parameterName = "RoleName")

    /**
     * 角色名稱(編輯角色)
     */
    @Parcelize
    object EditName : From(parameterName = "EditName")

    /**
     * 新增成員(建立角色)
     */
    @Parcelize
    object RoleAddMember : From(parameterName = "RoleAddMember")

    /**
     * 頻道排序
     */
    @Parcelize
    object ChannelOrder : From(parameterName = "ChannelOrder")

    /**
     * 分類排序
     */
    @Parcelize
    object CategoryOrder : From(parameterName = "CategoryOrder")

    /**
     * 新增分類
     */
    @Parcelize
    object AddCategory : From(parameterName = "AddCategory")

    /**
     * 新增分類時輸入名稱
     */
    @Parcelize
    object KeyinCategoryName : From(parameterName = "KeyinCategoryName")

    /**
     * 編輯分類名稱
     */
    @Parcelize
    object EditCategoryName : From(parameterName = "EditCategoryName")

    /**
     * 建立社團｜新增審核題目
     */
    @Parcelize
    object CreateGroupAddQuestion : From(parameterName = "CreateGroup.AddQuestion")

    /**
     * 編輯社團｜新增審核題目
     */
    @Parcelize
    object GroupSettingsAddQuestion : From(parameterName = "GroupSettings.AddQuestion")

    /**
     * 所有成員_新增角色
     */
    @Parcelize
    object AllMembersAddRole : From(parameterName = "AllMembersAddRole")

    /**
     * VIP方案名稱
     */
    @Parcelize
    object VIPName : From(parameterName = "VIPName")

    /**
     * VIP方案選擇頻道權限
     */
    @Parcelize
    object VIPPermissions : From(parameterName = "VIPPermissions")

    /**
     * 頻道管理
     */
    @Parcelize
    object ChannelManagement : From(parameterName = "ChannelManagement")

    /**
     * 所有成員
     */
    @Parcelize
    object AllMembers : From(parameterName = "AllMembers")

    /**
     * 角色管理
     */
    @Parcelize
    object RoleManagement : From(parameterName = "RoleManagement")

}
