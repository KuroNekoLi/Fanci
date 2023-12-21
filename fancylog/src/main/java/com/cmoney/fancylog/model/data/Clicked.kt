package com.cmoney.fancylog.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 點擊事件
 * 
 * @property eventName 事件名稱
 */
sealed class Clicked(val eventName: String) : Parcelable {
    /**
     * 新手流程.第1頁立即開始_點擊
     */
    @Parcelize
    object OnbordingStart1 : Clicked(eventName = "Onbording.Start1")

    /**
     * 新手流程.第2頁立即開始_點擊
     */
    @Parcelize
    object OnbordingStart2 : Clicked(eventName = "Onbording.Start2")

    /**
     * 首頁.待審核中社團_點擊
     */
    @Parcelize
    object HomeWaitToJoinGroup : Clicked(eventName = "Home_WaitToJoinGroup")

    /**
     * 建立社團_點擊
     */
    @Parcelize
    object CreateGroup : Clicked(eventName = "CreateGroup")

    /**
     * 建立社團_社團名稱_點擊
     */
    @Parcelize
    object CreateGroupName : Clicked(eventName = "CreateGroup_Name")

    /**
     * 建立社團_權限_點擊
     */
    @Parcelize
    object CreateGroupOpenness : Clicked(eventName = "CreateGroup_Openness")

    /**
     * 建立社團_新增審核題目彈窗_點擊
     */
    @Parcelize
    object CreateGroupAddQuestionPopup : Clicked(eventName = "CreateGroup_AddQuestionPopup")

    /**
     * 建立社團_新增審核題目_點擊
     */
    @Parcelize
    object CreateGroupAddReviewQuestion : Clicked(eventName = "CreateGroup_AddReviewQuestion")

    /**
     * 建立社團.新增審核題目_新增題目_點擊
     */
    @Parcelize
    object CreateGroupQuestionKeyin : Clicked(eventName = "CreateGroup_QuestionKeyin")

    /**
     * 建立社團.審核題目_管理題目_點擊
     */
    @Parcelize
    object CreateGroupQuestionManage : Clicked(eventName = "CreateGroup_QuestionManage")

    /**
     * 建立社團.審核題目_編輯題目_點擊
     */
    @Parcelize
    object CreateGroupQuestionEdit : Clicked(eventName = "CreateGroup_QuestionEdit")

    /**
     * 建立社團.審核題目_刪除題目_點擊
     */
    @Parcelize
    object CreateGroupQuestionRemove : Clicked(eventName = "CreateGroup_QuestionRemove")

    /**
     * 建立社團_設定社團圖示_點擊
     */
    @Parcelize
    object CreateGroupGroupIcon : Clicked(eventName = "CreateGroup_GroupIcon")

    /**
     * 建立社團_社團圖示更換圖片_點擊
     */
    @Parcelize
    object CreateGroupChangeGroupIconPicture : Clicked(eventName = "CreateGroup_ChangeGroupIconPicture")

    /**
     * 建立社團_設定首頁背景_點擊
     */
    @Parcelize
    object CreateGroupHomeBackground : Clicked(eventName = "CreateGroup_HomeBackground")

    /**
     * 建立社團_首頁背景更換圖片_點擊
     */
    @Parcelize
    object CreateGroupChangeHomeBackgroundPicture : Clicked(eventName = "CreateGroup_ChangeHomeBackgroundPicture")

    /**
     * 建立社團_設定主題色彩_點擊
     */
    @Parcelize
    object CreateGroupThemeColor : Clicked(eventName = "CreateGroup_ThemeColor")

    /**
     * 建立社團.設定主題色彩_主題_點擊
     */
    @Parcelize
    object CreateGroupThemeColorTheme : Clicked(eventName = "CreateGroup.ThemeColor_Theme")

    /**
     * 建立社團.設定主題色彩_主題套用_點擊
     */
    @Parcelize
    object CreateGroupThemeColorThemeApply : Clicked(eventName = "CreateGroup.ThemeColor.Theme_Apply")

    /**
     * 建立社團流程_下一步/建立社團_點擊
     */
    @Parcelize
    object CreateGroupNextStep : Clicked(eventName = "CreateGroup_NextStep")

    /**
     * 建立社團流程_上一步_點擊
     */
    @Parcelize
    object CreateGroupBackward : Clicked(eventName = "CreateGroup_Backward")

    /**
     * 側邊欄_社團_點擊
     */
    @Parcelize
    object SideBarGroup : Clicked(eventName = "SideBar_Group")

    /**
     * 側邊欄_會員頁_點擊
     */
    @Parcelize
    object SideBarMemberPage : Clicked(eventName = "SideBar_MemberPage")

    /**
     * 側邊欄_通知中心_點擊
     */
    @Parcelize
    object SideBarNotification : Clicked(eventName = "SideBar_Notification")

    /**
     * 會員頁_信箱登入_點擊
     */
    @Parcelize
    object EmailLogin : Clicked(eventName = "EmailLogin")

