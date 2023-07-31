package com.cmoney.fancylog.model.data

/**
 * 點擊事件
 * 
 * @property eventName 事件名稱
 */
sealed class Clicked(val eventName: String) {
    /**
     * 首頁.預設未加入社團_社團_點擊
     */
    object HomeNotJoinGroup_Group : Clicked(eventName = "Home.NotJoinGroup_Group")

    /**
     * 側邊欄_社團_點擊
     */
    object NavigationBar_Group : Clicked(eventName = "NavigationBar_Group")

    /**
     * 側邊欄_通知_點擊
     */
    object NavigationBar_Notification : Clicked(eventName = "NavigationBar_Notification")

    /**
     * 側邊欄_探索社團_點擊
     */
    object NavigationBar_ExploreGroup : Clicked(eventName = "NavigationBar_ExploreGroup")

    /**
     * 側邊欄_會員頁_點擊
     */
    object NavigationBar_MemberPage : Clicked(eventName = "NavigationBar_MemberPage")

    /**
     * 會員頁_信箱登入_點擊
     */
    object MemberPage_EmailLogin : Clicked(eventName = "MemberPage_EmailLogin")

    /**
     * 會員頁_社群帳號登入_點擊
     */
    object MemberPage_SocialAccountLogin : Clicked(eventName = "MemberPage_SocialAccountLogin")

    /**
     * 會員頁_首頁_點擊
     */
    object MemberPage_Home : Clicked(eventName = "MemberPage_Home")

    /**
     * 會員頁_頭像與暱稱_點擊
     */
    object MemberPage_AvatarAndNickname : Clicked(eventName = "MemberPage_AvatarAndNickname")

    /**
     * 頭像與暱稱_頭像_點擊
     */
    object AvatarAndNickname_Avatar : Clicked(eventName = "AvatarAndNickname_Avatar")

    /**
     * 頭像與暱稱_暱稱_點擊
     */
    object AvatarAndNickname_Nickname : Clicked(eventName = "AvatarAndNickname_Nickname")

    /**
     * 會員頁_帳號管理_點擊
     */
    object MemberPage_AccountManagement : Clicked(eventName = "MemberPage_AccountManagement")

    /**
     * 帳號管理_登出帳號_點擊
     */
    object AccountManagement_Logout : Clicked(eventName = "AccountManagement_Logout")

    /**
     * 登出帳號_確定登出_點擊
     */
    object Logout_ConfirmLogout : Clicked(eventName = "Logout_ConfirmLogout")

    /**
     * 登出帳號_返回_點擊
     */
    object Logout_Return : Clicked(eventName = "Logout_Return")

    /**
     * 帳號管理_刪除帳號_點擊
     */
    object AccountManagement_DeleteAccount : Clicked(eventName = "AccountManagement_DeleteAccount")

    /**
     * 刪除帳號_確定刪除_點擊
     */
    object DeleteAccount_ConfirmDelete : Clicked(eventName = "DeleteAccount_ConfirmDelete")

    /**
     * 刪除帳號_返回_點擊
     */
    object DeleteAccount_Return : Clicked(eventName = "DeleteAccount_Return")

    /**
     * 會員頁_服務條款_點擊
     */
    object MemberPage_TermsOfService : Clicked(eventName = "MemberPage_TermsOfService")

    /**
     * 會員頁_隱私權政策_點擊
     */
    object MemberPage_PrivacyPolicy : Clicked(eventName = "MemberPage_PrivacyPolicy")

    /**
     * 會員頁_著作權政策_點擊
     */
    object MemberPage_CopyrightPolicy : Clicked(eventName = "MemberPage_CopyrightPolicy")

    /**
     * 會員頁_意見回饋_點擊
     */
    object MemberPage_Feedback : Clicked(eventName = "MemberPage_Feedback")

    /**
     * 貼文_顯示更多_點擊
     */
    object Post_ShowMore : Clicked(eventName = "Post_ShowMore")

    /**
     * 貼文_圖片_點擊
     */
    object Post_Image : Clicked(eventName = "Post_Image")

    /**
     * 貼文_進入內層_點擊
     */
    object Post_EnterInnerLayer : Clicked(eventName = "Post_EnterInnerLayer")

    /**
     * 貼文_發表貼文_點擊
     */
    object Post_PublishPost : Clicked(eventName = "Post_PublishPost")

