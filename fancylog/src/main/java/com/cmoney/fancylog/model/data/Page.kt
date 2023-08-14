package com.cmoney.fancylog.model.data

/**
 * 頁面事件
 * 
 * @property eventName 事件名稱
 */
sealed class Page(val eventName: String) {
    /**
     * 首頁_觀看
     */
    object Home : Page(eventName = "Home")

    /**
     * 會員頁_觀看
     */
    object MemberPage : Page(eventName = "MemberPage")

    /**
     * 會員頁.未登入頁_觀看
     */
    object MemberPageNotLoggedInPage : Page(eventName = "MemberPage.NotLoggedInPage")

    /**
     * 會員頁.信箱登入_觀看
     */
    object MemberPageEmailLogin : Page(eventName = "MemberPage.EmailLogin")

    /**
     * 會員頁.社群帳號登入_觀看
     */
    object MemberPageSocialAccountLogin : Page(eventName = "MemberPage.SocialAccountLogin")

    /**
     * 會員頁.頭像與暱稱_觀看
     */
    object MemberPageAvatarAndNickname : Page(eventName = "MemberPage.AvatarAndNickname")

    /**
     * 頭像與暱稱.頭像_觀看
     */
    object AvatarAndNicknameAvatar : Page(eventName = "AvatarAndNickname.Avatar")

    /**
     * 頭像與暱稱.暱稱_觀看
     */
    object AvatarAndNicknameNickname : Page(eventName = "AvatarAndNickname.Nickname")

    /**
     * 會員頁.帳號管理_觀看
     */
    object MemberPageAccountManagement : Page(eventName = "MemberPage.AccountManagement")

    /**
     * 帳號管理.登出帳號_觀看
     */
    object AccountManagementLogout : Page(eventName = "AccountManagement.Logout")

    /**
     * 帳號管理.刪除帳號_觀看
     */
    object AccountManagementDeleteAccount : Page(eventName = "AccountManagement.DeleteAccount")

    /**
     * 會員頁.服務條款_觀看
     */
    object MemberPageTermsOfService : Page(eventName = "MemberPage.TermsOfService")

    /**
     * 會員頁.隱私權政策_觀看
     */
    object MemberPagePrivacyPolicy : Page(eventName = "MemberPage.PrivacyPolicy")

    /**
     * 會員頁.著作權政策_觀看
     */
    object MemberPageCopyrightPolicy : Page(eventName = "MemberPage.CopyrightPolicy")

    /**
     * 會員頁.意見回饋_觀看
     */
    object MemberPageFeedback : Page(eventName = "MemberPage.Feedback")

    /**
     * 貼文牆
     */
    object PostWall : Page(eventName = "PostWall")

    /**
     * 貼文.內頁_觀看
     */
    object PostInnerPage : Page(eventName = "Post.InnerPage")

    /**
     * 發表貼文_觀看
     */
    object PublishPost : Page(eventName = "PublishPost")

    /**
     * 編輯貼文_觀看
     */
    object EditPost : Page(eventName = "EditPost")

    /**
     * 貼文.圖片_觀看
     */
    object PostImage : Page(eventName = "Post.Image")

    /**
     * 內容搜尋_全部_觀看
     */
    object ContentSearch_All : Page(eventName = "ContentSearch_All")

    /**
     * 內容搜尋_聊天_觀看
     */
    object ContentSearch_Chat : Page(eventName = "ContentSearch_Chat")

    /**
     * 內容搜尋_貼文_觀看
     */
    object ContentSearch_Post : Page(eventName = "ContentSearch_Post")

    /**
     * 探索社團.熱門社團_觀看
     */
    object ExploreGroupPopularGroups : Page(eventName = "ExploreGroup.PopularGroups")

    /**
     * 探索社團.最新社團_觀看
     */
    object ExploreGroupNewestGroups : Page(eventName = "ExploreGroup.NewestGroups")

    /**
     * 社團_觀看
     */
    object Group : Page(eventName = "Group")

    /**
     * 社團.設定.社團設定.社團名稱_觀看
     */
    object GroupSettingsGroupSettingsGroupName : Page(eventName = "Group.Settings.GroupSettings.GroupName")

    /**
     * 社團.設定.社團設定.社團簡介_觀看
     */
    object GroupSettingsGroupSettingsGroupIntroduction : Page(eventName = "Group.Settings.GroupSettings.GroupIntroduction")

    /**
     * 社團.設定.社團設定.社團圖示_觀看
     */
    object GroupSettingsGroupSettingsGroupIcon : Page(eventName = "Group.Settings.GroupSettings.GroupIcon")