    /**
     * 會員頁_社群帳號登入_點擊
     */
    @Parcelize
    object SocialAccountLogin : Clicked(eventName = "SocialAccountLogin")

    /**
     * 通知中心_通知_點擊
     */
    @Parcelize
    object NotificationNotification : Clicked(eventName = "Notification_Notification")

    /**
     * 通知中心_無權使用_點擊
     */
    @Parcelize
    object CannotUse : Clicked(eventName = "CannotUse")

    /**
     * 會員頁_首頁_點擊
     */
    @Parcelize
    object MemberPageHome : Clicked(eventName = "MemberPage_Home")

    /**
     * 會員頁_頭像與暱稱_點擊
     */
    @Parcelize
    object MemberPageAvatarAndNickname : Clicked(eventName = "MemberPage_AvatarAndNickname")

    /**
     * 頭像與暱稱_頭像_點擊
     */
    @Parcelize
    object AvatarAndNicknameAvatar : Clicked(eventName = "AvatarAndNickname_Avatar")

    /**
     * 頭像與暱稱_暱稱_點擊
     */
    @Parcelize
    object AvatarAndNicknameNickname : Clicked(eventName = "AvatarAndNickname_Nickname")

    /**
     * 會員頁_帳號管理_點擊
     */
    @Parcelize
    object MemberPageAccountManagement : Clicked(eventName = "MemberPage_AccountManagement")

    /**
     * 帳號管理_登出帳號_點擊
     */
    @Parcelize
    object AccountManagementLogout : Clicked(eventName = "AccountManagement_Logout")

    /**
     * 登出帳號_確定登出_點擊
     */
    @Parcelize
    object LogoutConfirmLogout : Clicked(eventName = "Logout_ConfirmLogout")

    /**
     * 登出帳號_返回_點擊
     */
    @Parcelize
    object LogoutReturn : Clicked(eventName = "Logout_Return")

    /**
     * 帳號管理_刪除帳號_點擊
     */
    @Parcelize
    object AccountManagementDeleteAccount : Clicked(eventName = "AccountManagement_DeleteAccount")

    /**
     * 刪除帳號_確定刪除_點擊
     */
    @Parcelize
    object DeleteAccountConfirmDelete : Clicked(eventName = "DeleteAccount_ConfirmDelete")

    /**
     * 刪除帳號_返回_點擊
     */
    @Parcelize
    object DeleteAccountReturn : Clicked(eventName = "DeleteAccount_Return")

    /**
     * 會員頁_服務條款_點擊
     */
    @Parcelize
    object MemberPageTermsOfService : Clicked(eventName = "MemberPage_TermsOfService")

    /**
     * 會員頁_隱私權政策_點擊
     */
    @Parcelize
    object MemberPagePrivacyPolicy : Clicked(eventName = "MemberPage_PrivacyPolicy")

    /**
     * 會員頁_著作權政策_點擊
     */
    @Parcelize
    object MemberPageCopyrightPolicy : Clicked(eventName = "MemberPage_CopyrightPolicy")

    /**
     * 會員頁_意見回饋_點擊
     */
    @Parcelize
    object MemberPageFeedback : Clicked(eventName = "MemberPage_Feedback")

    /**
     * 貼文_進入內層_點擊
     */
    @Parcelize
    object PostEnterInnerLayer : Clicked(eventName = "Post_EnterInnerLayer")

    /**
     * 貼文_發表貼文_點擊
     */
    @Parcelize
    object PostPublishPost : Clicked(eventName = "Post_PublishPost")

    /**
     * 貼文_編輯貼文_點擊
     */
    @Parcelize
    object PostEditPost : Clicked(eventName = "Post_EditPost")

    /**
     * 貼文_置頂貼文_點擊
     */
    @Parcelize
    object PostPinPost : Clicked(eventName = "Post_PinPost")

    /**
     * 貼文.置頂貼文_確定置頂_點擊
     */
    @Parcelize
    object PostPinPostConfirmPin : Clicked(eventName = "Post.PinPost_ConfirmPin")

    /**
     * 貼文.置頂貼文_取消_點擊
     */
    @Parcelize
    object PostPinPostCancel : Clicked(eventName = "Post.PinPost_Cancel")

    /**
     * 貼文_取消置頂貼文_點擊
     */
    @Parcelize
    object PostUnpinPost : Clicked(eventName = "Post_UnpinPost")

    /**
     * 貼文.取消置頂貼文_確定取消_點擊
     */
    @Parcelize
    object PostUnpinPostConfirmUnpin : Clicked(eventName = "Post.UnpinPost_ConfirmUnpin")

    /**
     * 貼文.取消置頂貼文_返回_點擊
     */
    @Parcelize
    object PostUnpinPostReturn : Clicked(eventName = "Post.UnpinPost_Return")

    /**
     * 貼文_檢舉貼文_點擊
     */
    @Parcelize
    object PostReportPost : Clicked(eventName = "Post_ReportPost")

    /**
     * 貼文_刪除貼文_點擊
     */
    @Parcelize
    object PostDeletePost : Clicked(eventName = "Post_DeletePost")