    /**
     * 貼文_編輯貼文_點擊
     */
    object Post_EditPost : Clicked(eventName = "Post_EditPost")

    /**
     * 貼文_置頂貼文_點擊
     */
    object Post_PinPost : Clicked(eventName = "Post_PinPost")

    /**
     * 貼文.置頂貼文_確定置頂_點擊
     */
    object PostPinPost_ConfirmPin : Clicked(eventName = "Post.PinPost_ConfirmPin")

    /**
     * 貼文.置頂貼文_取消_點擊
     */
    object PostPinPost_Cancel : Clicked(eventName = "Post.PinPost_Cancel")

    /**
     * 貼文_取消置頂貼文_點擊
     */
    object Post_UnpinPost : Clicked(eventName = "Post_UnpinPost")

    /**
     * 貼文.取消置頂貼文_確定取消_點擊
     */
    object PostUnpinPost_ConfirmUnpin : Clicked(eventName = "Post.UnpinPost_ConfirmUnpin")

    /**
     * 貼文.取消置頂貼文_返回_點擊
     */
    object PostUnpinPost_Return : Clicked(eventName = "Post.UnpinPost_Return")

    /**
     * 貼文_檢舉貼文_點擊
     */
    object Post_ReportPost : Clicked(eventName = "Post_ReportPost")

    /**
     * 貼文_刪除貼文_點擊
     */
    object Post_DeletePost : Clicked(eventName = "Post_DeletePost")

    /**
     * 貼文.刪除貼文_確定刪除_點擊
     */
    object PostDeletePost_ConfirmDelete : Clicked(eventName = "Post.DeletePost_ConfirmDelete")

    /**
     * 貼文.刪除貼文_取消_點擊
     */
    object PostDeletePost_Cancel : Clicked(eventName = "Post.DeletePost_Cancel")

    /**
     * 留言_開啟鍵盤_點擊
     */
    object Comment_OpenKeyboard : Clicked(eventName = "Comment_OpenKeyboard")

    /**
     * 留言_回覆_點擊
     */
    object Comment_Reply : Clicked(eventName = "Comment_Reply")

    /**
     * 留言_插入圖片_點擊
     */
    object Comment_InsertImage : Clicked(eventName = "Comment_InsertImage")

    /**
     * 留言_傳送鍵_點擊
     */
    object Comment_SendButton : Clicked(eventName = "Comment_SendButton")

    /**
     * 留言_編輯留言_點擊
     */
    object Comment_EditComment : Clicked(eventName = "Comment_EditComment")

    /**
     * 留言_檢舉留言_點擊
     */
    object Comment_ReportComment : Clicked(eventName = "Comment_ReportComment")

    /**
     * 留言_刪除留言_點擊
     */
    object Comment_DeleteComment : Clicked(eventName = "Comment_DeleteComment")

    /**
     * 留言_編輯回覆_點擊
     */
    object Comment_EditReply : Clicked(eventName = "Comment_EditReply")

    /**
     * 留言_檢舉回覆_點擊
     */
    object Comment_ReportReply : Clicked(eventName = "Comment_ReportReply")

    /**
     * 留言_刪除回覆_點擊
     */
    object Comment_DeleteReply : Clicked(eventName = "Comment_DeleteReply")

    /**
     * 貼文_開啟鍵盤_點擊
     */
    object Post_OpenKeyboard : Clicked(eventName = "Post_OpenKeyboard")

    /**
     * 貼文_選取照片_點擊
     */
    object Post_SelectPhoto : Clicked(eventName = "Post_SelectPhoto")

    /**
     * 貼文.選取照片_打開相機_點擊
     */
    object PostSelectPhoto_OpenCamera : Clicked(eventName = "Post.SelectPhoto_OpenCamera")

    /**
     * 貼文.選取照片_從相簿選取_點擊
     */
    object PostSelectPhoto_SelectFromAlbum : Clicked(eventName = "Post.SelectPhoto_SelectFromAlbum")

    /**
     * 貼文.選取照片_返回_點擊
     */
    object PostSelectPhoto_Return : Clicked(eventName = "Post.SelectPhoto_Return")

    /**
     * 貼文_發布_點擊
     */
    object Post_Publish : Clicked(eventName = "Post_Publish")

    /**
     * 貼文_捨棄_點擊
     */
    object Post_Discard : Clicked(eventName = "Post_Discard")

