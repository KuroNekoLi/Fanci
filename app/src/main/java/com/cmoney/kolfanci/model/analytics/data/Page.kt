package com.cmoney.kolfanci.model.analytics.data

/**
 * 頁面
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
    object MemberPage : Page(eventName = "MemberPage") {
        /**
         * 會員頁.未登入頁_觀看
         */
        object NotLoggedInPage : Page(eventName = "MemberPage.NotLoggedInPage")

        /**
         * 會員頁.信箱登入_觀看
         */
        object EmailLogin : Page(eventName = "MemberPage.EmailLogin")

        /**
         * 會員頁.社群帳號登入_觀看
         */
        object SocialAccountLogin : Page(eventName = "MemberPage.SocialAccountLogin")

        /**
         * 會員頁.頭像與暱稱_觀看
         */
        object AvatarAndNickname : Page(eventName = "MemberPage.AvatarAndNickname") {

            /**
             * 頭像與暱稱.頭像_觀看
             */
            object Avatar : Page(eventName = "AvatarAndNickname.Avatar")

            /**
             * 頭像與暱稱.暱稱_觀看
             */
            object Nickname : Page(eventName = "AvatarAndNickname.Nickname")
        }

        /**
         * 會員頁.帳號管理_觀看
         */
        object AccountManagement : Page(eventName = "MemberPage.AccountManagement") {

            /**
             * 帳號管理.登出帳號_觀看
             */
            object Logout : Page(eventName = "AccountManagement.Logout")

            /**
             * 帳號管理.刪除帳號_觀看
             */
            object DeleteAccount : Page(eventName = "AccountManagement.DeleteAccount")
        }

        /**
         * 會員頁.服務條款_觀看
         */
        object TermsOfService : Page(eventName = "MemberPage.TermsOfService")

        /**
         * 會員頁.隱私權政策_觀看
         */
        object PrivacyPolicy : Page(eventName = "MemberPage.PrivacyPolicy")

        /**
         * 會員頁.著作權政策_觀看
         */
        object CopyrightPolicy : Page(eventName = "MemberPage.CopyrightPolicy")

        /**
         * 會員頁.意見回饋_觀看
         */
        object Feedback : Page(eventName = "MemberPage.Feedback")
    }

    /**
     * 探索社團
     */
    object ExploreGroup {

        /**
         * 探索社團.熱門社團_觀看
         */
        object PopularGroups : Page(eventName = "ExploreGroup.PopularGroups")

        /**
         * 探索社團.最新社團_觀看
         */
        object NewestGroups : Page(eventName = "ExploreGroup.NewestGroups")
    }

    /**
     * 社團_觀看
     */
    object Group : Page(eventName = "Group") {

        /**
         * 社團.設定
         */
        object Settings {

            /**
             * 社團.設定.社團設定
             */
            object GroupSettings {

                /**
                 * 社團.設定.社團設定.社團名稱_觀看
                 */
                object GroupName : Page(eventName = "Group.Settings.GroupSettings.GroupName")

                /**
                 * 社團.設定.社團設定.社團簡介_觀看
                 */
                object GroupIntroduction :
                    Page(eventName = "Group.Settings.GroupSettings.GroupIntroduction")

                /**
                 * 社團.設定.社團設定.社團圖示_觀看
                 */
                object GroupIcon : Page(eventName = "Group.Settings.GroupSettings.GroupIcon")

                /**
                 * 社團.設定.社團設定.首頁背景_觀看
                 */
                object HomeBackground :
                    Page(eventName = "Group.Settings.GroupSettings.HomeBackground")

                /**
                 * 社團.設定.社團設定.主題色彩_觀看
                 */
                object ThemeColor : Page(eventName = "Group.Settings.GroupSettings.ThemeColor")
            }

            /**
             * 社團.設定.社團公開度
             */
            object GroupOpenness {

                /**
                 * 社團.設定.社團公開度.完全公開_觀看
                 */
                object FullyOpen : Page(eventName = "Group.Settings.GroupOpenness.FullyOpen")

                /**
                 * 社團.設定.社團公開度.不公開
                 */
                object NonPublic {

                    /**
                     * 社團.設定.社團公開度.不公開.審核題目
                     */
                    object ReviewQuestion {

                        /**
                         * 社團.設定.社團公開度.不公開.審核題目.新增審核題目_觀看
                         */
                        object AddReviewQuestion :
                            Page(eventName = "Group.Settings.GroupOpenness.NonPublic.ReviewQuestion.AddReviewQuestion")

                        /**
                         * 社團.設定.社團公開度.不公開.審核題目.編輯_觀看
                         */
                        object Edit :
                            Page(eventName = "Group.Settings.GroupOpenness.NonPublic.ReviewQuestion.Edit")

                        /**
                         * 社團.設定.社團公開度.不公開.審核題目.移除_觀看
                         */
                        object Remove :
                            Page(eventName = "Group.Settings.GroupOpenness.NonPublic.ReviewQuestion.Remove")
                    }
                }
            }

            /**
             * 社團.設定.頻道管理_觀看
             */
            object ChannelManagement : Page(eventName = "Group.Settings.ChannelManagement") {

                /**
                 * 社團.設定.頻道管理.新增分類_觀看
                 */
                object AddCategory :
                    Page(eventName = "Group.Settings.ChannelManagement.AddCategory")

                /**
                 * 社團.設定.頻道管理.編輯分類_觀看
                 */
                object EditCategory :
                    Page(eventName = "Group.Settings.ChannelManagement.EditCategory")

                /**
                 * 社團.設定.頻道管理.刪除分類_觀看
                 */
                object DeleteCategory :
                    Page(eventName = "Group.Settings.ChannelManagement.DeleteCategory")

                /**
                 * 社團.設定.頻道管理.新增頻道_觀看
                 */
                object AddChannel : Page(eventName = "Group.Settings.ChannelManagement.AddChannel")

                /**
                 * 社團.設定.頻道管理.編輯頻道_觀看
                 */
                object EditChannel :
                    Page(eventName = "Group.Settings.ChannelManagement.EditChannel")

                /**
                 * 社團.設定.頻道管理.樣式_觀看
                 */
                object Style : Page(eventName = "Group.Settings.ChannelManagement.Style") {

                    /**
                     * 社團.設定.頻道管理.樣式.頻道名稱_觀看
                     */
                    object ChannelName :
                        Page(eventName = "Group.Settings.ChannelManagement.Style.ChannelName")
                }

                /**
                 * 社團.設定.頻道管理.權限
                 */
                object Permissions {

                    /**
                     * 社團.設定.頻道管理.權限.完全公開_觀看
                     */
                    object Public :
                        Page(eventName = "Group.Settings.ChannelManagement.Permissions.Public")

                    /**
                     * 社團.設定.頻道管理.權限.不公開
                     */
                    object Private {

                        /**
                         * 社團.設定.頻道管理.權限.不公開.成員_觀看
                         */
                        object Members :
                            Page(eventName = "Group.Settings.ChannelManagement.Permissions.Private.Members")

                        /**
                         * 社團.設定.頻道管理.權限.不公開.新增成員_觀看
                         */
                        object AddMember :
                            Page(eventName = "Group.Settings.ChannelManagement.Permissions.Private.AddMember")

                        /**
                         * 社團.設定.頻道管理.權限.不公開.角色_觀看
                         */
                        object Roles :
                            Page(eventName = "Group.Settings.ChannelManagement.Permissions.Private.Roles")

                        /**
                         * 社團.設定.頻道管理.權限.不公開.新增角色_觀看
                         */
                        object AddRole :
                            Page(eventName = "Group.Settings.ChannelManagement.Permissions.Private.AddRole")

                        /**
                         * 社團.設定.頻道管理.權限.不公開.VIP_觀看
                         */
                        object VIP :
                            Page(eventName = "Group.Settings.ChannelManagement.Permissions.Private.VIP")

                        /**
                         * 社團.設定.頻道管理.權限.不公開.新增方案_觀看
                         */
                        object AddPlan :
                            Page(eventName = "Group.Settings.ChannelManagement.Permissions.Private.AddPlan")
                    }
                }

                /**
                 * 社團.設定.頻道管理.管理員_觀看
                 */
                object Admin : Page(eventName = "Group.Settings.ChannelManagement.Admin") {

                    /**
                     * 社團.設定.頻道管理.管理員.新增角色_觀看
                     */
                    object AddRole :
                        Page(eventName = "Group.Settings.ChannelManagement.Admin.AddRole")
                }

                /**
                 * 社團.設定.頻道管理.刪除頻道_觀看
                 */
                object DeleteChannel :
                    Page(eventName = "Group.Settings.ChannelManagement.DeleteChannel")
            }

            /**
             * 社團.設定.角色管理
             */
            object RoleManagement {

                /**
                 * 社團.設定.角色管理.新增角色
                 */
                object AddRole {

                    /**
                     * 社團.設定.角色管理.新增角色.樣式_觀看
                     */
                    object Style : Page(eventName = "Group.Settings.RoleManagement.AddRole.Style")

                    /**
                     * 社團.設定.角色管理.新增角色.權限_觀看
                     */
                    object Permissions :
                        Page(eventName = "Group.Settings.RoleManagement.AddRole.Permissions")

                    /**
                     * 社團.設定.角色管理.新增角色.成員_觀看
                     */
                    object Members :
                        Page(eventName = "Group.Settings.RoleManagement.AddRole.Members")
                }

                /**
                 * 社團.設定.角色管理.編輯角色
                 */
                object EditRole {

                    /**
                     * 社團.設定.角色管理.編輯角色.樣式_觀看
                     */
                    object Style : Page(eventName = "Group.Settings.RoleManagement.EditRole.Style")

                    /**
                     * 社團.設定.角色管理.編輯角色.權限_觀看
                     */
                    object Permissions :
                        Page(eventName = "Group.Settings.RoleManagement.EditRole.Permissions")

                    /**
                     * 社團.設定.角色管理.編輯角色.成員_觀看
                     */
                    object Members :
                        Page(eventName = "Group.Settings.RoleManagement.EditRole.Members")

                    /**
                     * 社團.設定.角色管理.編輯角色.刪除_觀看
                     */
                    object Delete :
                        Page(eventName = "Group.Settings.RoleManagement.EditRole.Delete")
                }
            }

            /**
             * 社團.設定.所有成員_觀看
             */
            object AllMembers : Page(eventName = "Group.Settings.AllMembers") {

                /**
                 * 社團.設定.所有成員.管理_觀看
                 */
                object Manage : Page(eventName = "Group.Settings.AllMembers.Manage")
            }

            /**
             * 社團.設定.加入申請_觀看
             */
            object JoinApplication : Page(eventName = "Group.Settings.JoinApplication")

            /**
             * 社團.設定.檢舉審核_觀看
             */
            object ReportReview : Page(eventName = "Group.Settings.ReportReview") {

                /**
                 * 社團.設定.檢舉審核.禁言_觀看
                 */
                object Mute : Page(eventName = "Group.Settings.ReportReview.Mute")

                /**
                 * 社團.設定.檢舉審核.踢除_觀看
                 */
                object KickOut : Page(eventName = "Group.Settings.ReportReview.KickOut")
            }

            /**
             * 社團.設定.VIP
             */
            object Vip {

                /**
                 * 社團.設定.VIP.方案管理_觀看
                 */
                object PlanMNG : Page(eventName = "Group.SET.VIP.PlanMNG")

                /**
                 * 社團.設定.VIP.資訊_觀看
                 */
                object INF : Page(eventName = "Group.SET.VIP.INF")

                /**
                 * 社團.設定.VIP.權限_觀看
                 */
                object Permission : Page(eventName = "Group.SET.VIP.Permission")

                /**
                 * 社團.設定.VIP.成員_觀看
                 */
                object Members : Page(eventName = "Group.SET.VIP.Members")
            }
        }
    }
}