    /**
     * 貼文.刪除貼文_確定刪除_點擊
     */
    @Parcelize
    object PostDeletePostConfirmDelete : Clicked(eventName = "Post.DeletePost_ConfirmDelete")

    /**
     * 貼文.刪除貼文_取消_點擊
     */
    @Parcelize
    object PostDeletePostCancel : Clicked(eventName = "Post.DeletePost_Cancel")

    /**
     * 留言_回覆_點擊
     */
    @Parcelize
    object CommentReply : Clicked(eventName = "Comment_Reply")

    /**
     * 留言_插入圖片_點擊
     */
    @Parcelize
    object CommentInsertImage : Clicked(eventName = "Comment_InsertImage")

    /**
     * 留言_傳送鍵_點擊
     */
    @Parcelize
    object CommentSendButton : Clicked(eventName = "Comment_SendButton")

    /**
     * 留言_編輯留言_點擊
     */
    @Parcelize
    object CommentEditComment : Clicked(eventName = "Comment_EditComment")

    /**
     * 留言_檢舉留言_點擊
     */
    @Parcelize
    object CommentReportComment : Clicked(eventName = "Comment_ReportComment")

    /**
     * 留言_刪除留言_點擊
     */
    @Parcelize
    object CommentDeleteComment : Clicked(eventName = "Comment_DeleteComment")

    /**
     * 留言_編輯回覆_點擊
     */
    @Parcelize
    object CommentEditReply : Clicked(eventName = "Comment_EditReply")

    /**
     * 留言_檢舉回覆_點擊
     */
    @Parcelize
    object CommentReportReply : Clicked(eventName = "Comment_ReportReply")

    /**
     * 留言_刪除回覆_點擊
     */
    @Parcelize
    object CommentDeleteReply : Clicked(eventName = "Comment_DeleteReply")

    /**
     * 貼文_選取照片_點擊
     */
    @Parcelize
    object PostSelectPhoto : Clicked(eventName = "Post_SelectPhoto")

    /**
     * 貼文_發布_點擊
     */
    @Parcelize
    object PostPublish : Clicked(eventName = "Post_Publish")

    /**
     * 貼文_捨棄_點擊
     */
    @Parcelize
    object PostDiscard : Clicked(eventName = "Post_Discard")

    /**
     * 貼文_繼續編輯_點擊
     */
    @Parcelize
    object PostContinueEditing : Clicked(eventName = "Post_ContinueEditing")

    /**
     * 貼文.空白貼文_修改_點擊
     */
    @Parcelize
    object PostBlankPostEdit : Clicked(eventName = "Post.BlankPost_Edit")

    /**
     * 訊息_插入圖片_點擊
     */
    @Parcelize
    object MessageInsertImage : Clicked(eventName = "Message_InsertImage")

    /**
     * 訊息_傳送鍵_點擊
     */
    @Parcelize
    object MessageSendButton : Clicked(eventName = "Message_SendButton")

    /**
     * 訊息_常壓訊息_點擊
     */
    @Parcelize
    object MessageLongPressMessage : Clicked(eventName = "Message_LongPressMessage")

    /**
     * 訊息.常壓訊息_表情符號_點擊
     */
    @Parcelize
    object MessageLongPressMessageEmoji : Clicked(eventName = "Message.LongPressMessage_Emoji")

    /**
     * 訊息.常壓訊息_回覆_點擊
     */
    @Parcelize
    object MessageLongPressMessageReply : Clicked(eventName = "Message.LongPressMessage_Reply")

    /**
     * 訊息.常壓訊息_複製訊息_點擊
     */
    @Parcelize
    object MessageLongPressMessageCopyMessage : Clicked(eventName = "Message.LongPressMessage_CopyMessage")

    /**
     * 訊息.常壓訊息_置頂訊息_點擊
     */
    @Parcelize
    object MessageLongPressMessagePinMessage : Clicked(eventName = "Message.LongPressMessage_PinMessage")

    /**
     * 訊息.常壓訊息_檢舉_點擊
     */
    @Parcelize
    object MessageLongPressMessageReport : Clicked(eventName = "Message.LongPressMessage_Report")

    /**
     * 訊息.常壓訊息_刪除訊息_點擊
     */
    @Parcelize
    object MessageLongPressMessageDeleteMessage : Clicked(eventName = "Message.LongPressMessage_DeleteMessage")

    /**
     * 訊息.常壓訊息_收回訊息_點擊
     */
    @Parcelize
    object MessageLongPressMessageUnsendMessage : Clicked(eventName = "Message.LongPressMessage_UnsendMessage")

    /**
     * 檢舉_確定檢舉_點擊
     */
    @Parcelize
    object ReportConfirmReport : Clicked(eventName = "Report_ConfirmReport")

    /**
     * 檢舉_取消_點擊
     */
    @Parcelize
    object ReportCancel : Clicked(eventName = "Report_Cancel")