    /**
     * 貼文_繼續編輯_點擊
     */
    object Post_ContinueEditing : Clicked(eventName = "Post_ContinueEditing")

    /**
     * 貼文.空白貼文_修改_點擊
     */
    object PostBlankPost_Edit : Clicked(eventName = "Post.BlankPost_Edit")

    /**
     * 訊息_開啟鍵盤_點擊
     */
    object Message_OpenKeyboard : Clicked(eventName = "Message_OpenKeyboard")

    /**
     * 訊息_插入圖片_點擊
     */
    object Message_InsertImage : Clicked(eventName = "Message_InsertImage")

    /**
     * 訊息_圖片_點擊
     */
    object Message_Image : Clicked(eventName = "Message_Image")

    /**
     * 訊息_傳送鍵_點擊
     */
    object Message_SendButton : Clicked(eventName = "Message_SendButton")

    /**
     * 訊息_常壓訊息_點擊
     */
    object Message_LongPressMessage : Clicked(eventName = "Message_LongPressMessage")

    /**
     * 訊息.常壓訊息_表情符號_點擊
     */
    object MessageLongPressMessage_Emoji : Clicked(eventName = "Message.LongPressMessage_Emoji")

    /**
     * 訊息.常壓訊息_回覆_點擊
     */
    object MessageLongPressMessage_Reply : Clicked(eventName = "Message.LongPressMessage_Reply")

    /**
     * 訊息.常壓訊息_複製訊息_點擊
     */
    object MessageLongPressMessage_CopyMessage : Clicked(eventName = "Message.LongPressMessage_CopyMessage")

    /**
     * 訊息.常壓訊息_置頂訊息_點擊
     */
    object MessageLongPressMessage_PinMessage : Clicked(eventName = "Message.LongPressMessage_PinMessage")

    /**
     * 訊息.常壓訊息_檢舉_點擊
     */
    object MessageLongPressMessage_Report : Clicked(eventName = "Message.LongPressMessage_Report")

    /**
     * 訊息.常壓訊息_刪除訊息_點擊
     */
    object MessageLongPressMessage_DeleteMessage : Clicked(eventName = "Message.LongPressMessage_DeleteMessage")

    /**
     * 訊息.常壓訊息_收回訊息_點擊
     */
    object MessageLongPressMessage_UnsendMessage : Clicked(eventName = "Message.LongPressMessage_UnsendMessage")

    /**
     * 檢舉_確定檢舉_點擊
     */
    object Report_ConfirmReport : Clicked(eventName = "Report_ConfirmReport")

    /**
     * 檢舉_取消_點擊
     */
    object Report_Cancel : Clicked(eventName = "Report_Cancel")

    /**
     * 檢舉原因_原因_點擊
     */
    object ReportReason_Reason : Clicked(eventName = "ReportReason_Reason")

    /**
     * 檢舉原因_取消檢舉_點擊
     */
    object ReportReason_CancelReport : Clicked(eventName = "ReportReason_CancelReport")

    /**
     * 刪除訊息_確定刪除_點擊
     */
    object DeleteMessage_ConfirmDelete : Clicked(eventName = "DeleteMessage_ConfirmDelete")

    /**
     * 刪除訊息_取消_點擊
     */
    object DeleteMessage_Cancel : Clicked(eventName = "DeleteMessage_Cancel")

    /**
     * 訊息_重試_點擊
     */
    object Message_Retry : Clicked(eventName = "Message_Retry")

    /**
     * 訊息.重試_重新傳送_點擊
     */
    object MessageRetry_Resend : Clicked(eventName = "Message.Retry_Resend")

    /**
     * 訊息.重試_刪除訊息_點擊
     */
    object MessageRetry_DeleteMessage : Clicked(eventName = "Message.Retry_DeleteMessage")

    /**
     * 訊息.重試_返回_點擊
     */
    object MessageRetry_Return : Clicked(eventName = "Message.Retry_Return")

    /**
     * 表情符號_現有的表情_點擊
     */
    object Emoticon_ExistingEmoticon : Clicked(eventName = "Emoticon_ExistingEmoticon")

    /**
     * 表情符號_新增表情_點擊
     */
    object Emoticon_AddEmoticon : Clicked(eventName = "Emoticon_AddEmoticon")

    /**
     * 內容搜尋_點擊
     */
    object ContentSearch : Clicked(eventName = "ContentSearch")