    /**
     * 社團.設定.社團設定.首頁背景_觀看
     */
    object GroupSettingsGroupSettingsHomeBackground : Page(eventName = "Group.Settings.GroupSettings.HomeBackground")

    /**
     * 社團.設定.社團設定.主題色彩_觀看
     */
    object GroupSettingsGroupSettingsThemeColor : Page(eventName = "Group.Settings.GroupSettings.ThemeColor")

    /**
     * 社團.設定.社團公開度.公開度_觀看
     */
    object GroupSettingsGroupOpennessOpenness : Page(eventName = "Group.Settings.GroupOpenness.Openness")

    /**
     * 社團.設定.社團公開度.不公開.審核題目.新增審核題目_觀看
     */
    object GroupSettingsGroupOpennessNonPublicReviewQuestionAddReviewQuestion : Page(eventName = "Group.Settings.GroupOpenness.NonPublic.ReviewQuestion.AddReviewQuestion")

    /**
     * 社團.設定.社團公開度.不公開.審核題目.編輯_觀看
     */
    object GroupSettingsGroupOpennessNonPublicReviewQuestionEdit : Page(eventName = "Group.Settings.GroupOpenness.NonPublic.ReviewQuestion.Edit")

    /**
     * 社團.設定.社團公開度.不公開.審核題目.移除_觀看
     */
    object GroupSettingsGroupOpennessNonPublicReviewQuestionRemove : Page(eventName = "Group.Settings.GroupOpenness.NonPublic.ReviewQuestion.Remove")

    /**
     * 社團.設定.頻道管理_觀看
     */
    object GroupSettingsChannelManagement : Page(eventName = "Group.Settings.ChannelManagement")

    /**
     * 社團.設定.頻道管理.新增分類_觀看
     */
    object GroupSettingsChannelManagementAddCategory : Page(eventName = "Group.Settings.ChannelManagement.AddCategory")

    /**
     * 社團.設定.頻道管理.編輯分類_觀看
     */
    object GroupSettingsChannelManagementEditCategory : Page(eventName = "Group.Settings.ChannelManagement.EditCategory")

    /**
     * 社團.設定.頻道管理.刪除分類_觀看
     */
    object GroupSettingsChannelManagementDeleteCategory : Page(eventName = "Group.Settings.ChannelManagement.DeleteCategory")

    /**
     * 社團.設定.頻道管理.新增頻道_觀看
     */
    object GroupSettingsChannelManagementAddChannel : Page(eventName = "Group.Settings.ChannelManagement.AddChannel")

    /**
     * 社團.設定.頻道管理.編輯頻道_觀看
     */
    object GroupSettingsChannelManagementEditChannel : Page(eventName = "Group.Settings.ChannelManagement.EditChannel")

    /**
     * 社團.設定.頻道管理.樣式_觀看
     */
    object GroupSettingsChannelManagementStyle : Page(eventName = "Group.Settings.ChannelManagement.Style")

    /**
     * 社團.設定.頻道管理.樣式.頻道名稱_觀看
     */
    object GroupSettingsChannelManagementStyleChannelName : Page(eventName = "Group.Settings.ChannelManagement.Style.ChannelName")

    /**
     * 社團.設定.頻道管理.權限_觀看
     */
    object GroupSettingsChannelManagementPermissions : Page(eventName = "Group.Settings.ChannelManagement.Permissions")

    /**
     * 社團.設定.頻道管理.權限.公開度_觀看
     */
    object GroupSettingsChannelManagementPermissionsOpenness : Page(eventName = "Group.Settings.ChannelManagement.Permissions.Openness")

    /**
     * 社團.設定.頻道管理.權限.不公開.成員_觀看
     */
    object GroupSettingsChannelManagementPermissionsPrivateMembers : Page(eventName = "Group.Settings.ChannelManagement.Permissions.Private.Members")

    /**
     * 社團.設定.頻道管理.權限.不公開.新增成員_觀看
     */
    object GroupSettingsChannelManagementPermissionsPrivateAddMember : Page(eventName = "Group.Settings.ChannelManagement.Permissions.Private.AddMember")

    /**
     * 社團.設定.頻道管理.權限.不公開.角色_觀看
     */
    object GroupSettingsChannelManagementPermissionsPrivateRoles : Page(eventName = "Group.Settings.ChannelManagement.Permissions.Private.Roles")

    /**
     * 社團.設定.頻道管理.權限.不公開.新增角色_觀看
     */
    object GroupSettingsChannelManagementPermissionsPrivateAddRole : Page(eventName = "Group.Settings.ChannelManagement.Permissions.Private.AddRole")

    /**
     * 社團.設定.頻道管理.權限.不公開.VIP_觀看
     */
    object GroupSettingsChannelManagementPermissionsPrivateVIP : Page(eventName = "Group.Settings.ChannelManagement.Permissions.Private.VIP")