    /**
     * 檢舉原因_原因_點擊
     */
    @Parcelize
    object ReportReasonReason : Clicked(eventName = "ReportReason_Reason")

    /**
     * 檢舉原因_取消檢舉_點擊
     */
    @Parcelize
    object ReportReasonCancelReport : Clicked(eventName = "ReportReason_CancelReport")

    /**
     * 刪除訊息_確定刪除_點擊
     */
    @Parcelize
    object DeleteMessageConfirmDelete : Clicked(eventName = "DeleteMessage_ConfirmDelete")

    /**
     * 刪除訊息_取消_點擊
     */
    @Parcelize
    object DeleteMessageCancel : Clicked(eventName = "DeleteMessage_Cancel")

    /**
     * 訊息_重試_點擊
     */
    @Parcelize
    object MessageRetry : Clicked(eventName = "Message_Retry")

    /**
     * 顯示更多_點擊
     */
    @Parcelize
    object ShowMore : Clicked(eventName = "ShowMore")

    /**
     * 更多功能(點點點)_點擊
     */
    @Parcelize
    object MoreAction : Clicked(eventName = "MoreAction")

    /**
     * 圖片_點擊
     */
    @Parcelize
    object Image : Clicked(eventName = "Image")

    /**
     * 表情符號_現有的表情_點擊
     */
    @Parcelize
    object ExistingEmoji : Clicked(eventName = "ExistingEmoji")

    /**
     * 表情符號_新增表情_點擊
     */
    @Parcelize
    object AddEmoji : Clicked(eventName = "AddEmoji")

    /**
     * 內容搜尋_點擊
     */
    @Parcelize
    object ContentSearch : Clicked(eventName = "ContentSearch")

    /**
     * 內容搜尋_搜尋_點擊
     */
    @Parcelize
    object ContentSearchSearch : Clicked(eventName = "ContentSearch_Search")

    /**
     * 內容搜尋_全部_點擊
     */
    @Parcelize
    object ContentSearchAll : Clicked(eventName = "ContentSearch_All")

    /**
     * 內容搜尋_貼文_點擊
     */
    @Parcelize
    object ContentSearchPost : Clicked(eventName = "ContentSearch_Post")

    /**
     * 內容搜尋_聊天_點擊
     */
    @Parcelize
    object ContentSearchChat : Clicked(eventName = "ContentSearch_Chat")

    /**
     * 內容搜尋_查看_點擊
     */
    @Parcelize
    object ContentSearchView : Clicked(eventName = "ContentSearch_View")

    /**
     * 探索社團_熱門社團_點擊
     */
    @Parcelize
    object ExploreGroupPopularGroups : Clicked(eventName = "ExploreGroup_PopularGroups")

    /**
     * 探索社團_最新社團_點擊
     */
    @Parcelize
    object ExploreGroupNewestGroups : Clicked(eventName = "ExploreGroup_NewestGroups")

    /**
     * 社團_加入_點擊
     */
    @Parcelize
    object GroupJoin : Clicked(eventName = "Group_Join")

    /**
     * 社團_申請加入_點擊
     */
    @Parcelize
    object GroupApplyToJoin : Clicked(eventName = "Group_ApplyToJoin")

    /**
     * 社團_進入社團_點擊
     */
    @Parcelize
    object GroupEnter : Clicked(eventName = "Group_enter")

    /**
     * 社團已被解散彈窗_點擊
     */
    @Parcelize
    object AlreadyDissolve : Clicked(eventName = "AlreadyDissolve")

    /**
     * 社團_社團設定_點擊
     */
    @Parcelize
    object GroupGroupSettings : Clicked(eventName = "Group_GroupSettings")

    /**
     * 社團設定_邀請社團成員_點擊
     */
    @Parcelize
    object GroupSettingsInviteGroupMember : Clicked(eventName = "GroupSettings_InviteGroupMember")

    /**
     * 社團設定_退出社團_點擊
     */
    @Parcelize
    object GroupSettingsLeaveGroup : Clicked(eventName = "GroupSettings_LeaveGroup")

    /**
     * 退出社團_確定退出_點擊
     */
    @Parcelize
    object LeaveGroupConfirmLeave : Clicked(eventName = "LeaveGroup_ConfirmLeave")

    /**
     * 退出社團_取消退出_點擊
     */
    @Parcelize
    object LeaveGroupCancelLeave : Clicked(eventName = "LeaveGroup_CancelLeave")

    /**
     * 社團設定_社團設定_點擊
     */
    @Parcelize
    object GroupSettingsGroupSettingsPage : Clicked(eventName = "GroupSettings_GroupSettingsPage")

    /**
     * 社團名稱_點擊
     */
    @Parcelize
    object GroupName : Clicked(eventName = "GroupName")

    /**
     * 社團名稱_名稱_點擊
     */
    @Parcelize
    object GroupNameName : Clicked(eventName = "GroupName_Name")

    /**
     * 社團簡介_點擊
     */
    @Parcelize
    object GroupIntroduction : Clicked(eventName = "GroupIntroduction")