    /**
     * 內容搜尋_搜尋_點擊
     */
    object ContentSearch_Search : Clicked(eventName = "ContentSearch_Search")

    /**
     * 內容搜尋_全部_點擊
     */
    object ContentSearch_All : Clicked(eventName = "ContentSearch_All")

    /**
     * 內容搜尋_貼文_點擊
     */
    object ContentSearch_Post : Clicked(eventName = "ContentSearch_Post")

    /**
     * 內容搜尋_聊天_點擊
     */
    object ContentSearch_Chat : Clicked(eventName = "ContentSearch_Chat")

    /**
     * 內容搜尋_查看_點擊
     */
    object ContentSearch_View : Clicked(eventName = "ContentSearch_View")

    /**
     * 探索社團_搜尋_點擊
     */
    object ExploreGroup_Search : Clicked(eventName = "ExploreGroup_Search")

    /**
     * 探索社團_熱門社團_點擊
     */
    object ExploreGroup_PopularGroups : Clicked(eventName = "ExploreGroup_PopularGroups")

    /**
     * 探索社團_最新社團_點擊
     */
    object ExploreGroup_NewestGroups : Clicked(eventName = "ExploreGroup_NewestGroups")

    /**
     * 建立社團_點擊
     */
    object CreateGroup : Clicked(eventName = "CreateGroup")

    /**
     * 上一步_點擊
     */
    object PreviousStep : Clicked(eventName = "PreviousStep")

    /**
     * 下一步_點擊
     */
    object NextStep : Clicked(eventName = "NextStep")

    /**
     * 建立社團GO_點擊
     */
    object CreateGroupGO : Clicked(eventName = "CreateGroupGO")

    /**
     * 社團_加入_點擊
     */
    object Group_Join : Clicked(eventName = "Group_Join")

    /**
     * 社團_申請加入_點擊
     */
    object Group_ApplyToJoin : Clicked(eventName = "Group_ApplyToJoin")

    /**
     * 社團_社團設定_點擊
     */
    object Group_GroupSettings : Clicked(eventName = "Group_GroupSettings")

    /**
     * 社團設定_邀請社團成員_點擊
     */
    object GroupSettings_InviteGroupMember : Clicked(eventName = "GroupSettings_InviteGroupMember")

    /**
     * 社團設定_退出社團_點擊
     */
    object GroupSettings_LeaveGroup : Clicked(eventName = "GroupSettings_LeaveGroup")

    /**
     * 退出社團_確定退出_點擊
     */
    object LeaveGroup_ConfirmLeave : Clicked(eventName = "LeaveGroup_ConfirmLeave")

    /**
     * 退出社團_取消退出_點擊
     */
    object LeaveGroup_CancelLeave : Clicked(eventName = "LeaveGroup_CancelLeave")

    /**
     * 社團設定_社團設定_點擊
     */
    object GroupSettings_GroupSettingsPage : Clicked(eventName = "GroupSettings_GroupSettingsPage")

    /**
     * 社團名稱_點擊
     */
    object GroupName : Clicked(eventName = "GroupName")

    /**
     * 社團名稱_名稱_點擊
     */
    object GroupName_Name : Clicked(eventName = "GroupName_Name")

    /**
     * 社團簡介_點擊
     */
    object GroupIntroduction : Clicked(eventName = "GroupIntroduction")

    /**
     * 社團簡介_簡介_點擊
     */
    object GroupIntroduction_Introduction : Clicked(eventName = "GroupIntroduction_Introduction")

    /**
     * 社團圖示_點擊
     */
    object GroupIcon : Clicked(eventName = "GroupIcon")

    /**
     * 社團圖示_更換圖片_點擊
     */
    object GroupIcon_ChangePicture : Clicked(eventName = "GroupIcon_ChangePicture")

    /**
     * 首頁背景_點擊
     */
    object HomeBackground : Clicked(eventName = "HomeBackground")

    /**
     * 首頁背景_更換圖片_點擊
     */
    object HomeBackground_ChangePicture : Clicked(eventName = "HomeBackground_ChangePicture")

    /**
     * 主題色彩_點擊
     */
    object ThemeColor : Clicked(eventName = "ThemeColor")

    /**
     * 主題色彩_主題_點擊
     */
    object ThemeColor_Theme : Clicked(eventName = "ThemeColor_Theme")