    /**
     * 社團.設定.頻道管理.權限.不公開.新增方案_觀看
     */
    object GroupSettingsChannelManagementPermissionsPrivateAddPlan : Page(eventName = "Group.Settings.ChannelManagement.Permissions.Private.AddPlan")

    /**
     * 社團.設定.頻道管理.管理員_觀看
     */
    object GroupSettingsChannelManagementAdmin : Page(eventName = "Group.Settings.ChannelManagement.Admin")

    /**
     * 社團.設定.頻道管理.管理員.新增角色_觀看
     */
    object GroupSettingsChannelManagementAdminAddRole : Page(eventName = "Group.Settings.ChannelManagement.Admin.AddRole")

    /**
     * 社團.設定.頻道管理.刪除頻道_觀看
     */
    object GroupSettingsChannelManagementDeleteChannel : Page(eventName = "Group.Settings.ChannelManagement.DeleteChannel")

    /**
     * 社團.設定.角色管理.新增角色.樣式_觀看
     */
    object GroupSettingsRoleManagementAddRoleStyle : Page(eventName = "Group.Settings.RoleManagement.AddRole.Style")

    /**
     * 社團.設定.角色管理.新增角色.權限_觀看
     */
    object GroupSettingsRoleManagementAddRolePermissions : Page(eventName = "Group.Settings.RoleManagement.AddRole.Permissions")

    /**
     * 社團.設定.角色管理.新增角色.成員_觀看
     */
    object GroupSettingsRoleManagementAddRoleMembers : Page(eventName = "Group.Settings.RoleManagement.AddRole.Members")

    /**
     * 社團.設定.角色管理.編輯角色.樣式_觀看
     */
    object GroupSettingsRoleManagementEditRoleStyle : Page(eventName = "Group.Settings.RoleManagement.EditRole.Style")

    /**
     * 社團.設定.角色管理.編輯角色.權限_觀看
     */
    object GroupSettingsRoleManagementEditRolePermissions : Page(eventName = "Group.Settings.RoleManagement.EditRole.Permissions")

    /**
     * 社團.設定.角色管理.編輯角色.成員_觀看
     */
    object GroupSettingsRoleManagementEditRoleMembers : Page(eventName = "Group.Settings.RoleManagement.EditRole.Members")

    /**
     * 社團.設定.角色管理.編輯角色.刪除_觀看
     */
    object GroupSettingsRoleManagementEditRoleDelete : Page(eventName = "Group.Settings.RoleManagement.EditRole.Delete")

    /**
     * 社團.設定.所有成員_觀看
     */
    object GroupSettingsAllMembers : Page(eventName = "Group.Settings.AllMembers")

    /**
     * 社團.設定.所有成員.管理_觀看
     */
    object GroupSettingsAllMembersManage : Page(eventName = "Group.Settings.AllMembers.Manage")

    /**
     * 社團.設定.加入申請_觀看
     */
    object GroupSettingsJoinApplication : Page(eventName = "Group.Settings.JoinApplication")

    /**
     * 社團.設定.VIP.方案管理_觀看
     */
    object GroupSettingsVIPPlanMNG : Page(eventName = "Group.Settings.VIP.PlanMNG")

    /**
     * 社團.設定.VIP.資訊_觀看
     */
    object GroupSettingsVIPINF : Page(eventName = "Group.Settings.VIP.INF")

    /**
     * 社團.設定.VIP.權限_觀看
     */
    object GroupSettingsVIPPermission : Page(eventName = "Group.Settings.VIP.Permission")

    /**
     * 社團.設定.VIP.成員_觀看
     */
    object GroupSettingsVIPMembers : Page(eventName = "Group.Settings.VIP.Members")

    /**
     * 社團.設定.檢舉審核_觀看
     */
    object GroupSettingsReportReview : Page(eventName = "Group.Settings.ReportReview")

    /**
     * 社團.設定.檢舉審核.禁言_觀看
     */
    object GroupSettingsReportReviewMute : Page(eventName = "Group.Settings.ReportReview.Mute")

    /**
     * 社團.設定.檢舉審核.踢除_觀看
     */
    object GroupSettingsReportReviewKickOut : Page(eventName = "Group.Settings.ReportReview.KickOut")

    /**
     * 社團.設定.禁言列表_觀看
     */
    object GroupSettingsMuteList : Page(eventName = "Group.Settings.MuteList")

    /**
     * 社團.設定.禁言列表.管理_觀看
     */
    object GroupSettingsMuteListManage : Page(eventName = "Group.Settings.MuteList.Manage")

}