    /**
     * 社團簡介_簡介_點擊
     */
    @Parcelize
    object GroupIntroductionIntroduction : Clicked(eventName = "GroupIntroduction_Introduction")

    /**
     * 社團圖示_點擊
     */
    @Parcelize
    object GroupIcon : Clicked(eventName = "GroupIcon")
    /**
     * 社團Logo_點擊
     */
    @Parcelize
    object GroupLogo : Clicked(eventName = "GroupLogo")

    /**
     * 社團圖示_更換圖片_點擊
     */
    @Parcelize
    object GroupIconChangePicture : Clicked(eventName = "GroupIcon_ChangePicture")

    /**
     * 首頁背景_點擊
     */
    @Parcelize
    object HomeBackground : Clicked(eventName = "HomeBackground")

    /**
     * 首頁背景_更換圖片_點擊
     */
    @Parcelize
    object HomeBackgroundChangePicture : Clicked(eventName = "HomeBackground_ChangePicture")

    /**
     * 主題色彩_點擊
     */
    @Parcelize
    object ThemeColor : Clicked(eventName = "ThemeColor")

    /**
     * 主題色彩_主題_點擊
     */
    @Parcelize
    object ThemeColorTheme : Clicked(eventName = "ThemeColor_Theme")

    /**
     * 主題色彩_主題套用_點擊
     */
    @Parcelize
    object ThemeColorThemeApply : Clicked(eventName = "ThemeColor.Theme_Apply")

    /**
     * 解散社團_點擊
     */
    @Parcelize
    object DissolveGroup : Clicked(eventName = "DissolveGroup")

    /**
     * 解散社團_返回_點擊
     */
    @Parcelize
    object DissolveGroupReturn : Clicked(eventName = "DissolveGroup_Return")

    /**
     * 解散社團_確定解散_點擊
     */
    @Parcelize
    object DissolveGroupConfirmDissolution : Clicked(eventName = "DissolveGroup_ConfirmDissolution")

    /**
     * 確定解散_返回_點擊
     */
    @Parcelize
    object ConfirmDissolutionReturn : Clicked(eventName = "ConfirmDissolution_Return")

    /**
     * 確定解散_確定解散_點擊
     */
    @Parcelize
    object ConfirmDissolutionConfirmDissolution : Clicked(eventName = "ConfirmDissolution_ConfirmDissolution")

    /**
     * 社團設定_社團公開度_點擊
     */
    @Parcelize
    object GroupSettingsGroupOpenness : Clicked(eventName = "GroupSettings_GroupOpenness")

    /**
     * 社團權限_公開度_點擊
     */
    @Parcelize
    object GroupPermissionsOpenness : Clicked(eventName = "GroupPermissions_Openness")

    /**
     * 社團公開度_新增審核題目_點擊
     */
    @Parcelize
    object GroupOpennessAddReviewQuestion : Clicked(eventName = "GroupOpenness_AddReviewQuestion")

    /**
     * 社團公開度_新增題目_點擊
     */
    @Parcelize
    object GroupOpennessAddQuestion : Clicked(eventName = "GroupOpenness_AddQuestion")

    /**
     * 社團公開度_暫時跳過_點擊
     */
    @Parcelize
    object GroupOpennessSkipForNow : Clicked(eventName = "GroupOpenness_SkipForNow")

    /**
     * 社團公開度_管理_點擊
     */
    @Parcelize
    object GroupOpennessManage : Clicked(eventName = "GroupOpenness_Manage")

    /**
     * 社團公開度.管理_編輯_點擊
     */
    @Parcelize
    object GroupOpennessManageEdit : Clicked(eventName = "GroupOpenness.Manage_Edit")

    /**
     * 社團公開度.管理_移除_點擊
     */
    @Parcelize
    object GroupOpennessManageRemove : Clicked(eventName = "GroupOpenness.Manage_Remove")

    /**
     * 題目_文字區_點擊
     */
    @Parcelize
    object QuestionTextArea : Clicked(eventName = "Question_TextArea")

    /**
     * 社團設定_頻道管理_點擊
     */
    @Parcelize
    object GroupSettingsChannelManagement : Clicked(eventName = "GroupSettings_ChannelManagement")

    /**
     * 頻道管理_新增分類_點擊
     */
    @Parcelize
    object ChannelManagementAddCategory : Clicked(eventName = "ChannelManagement_AddCategory")

    /**
     * 頻道管理_編輯分類_點擊
     */
    @Parcelize
    object ChannelManagementEditCategory : Clicked(eventName = "ChannelManagement_EditCategory")

    /**
     * 分類名稱_點擊
     */
    @Parcelize
    object CategoryName : Clicked(eventName = "CategoryName")

    /**
     * 分類名稱內層輸入框_點擊
     */
    @Parcelize
    object CategoryNameNameKeyIn : Clicked(eventName = "CategoryName_NameKeyIn")

    /**
     * 頻道管理_編輯排序_點擊
     */
    @Parcelize
    object ChannelManagementEditOrder : Clicked(eventName = "ChannelManagement_EditOrder")