    /**
     * 主題色彩_主題套用_點擊
     */
    object ThemeColorTheme_Apply : Clicked(eventName = "ThemeColor.Theme_Apply")

    /**
     * 解散社團_點擊
     */
    object DissolveGroup : Clicked(eventName = "DissolveGroup")

    /**
     * 解散社團_返回_點擊
     */
    object DissolveGroup_Return : Clicked(eventName = "DissolveGroup_Return")

    /**
     * 解散社團_確定解散_點擊
     */
    object DissolveGroup_ConfirmDissolution : Clicked(eventName = "DissolveGroup_ConfirmDissolution")

    /**
     * 確定解散_返回_點擊
     */
    object ConfirmDissolution_Return : Clicked(eventName = "ConfirmDissolution_Return")

    /**
     * 確定解散_確定解散_點擊
     */
    object ConfirmDissolution_ConfirmDissolution : Clicked(eventName = "ConfirmDissolution_ConfirmDissolution")

    /**
     * 社團設定_社團公開度_點擊
     */
    object GroupSettings_GroupOpenness : Clicked(eventName = "GroupSettings_GroupOpenness")

    /**
     * 社團權限_公開度_點擊
     */
    object GroupPermissions_Openness : Clicked(eventName = "GroupPermissions_Openness")

    /**
     * 社團公開度_新增審核題目_點擊
     */
    object GroupOpenness_AddReviewQuestion : Clicked(eventName = "GroupOpenness_AddReviewQuestion")

    /**
     * 社團公開度_新增題目_點擊
     */
    object GroupOpenness_AddQuestion : Clicked(eventName = "GroupOpenness_AddQuestion")

    /**
     * 社團公開度_暫時跳過_點擊
     */
    object GroupOpenness_SkipForNow : Clicked(eventName = "GroupOpenness_SkipForNow")

    /**
     * 社團公開度_管理_點擊
     */
    object GroupOpenness_Manage : Clicked(eventName = "GroupOpenness_Manage")

    /**
     * 社團公開度.管理_編輯_點擊
     */
    object GroupOpennessManage_Edit : Clicked(eventName = "GroupOpenness.Manage_Edit")

    /**
     * 社團公開度.管理_移除_點擊
     */
    object GroupOpennessManage_Remove : Clicked(eventName = "GroupOpenness.Manage_Remove")

    /**
     * 社團公開度.管理_返回_點擊
     */
    object GroupOpennessManage_Return : Clicked(eventName = "GroupOpenness.Manage_Return")

    /**
     * 題目_文字區_點擊
     */
    object Question_TextArea : Clicked(eventName = "Question_TextArea")

    /**
     * 題目_確定新增_點擊
     */
    object Question_ConfirmAdd : Clicked(eventName = "Question_ConfirmAdd")

    /**
     * 返回_繼續建立_點擊
     */
    object Return_ContinueCreation : Clicked(eventName = "Return_ContinueCreation")

    /**
     * 返回_確定並返回_點擊
     */
    object Return_ConfirmAndReturn : Clicked(eventName = "Return_ConfirmAndReturn")

    /**
     * 社團設定_頻道管理_點擊
     */
    object GroupSettings_ChannelManagement : Clicked(eventName = "GroupSettings_ChannelManagement")

    /**
     * 頻道管理_新增分類_點擊
     */
    object ChannelManagement_AddCategory : Clicked(eventName = "ChannelManagement_AddCategory")

    /**
     * 頻道管理_編輯分類_點擊
     */
    object ChannelManagement_EditCategory : Clicked(eventName = "ChannelManagement_EditCategory")

    /**
     * 頻道管理_編輯排序_點擊
     */
    object ChannelManagement_EditOrder : Clicked(eventName = "ChannelManagement_EditOrder")

    /**
     * 頻道管理_刪除分類_點擊
     */
    object ChannelManagement_DeleteCategory : Clicked(eventName = "ChannelManagement_DeleteCategory")

    /**
     * 頻道管理_新增頻道_點擊
     */
    object ChannelManagement_AddChannel : Clicked(eventName = "ChannelManagement_AddChannel")

    /**
     * 頻道管理_編輯頻道_點擊
     */
    object ChannelManagement_EditChannel : Clicked(eventName = "ChannelManagement_EditChannel")

