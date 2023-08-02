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
     * 社團圖示
     */
    object GroupIcon : From(parameterName = "GroupIcon")

    /**
     * 首頁背景
     */
    object HomeBackground : From(parameterName = "HomeBackground")

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
     * 在貼文外層點擊圖片
     */
    object PostList : From(parameterName = "PostList")

    /**
     * 在貼文內層點擊圖片
     */
    object InnerLayer : From(parameterName = "InnerLayer")

    /**
     * 在貼文內層留言區點擊圖片
     */
    object Comment : From(parameterName = "Comment")

    /**
     * 自己的貼文
     */
    object Poster : From(parameterName = "Poster")

    /**
     * 別人的貼文
     */
    object OthersPost : From(parameterName = "OthersPost")

    /**
     * 打開相機
     */
    object OpenCamera : From(parameterName = "OpenCamera")

    /**
     * 從相簿選擇
     */
    object SelectFromAlbum : From(parameterName = "SelectFromAlbum")

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
     * 貼文
     */
    object Post : From(parameterName = "Post")

    /**
     * 聊天
     */
    object Chat : From(parameterName = "Chat")

    /**
     * 已登入畫面
     */
    object Login : From(parameterName = "Login")

    /**
     * 未登入畫面
     */
    object Logout : From(parameterName = "Logout")

    /**
     * 邀請連結
     */
    object Link : From(parameterName = "Link")

    /**
     * 從相簿選擇
     */
    object Album : From(parameterName = "Album")

    /**
     * 從Fanci圖庫選擇
     */
    object Gallery : From(parameterName = "Gallery")

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
     * 建立角色
     */
    object AddRole : From(parameterName = "AddRole")

    /**
     * 角色名稱(建立角色)
     */
    object RoleName : From(parameterName = "RoleName")

    /**
     * 新增成員(建立角色)
     */
    object RoleAddMember : From(parameterName = "RoleAddMember")

    /**
     * 新增分類
     */
    object AddCategory : From(parameterName = "AddCategory")

    /**
     * 分類名稱
     */
    object CategoryName : From(parameterName = "CategoryName")

    /**
     * 新增審核題目
     */
    object AddReviewQuestion : From(parameterName = "AddReviewQuestion")

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