    /**
     * 頻道管理_編輯排序選項_點擊
     */
    @Parcelize
    object ChannelManagementOrderOption : Clicked(eventName = "ChannelManagement_OrderOption")

    /**
     * 頻道管理_刪除分類_點擊
     */
    @Parcelize
    object ChannelManagementDeleteCategory : Clicked(eventName = "ChannelManagement_DeleteCategory")

    /**
     * 頻道管理_新增頻道_點擊
     */
    @Parcelize
    object ChannelManagementAddChannel : Clicked(eventName = "ChannelManagement_AddChannel")

    /**
     * 頻道管理_編輯頻道_點擊
     */
    @Parcelize
    object ChannelManagementEditChannel : Clicked(eventName = "ChannelManagement_EditChannel")

    /**
     * 頻道管理_樣式_點擊
     */
    @Parcelize
    object ChannelManagementStyle : Clicked(eventName = "ChannelManagement_Style")

    /**
     * 樣式_頻道名稱_點擊
     */
    @Parcelize
    object StyleChannelName : Clicked(eventName = "Style_ChannelName")

    /**
     * 樣式_頻道名稱內層輸入框_點擊
     */
    @Parcelize
    object StyleChannelNameKeyIn : Clicked(eventName = "Style_ChannelNameKeyIn")

    /**
     * 樣式_頻道版面_點擊
     */
    @Parcelize
    object StyleLayout : Clicked(eventName = "Style_Layout")

    /**
     * 樣式_頻道版面聊天區優先_點擊
     */
    @Parcelize
    object StyleLayoutChatFirst : Clicked(eventName = "Style_LayoutChatFirst")

    /**
     * 樣式_頻道版面貼文區優先_點擊
     */
    @Parcelize
    object StyleLayoutPostFirst : Clicked(eventName = "Style_LayoutPostFirst")

    /**
     * 頻道管理_權限_點擊
     */
    @Parcelize
    object ChannelManagementPermissions : Clicked(eventName = "ChannelManagement_Permissions")

    /**
     * 權限_完全公開_點擊
     */
    @Parcelize
    object PermissionsPublic : Clicked(eventName = "Permissions_Public")

    /**
     * 權限_不公開_點擊
     */
    @Parcelize
    object PermissionsNonPublic : Clicked(eventName = "Permissions_NonPublic")

    /**
     * 不公開_任一權限_點擊
     */
    @Parcelize
    object NonPublicAnyPermission : Clicked(eventName = "NonPublic_AnyPermission")

    /**
     * 不公開.任一權限_成員_點擊
     */
    @Parcelize
    object NonPublicAnyPermissionMembers : Clicked(eventName = "NonPublic.AnyPermission_Members")

    /**
     * 不公開.任一權限.成員_新增成員_點擊
     */
    @Parcelize
    object NonPublicAnyPermissionMembersAddMember : Clicked(eventName = "NonPublic.AnyPermission.Members_AddMember")

    /**
     * 不公開.任一權限.成員_移除成員_點擊
     */
    @Parcelize
    object NonPublicAnyPermissionMembersRemoveMember : Clicked(eventName = "NonPublic.AnyPermission.Members_RemoveMember")

    /**
     * 不公開.任一權限_角色_點擊
     */
    @Parcelize
    object NonPublicAnyPermissionRoles : Clicked(eventName = "NonPublic.AnyPermission_Roles")

    /**
     * 不公開.任一權限.角色_新增角色_點擊
     */
    @Parcelize
    object NonPublicAnyPermissionRolesAddRole : Clicked(eventName = "NonPublic.AnyPermission.Roles_AddRole")

    /**
     * 不公開.任一權限.角色_移除角色_點擊
     */
    @Parcelize
    object NonPublicAnyPermissionRolesRemoveRole : Clicked(eventName = "NonPublic.AnyPermission.Roles_RemoveRole")

    /**
     * 不公開.任一權限_方案_點擊
     */
    @Parcelize
    object NonPublicAnyPermissionPlan : Clicked(eventName = "NonPublic.AnyPermission_Plan")

    /**
     * 不公開.任一權限.方案_新增方案_點擊
     */
    @Parcelize
    object NonPublicAnyPermissionPlanAddPlan : Clicked(eventName = "NonPublic.AnyPermission.Plan_AddPlan")

    /**
     * 不公開.任一權限.方案_移除方案_點擊
     */
    @Parcelize
    object NonPublicAnyPermissionPlanRemovePlan : Clicked(eventName = "NonPublic.AnyPermission.Plan_RemovePlan")

    /**
     * 頻道管理_管理員_點擊
     */
    @Parcelize
    object ChannelManagementAdmin : Clicked(eventName = "ChannelManagement_Admin")

    /**
     * 管理員_新增角色_點擊
     */
    @Parcelize
    object AdminAddRole : Clicked(eventName = "Admin_AddRole")

    /**
     * 管理員_移除角色_點擊
     */
    @Parcelize
    object AdminRemoveRole : Clicked(eventName = "Admin_RemoveRole")