    /**
     * 頻道管理_樣式_點擊
     */
    object ChannelManagement_Style : Clicked(eventName = "ChannelManagement_Style")

    /**
     * 樣式_頻道名稱_點擊
     */
    object Style_ChannelName : Clicked(eventName = "Style_ChannelName")

    /**
     * 頻道管理_權限_點擊
     */
    object ChannelManagement_Permissions : Clicked(eventName = "ChannelManagement_Permissions")

    /**
     * 權限_完全公開_點擊
     */
    object Permissions_Public : Clicked(eventName = "Permissions_Public")

    /**
     * 權限_不公開_點擊
     */
    object Permissions_NonPublic : Clicked(eventName = "Permissions_NonPublic")

    /**
     * 不公開_任一權限_點擊
     */
    object NonPublic_AnyPermission : Clicked(eventName = "NonPublic_AnyPermission")

    /**
     * 不公開.任一權限_成員_點擊
     */
    object NonPublicAnyPermission_Members : Clicked(eventName = "NonPublic.AnyPermission_Members")

    /**
     * 不公開.任一權限.成員_新增成員_點擊
     */
    object NonPublicAnyPermissionMembers_AddMember : Clicked(eventName = "NonPublic.AnyPermission.Members_AddMember")

    /**
     * 不公開.任一權限.成員_移除成員_點擊
     */
    object NonPublicAnyPermissionMembers_RemoveMember : Clicked(eventName = "NonPublic.AnyPermission.Members_RemoveMember")

    /**
     * 不公開.任一權限_角色_點擊
     */
    object NonPublicAnyPermission_Roles : Clicked(eventName = "NonPublic.AnyPermission_Roles")

    /**
     * 不公開.任一權限.角色_新增角色_點擊
     */
    object NonPublicAnyPermissionRoles_AddRole : Clicked(eventName = "NonPublic.AnyPermission.Roles_AddRole")

    /**
     * 不公開.任一權限.角色_移除角色_點擊
     */
    object NonPublicAnyPermissionRoles_RemoveRole : Clicked(eventName = "NonPublic.AnyPermission.Roles_RemoveRole")

    /**
     * 不公開.任一權限_方案_點擊
     */
    object NonPublicAnyPermission_Plan : Clicked(eventName = "NonPublic.AnyPermission_Plan")

    /**
     * 不公開.任一權限.方案_新增方案_點擊
     */
    object NonPublicAnyPermissionPlan_AddPlan : Clicked(eventName = "NonPublic.AnyPermission.Plan_AddPlan")

    /**
     * 不公開.任一權限.方案_移除方案_點擊
     */
    object NonPublicAnyPermissionPlan_RemovePlan : Clicked(eventName = "NonPublic.AnyPermission.Plan_RemovePlan")

    /**
     * 頻道管理_管理員_點擊
     */
    object ChannelManagement_Admin : Clicked(eventName = "ChannelManagement_Admin")

    /**
     * 管理員_新增角色_點擊
     */
    object Admin_AddRole : Clicked(eventName = "Admin_AddRole")

    /**
     * 管理員_移除角色_點擊
     */
    object Admin_RemoveRole : Clicked(eventName = "Admin_RemoveRole")

    /**
     * 頻道管理_刪除頻道_點擊
     */
    object ChannelManagement_DeleteChannel : Clicked(eventName = "ChannelManagement_DeleteChannel")

    /**
     * 社團設定_角色管理_點擊
     */
    object GroupSettings_RoleManagement : Clicked(eventName = "GroupSettings_RoleManagement")

    /**
     * 角色管理_新增角色_點擊
     */
    object RoleManagement_AddRole : Clicked(eventName = "RoleManagement_AddRole")

    /**
     * 角色管理_重新排列_點擊
     */
    object RoleManagement_Reshuffle : Clicked(eventName = "RoleManagement_Reshuffle")

    /**
     * 角色管理_編輯角色_點擊
     */
    object RoleManagement_EditRole : Clicked(eventName = "RoleManagement_EditRole")

    /**
     * 角色管理.編輯角色_刪除角色_點擊
     */
    object RoleManagementEditRole_DeleteRole : Clicked(eventName = "RoleManagement.EditRole_DeleteRole")

    /**
     * 角色管理_樣式_點擊
     */
    object RoleManagement_Style : Clicked(eventName = "RoleManagement_Style")

    /**
     * 樣式_角色名稱_點擊
     */
    object Style_RoleName : Clicked(eventName = "Style_RoleName")

    /**
     * 樣式_選取顏色_點擊
     */
    object Style_SelectColor : Clicked(eventName = "Style_SelectColor")

    /**
     * 角色管理_權限_點擊
     */
    object RoleManagement_Permissions : Clicked(eventName = "RoleManagement_Permissions")

    /**
     * 權限_編輯社團_點擊
     */
    object Permissions_EditGroup : Clicked(eventName = "Permissions_EditGroup")

    /**
     * 權限_主頁編輯_點擊
     */
    object Permissions_HomepageEdit : Clicked(eventName = "Permissions_HomepageEdit")

    /**
     * 權限_社團公開度_點擊
     */
    object Permissions_GroupVisibility : Clicked(eventName = "Permissions_GroupVisibility")

    /**
     * 權限_建立與編輯分類_點擊
     */
    object Permissions_CreateAndEditCategory : Clicked(eventName = "Permissions_CreateAndEditCategory")

    /**
     * 權限_刪除分類_點擊
     */
    object Permissions_DeleteCategory : Clicked(eventName = "Permissions_DeleteCategory")

    /**
     * 權限_建立與編輯頻道_點擊
     */
    object Permissions_CreateAndEditChannel : Clicked(eventName = "Permissions_CreateAndEditChannel")

    /**
     * 權限_刪除頻道_點擊
     */
    object Permissions_DeleteChannel : Clicked(eventName = "Permissions_DeleteChannel")

    /**
     * 權限_新增、編輯角色_點擊
     */
    object Permissions_AddEditRole : Clicked(eventName = "Permissions_AddEditRole")

    /**
     * 權限_刪除角色_點擊
     */
    object Permissions_DeleteRole : Clicked(eventName = "Permissions_DeleteRole")

    /**
     * 權限_管理角色層級_點擊
     */
    object Permissions_ManageRoleHierarchy : Clicked(eventName = "Permissions_ManageRoleHierarchy")

    /**
     * 權限_指派成員角色_點擊
     */
    object Permissions_AssignMemberRoles : Clicked(eventName = "Permissions_AssignMemberRoles")

    /**
     * 權限_管理VIP方案_點擊
     */
    object Permissions_ManageVIPPlans : Clicked(eventName = "Permissions_ManageVIPPlans")

    /**
     * 權限_審核入社申請_點擊
     */
    object Permissions_ReviewMembershipApplication : Clicked(eventName = "Permissions_ReviewMembershipApplication")

    /**
     * 權限_禁言與移除成員_點擊
     */
    object Permissions_MuteAndRemoveMembers : Clicked(eventName = "Permissions_MuteAndRemoveMembers")

    /**
     * 角色管理_成員_點擊
     */
    object RoleManagement_Members : Clicked(eventName = "RoleManagement_Members")

    /**
     * 成員_新增成員_點擊
     */
    object Members_AddMember : Clicked(eventName = "Members_AddMember")

    /**
     * 成員_移除_點擊
     */
    object Members_Remove : Clicked(eventName = "Members_Remove")

    /**
     * 社團設定_所有成員_點擊
     */
    object GroupSettings_AllMembers : Clicked(eventName = "GroupSettings_AllMembers")

    /**
     * 所有成員_管理_點擊
     */
    object AllMembers_Manage : Clicked(eventName = "AllMembers_Manage")

    /**
     * 管理成員_移除_點擊
     */
    object ManageMembers_Remove : Clicked(eventName = "ManageMembers_Remove")

    /**
     * 管理成員_新增成員_點擊
     */
    object ManageMembers_AddMember : Clicked(eventName = "ManageMembers_AddMember")

    /**
     * 管理成員_禁言_點擊
     */
    object ManageMembers_Mute : Clicked(eventName = "ManageMembers_Mute")

    /**
     * 管理成員_踢出社團_點擊
     */
    object ManageMembers_KickOut : Clicked(eventName = "ManageMembers_KickOut")

    /**
     * 社團設定_加入申請_點擊
     */
    object GroupSettings_JoinApplication : Clicked(eventName = "GroupSettings_JoinApplication")

    /**
     * 加入申請_批准加入_點擊
     */
    object JoinApplication_ApproveJoin : Clicked(eventName = "JoinApplication_ApproveJoin")