    /**
     * 頻道管理_刪除頻道_點擊
     */
    @Parcelize
    object ChannelManagementDeleteChannel : Clicked(eventName = "ChannelManagement_DeleteChannel")

    /**
     * 社團設定_提醒設定_點擊
     */
    @Parcelize
    object GroupSettingsNotificationSetting : Clicked(eventName = "GroupSettings_NotificationSetting")

    /**
     * 提醒設定_任何新動態_點擊
     */
    @Parcelize
    object NotificationSettingAnyNews : Clicked(eventName = "NotificationSetting_AnyNews")

    /**
     * 提醒設定_只有新貼文_點擊
     */
    @Parcelize
    object NotificationSettingOnlyNewPost : Clicked(eventName = "NotificationSetting_OnlyNewPost")

    /**
     * 提醒設定_完全靜音_點擊
     */
    @Parcelize
    object NotificationSettingMute : Clicked(eventName = "NotificationSetting_Mute")

    /**
     * 提醒設定_前往系統設定_點擊
     */
    @Parcelize
    object NotificationSettingGoSystem : Clicked(eventName = "NotificationSetting_GoSystem")

    /**
     * 社團設定_角色管理_點擊
     */
    @Parcelize
    object GroupSettingsRoleManagement : Clicked(eventName = "GroupSettings_RoleManagement")

    /**
     * 角色管理_新增角色_點擊
     */
    @Parcelize
    object RoleManagementAddRole : Clicked(eventName = "RoleManagement_AddRole")

    /**
     * 角色管理_重新排列_點擊
     */
    @Parcelize
    object RoleManagementReshuffle : Clicked(eventName = "RoleManagement_Reshuffle")

    /**
     * 角色管理_編輯角色_點擊
     */
    @Parcelize
    object RoleManagementEditRole : Clicked(eventName = "RoleManagement_EditRole")

    /**
     * 角色管理.編輯角色_刪除角色_點擊
     */
    @Parcelize
    object RoleManagementEditRoleDeleteRole : Clicked(eventName = "RoleManagement.EditRole_DeleteRole")

    /**
     * 角色管理_樣式_點擊
     */
    @Parcelize
    object RoleManagementStyle : Clicked(eventName = "RoleManagement_Style")

    /**
     * 樣式_角色名稱_點擊
     */
    @Parcelize
    object StyleRoleName : Clicked(eventName = "Style_RoleName")

    /**
     * 樣式_角色名稱內層輸入框_點擊
     */
    @Parcelize
    object StyleRoleNameKeyIn : Clicked(eventName = "Style_RoleNameKeyIn")

    /**
     * 樣式_選取顏色_點擊
     */
    @Parcelize
    object StyleSelectColor : Clicked(eventName = "Style_SelectColor")

    /**
     * 角色管理_權限_點擊
     */
    @Parcelize
    object RoleManagementPermissions : Clicked(eventName = "RoleManagement_Permissions")

    /**
     * 角色管理_成員_點擊
     */
    @Parcelize
    object RoleManagementMembers : Clicked(eventName = "RoleManagement_Members")

    /**
     * 成員_新增成員_點擊
     */
    @Parcelize
    object MembersAddMember : Clicked(eventName = "Members_AddMember")

    /**
     * 成員_移除_點擊
     */
    @Parcelize
    object MembersRemove : Clicked(eventName = "Members_Remove")

    /**
     * 社團設定_所有成員_點擊
     */
    @Parcelize
    object GroupSettingsAllMembers : Clicked(eventName = "GroupSettings_AllMembers")

    /**
     * 所有成員_管理_點擊
     */
    @Parcelize
    object AllMembersManage : Clicked(eventName = "AllMembers_Manage")

    /**
     * 管理成員_移除_點擊
     */
    @Parcelize
    object ManageMembersRemove : Clicked(eventName = "ManageMembers_Remove")

    /**
     * 管理成員_新增成員_點擊
     */
    @Parcelize
    object ManageMembersAddMember : Clicked(eventName = "ManageMembers_AddMember")

    /**
     * 管理成員_禁言_點擊
     */
    @Parcelize
    object ManageMembersMute : Clicked(eventName = "ManageMembers_Mute")

    /**
     * 管理成員_踢出社團_點擊
     */
    @Parcelize
    object ManageMembersKickOut : Clicked(eventName = "ManageMembers_KickOut")

    /**
     * 社團設定_加入申請_點擊
     */
    @Parcelize
    object GroupSettingsJoinApplication : Clicked(eventName = "GroupSettings_JoinApplication")

    /**
     * 加入申請_批准加入_點擊
     */
    @Parcelize
    object JoinApplicationApproveJoin : Clicked(eventName = "JoinApplication_ApproveJoin")

    /**
     * 加入申請_拒絕加入_點擊
     */
    @Parcelize
    object JoinApplicationRejectJoin : Clicked(eventName = "JoinApplication_RejectJoin")