    /**
     * 加入申請_拒絕加入_點擊
     */
    object JoinApplication_RejectJoin : Clicked(eventName = "JoinApplication_RejectJoin")

    /**
     * 加入申請_全選_點擊
     */
    object JoinApplication_SelectAll : Clicked(eventName = "JoinApplication_SelectAll")

    /**
     * 加入申請_取消全選_點擊
     */
    object JoinApplication_UnselectAll : Clicked(eventName = "JoinApplication_UnselectAll")

    /**
     * 社團設定_VIP方案管理_點擊
     */
    object GroupSettings_VIPPlanManagement : Clicked(eventName = "GroupSettings_VIPPlanManagement")

    /**
     * 方案管理_管理_點擊
     */
    object PlanManagement_Manage : Clicked(eventName = "PlanManagement_Manage")

    /**
     * 管理_資訊_點擊
     */
    object Manage_Info : Clicked(eventName = "Manage_Info")

    /**
     * 資訊_名稱_點擊
     */
    object Info_Name : Clicked(eventName = "Info_Name")

    /**
     * 管理_權限_點擊
     */
    object Manage_Permissions : Clicked(eventName = "Manage_Permissions")

    /**
     * 權限_頻道_點擊
     */
    object Permissions_Channel : Clicked(eventName = "Permissions_Channel")

    /**
     * 權限.頻道_任一權限_點擊
     */
    object PermissionsChannel_AnyPermission : Clicked(eventName = "Permissions.Channel_AnyPermission")

    /**
     * 管理_成員_點擊
     */
    object Manage_Members : Clicked(eventName = "Manage_Members")

    /**
     * 社團設定_檢舉審核_點擊
     */
    object GroupSettings_ReportReview : Clicked(eventName = "GroupSettings_ReportReview")

    /**
     * 檢舉審核_懲處_點擊
     */
    object ReportReview_Punish : Clicked(eventName = "ReportReview_Punish")

    /**
     * 檢舉審核_不懲處_點擊
     */
    object ReportReview_NoPunish : Clicked(eventName = "ReportReview_NoPunish")

    /**
     * 懲處_禁言_點擊
     */
    object Punish_Mute : Clicked(eventName = "Punish_Mute")

    /**
     * 懲處.禁言_日期_點擊
     */
    object PunishMute_Date : Clicked(eventName = "Punish.Mute_Date")

    /**
     * 懲處.禁言_返回_點擊
     */
    object PunishMute_Back : Clicked(eventName = "Punish.Mute_Back")

    /**
     * 懲處_踢出社團_點擊
     */
    object Punish_KickOut : Clicked(eventName = "Punish_KickOut")

    /**
     * 懲處.踢出社團_確定踢出_點擊
     */
    object PunishKickOut_ConfirmKickOut : Clicked(eventName = "Punish.KickOut_ConfirmKickOut")

    /**
     * 懲處.踢出社團_取消_點擊
     */
    object PunishKickOut_Cancel : Clicked(eventName = "Punish.KickOut_Cancel")

    /**
     * 懲處_返回_點擊
     */
    object Punish_Back : Clicked(eventName = "Punish_Back")

    /**
     * 社團設定_禁言列表_點擊
     */
    object GroupSettings_MuteList : Clicked(eventName = "GroupSettings_MuteList")

    /**
     * 禁言列表_管理_點擊
     */
    object MuteList_Manage : Clicked(eventName = "MuteList_Manage")

    /**
     * 管理_解除禁言_點擊
     */
    object Manage_Unmute : Clicked(eventName = "Manage_Unmute")

    /**
     * 管理_取消_點擊
     */
    object Manage_Cancel : Clicked(eventName = "Manage_Cancel")

    /**
     * 管理.解除禁言_確認解除_點擊
     */
    object ManageUnmute_ConfirmUnmute : Clicked(eventName = "Manage.Unmute_ConfirmUnmute")

    /**
     * 管理.解除禁言_取消_點擊
     */
    object ManageUnmute_Cancel : Clicked(eventName = "Manage.Unmute_Cancel")

    /**
     * 返回_點擊
     */
    object Return : Clicked(eventName = "Return")

    /**
     * 確認_點擊
     */
    object Confirm : Clicked(eventName = "Confirm")

    /**
     * 搜尋成員_點擊
     */
    object SearchMember : Clicked(eventName = "SearchMember")

}