    /**
     * 加入申請_全選_點擊
     */
    @Parcelize
    object JoinApplicationSelectAll : Clicked(eventName = "JoinApplication_SelectAll")

    /**
     * 加入申請_取消全選_點擊
     */
    @Parcelize
    object JoinApplicationUnselectAll : Clicked(eventName = "JoinApplication_UnselectAll")

    /**
     * 社團設定_VIP方案管理_點擊
     */
    @Parcelize
    object GroupSettingsVIPPlanManagement : Clicked(eventName = "GroupSettings_VIPPlanManagement")

    /**
     * 方案管理_管理_點擊
     */
    @Parcelize
    object PlanManagementManage : Clicked(eventName = "PlanManagement_Manage")

    /**
     * 管理_資訊_點擊
     */
    @Parcelize
    object ManageInfo : Clicked(eventName = "Manage_Info")

    /**
     * 資訊_名稱_點擊
     */
    @Parcelize
    object InfoName : Clicked(eventName = "Info_Name")

    /**
     * 資訊_名稱內層輸入框_點擊
     */
    @Parcelize
    object InfoNameKeyIn : Clicked(eventName = "Info_NameKeyIn")

    /**
     * 管理_權限_點擊
     */
    @Parcelize
    object ManagePermissions : Clicked(eventName = "Manage_Permissions")

    /**
     * 權限_頻道_點擊
     */
    @Parcelize
    object PermissionsChannel : Clicked(eventName = "Permissions_Channel")

    /**
     * 權限.頻道_任一權限_點擊
     */
    @Parcelize
    object PermissionsChannelAnyPermission : Clicked(eventName = "Permissions.Channel_AnyPermission")

    /**
     * 管理_成員_點擊
     */
    @Parcelize
    object ManageMembers : Clicked(eventName = "Manage_Members")

    /**
     * 社團設定_檢舉審核_點擊
     */
    @Parcelize
    object GroupSettingsReportReview : Clicked(eventName = "GroupSettings_ReportReview")

    /**
     * 檢舉審核_懲處_點擊
     */
    @Parcelize
    object ReportReviewPunish : Clicked(eventName = "ReportReview_Punish")

    /**
     * 檢舉審核_不懲處_點擊
     */
    @Parcelize
    object ReportReviewNoPunish : Clicked(eventName = "ReportReview_NoPunish")

    /**
     * 懲處_禁言_點擊
     */
    @Parcelize
    object PunishMute : Clicked(eventName = "Punish_Mute")

    /**
     * 懲處.禁言_日期_點擊
     */
    @Parcelize
    object PunishMuteDate : Clicked(eventName = "Punish.Mute_Date")

    /**
     * 懲處.禁言_返回_點擊
     */
    @Parcelize
    object PunishMuteBack : Clicked(eventName = "Punish.Mute_Back")

    /**
     * 懲處_踢出社團_點擊
     */
    @Parcelize
    object PunishKickOut : Clicked(eventName = "Punish_KickOut")

    /**
     * 懲處.踢出社團_確定踢出_點擊
     */
    @Parcelize
    object PunishKickOutConfirmKickOut : Clicked(eventName = "Punish.KickOut_ConfirmKickOut")

    /**
     * 懲處.踢出社團_取消_點擊
     */
    @Parcelize
    object PunishKickOutCancel : Clicked(eventName = "Punish.KickOut_Cancel")

    /**
     * 懲處_返回_點擊
     */
    @Parcelize
    object PunishBack : Clicked(eventName = "Punish_Back")

    /**
     * 社團設定_禁言列表_點擊
     */
    @Parcelize
    object GroupSettingsMuteList : Clicked(eventName = "GroupSettings_MuteList")

    /**
     * 禁言列表_管理_點擊
     */
    @Parcelize
    object MuteListManage : Clicked(eventName = "MuteList_Manage")

    /**
     * 管理_解除禁言_點擊
     */
    @Parcelize
    object ManageUnmute : Clicked(eventName = "Manage_Unmute")

    /**
     * 管理_取消_點擊
     */
    @Parcelize
    object ManageCancel : Clicked(eventName = "Manage_Cancel")

    /**
     * 管理.解除禁言_確認解除_點擊
     */
    @Parcelize
    object ManageUnmuteConfirmUnmute : Clicked(eventName = "Manage.Unmute_ConfirmUnmute")

    /**
     * 管理.解除禁言_取消_點擊
     */
    @Parcelize
    object ManageUnmuteCancel : Clicked(eventName = "Manage.Unmute_Cancel")

    /**
     * 返回_點擊
     */
    @Parcelize
    object Return : Clicked(eventName = "Return")

    /**
     * 確認_點擊
     */
    @Parcelize
    object Confirm : Clicked(eventName = "Confirm")

    /**
     * 搜尋成員_點擊
     */
    @Parcelize
    object SearchMember : Clicked(eventName = "SearchMember")

    /**
     * 建立社團_設定社團Logo_點擊
     */
    @Parcelize
    object CreateGroupLogo : Clicked(eventName = "CreateGroupLogo")
}
