package com.cmoney.kolfanci.model

import android.content.Context
import com.cmoney.fanciapi.fanci.model.ChannelPermission
import com.cmoney.fanciapi.fanci.model.GroupPermission
import com.cmoney.fanciapi.fanci.model.User
import com.cmoney.fanciapi.fanci.model.UserBuffInformation
import com.cmoney.kolfanci.BuildConfig
import com.cmoney.kolfanci.R
import com.cmoney.remoteconfig_library.IRemoteConfig
import com.cmoney.remoteconfig_library.model.config.AppStatus
import org.koin.core.context.GlobalContext

object Constant {

    //是否開啟 Mock 模式 (ex: server 壞掉下還可以使用)
    val isOpenMock = (true && BuildConfig.DEBUG)

    //我的個人資訊
    var MyInfo: User? = null

    //我在目前社團的權限
    var MyGroupPermission: GroupPermission = GroupPermission()

    //TODO: test
//    var MyGroupPermission: GroupPermission = GroupPermission(
//        editGroup = true,
//        createOrEditApplyQuestions = true,
//        rearrangeChannelCategory = true,
//        setGroupPublicity = true,
//        createOrEditCategory = true,
//        deleteCategory = true,
//        createOrEditChannel = true,
//        setAnnouncement = true,
//        deleteChannel = true,
//        createOrEditRole = true,
//        deleteRole = true,
//        rearrangeRoles = true,
//        assignRole = true,
//        approveJoinApplies = true,
//        deleteMemberMessage = true,
//        banOrKickMember = true
//    )

    //我在目前頻道的權限
//    var MyChannelPermission: ChannelPermission = ChannelPermission()
    //todo TEST
    var MyChannelPermission: ChannelPermission = ChannelPermission(
        canRead = true,
        canPost = true,
        canReply = true,
        canEmoji = true,
        canManage = true,
        canCopy = true,
        canBlock = true,
        canReport = true,
        canTakeback = true
    )

    //我在目前頻道的Buffer
    var MyChannelBuff: UserBuffInformation = UserBuffInformation()

    /**
     * 目前 app 是否不是審核中
     *
     */
    fun isAppNotInReview(): Boolean {
        val iRemoteConfig = GlobalContext.get().get<IRemoteConfig>()
        return when (iRemoteConfig.getAppStatus()) {
            is AppStatus.IsUnderReview -> false
            else -> true
        }
    }

    /**
     * 是否出現 上傳檔案功能
     */
    fun isShowUploadFile() : Boolean {
        val iRemoteConfig = GlobalContext.get().get<IRemoteConfig>()
        return when (iRemoteConfig.getAppStatus()) {
            is AppStatus.IsUnderReview -> false
            else -> true
        }
    }

    /**
     * 是否可以管理 vip 方案
     */
    fun isShowVipManager(): Boolean = (MyGroupPermission.editVipRole == true && isAppNotInReview())

    /**
     * 是否可以管理 加入申請
     */
    fun isShowApproval(): Boolean = MyGroupPermission.approveJoinApplies == true

    /**
     * 是否呈現 社團管理 區塊
     */
    fun isShowGroupManage(): Boolean {
        return (MyGroupPermission.editGroup == true) ||
                (MyGroupPermission.createOrEditChannel == true) ||
                (MyGroupPermission.createOrEditCategory == true) ||
                (MyGroupPermission.setGroupPublicity == true) ||
                (MyGroupPermission.rearrangeChannelCategory == true) ||
                (MyGroupPermission.deleteCategory == true) ||
                (MyGroupPermission.deleteChannel == true)
    }

    /**
     * 是否可以編輯 頻道
     */
    fun isEnterChannelEditPermission(): Boolean =
        (MyGroupPermission.createOrEditChannel == true || MyGroupPermission.deleteChannel == true)

    /**
     * 是否可以刪除頻道
     */
    fun isCanDeleteChannel(): Boolean = (MyGroupPermission.deleteChannel == true)

    /**
     * 是否可以 增加頻道
     */
    fun isAddChannelPermission(): Boolean = (MyGroupPermission.createOrEditChannel == true)

    /**
     * 是否可以進入編輯分類 新增/刪除
     */
    fun isEnterEditCategoryPermission(): Boolean =
        (MyGroupPermission.createOrEditCategory == true ||
                MyGroupPermission.deleteCategory == true)

    /**
     * 是否可以編輯分類 新增
     */
    fun isCanEditCategoryPermission(): Boolean = (MyGroupPermission.createOrEditCategory == true)

    /**
     * 是否 可以刪除分類
     */
    fun isCanDeleteCategory(): Boolean = (MyGroupPermission.deleteCategory == true)

    /**
     * 是否可以進入 新增/編輯/刪除 角色
     */
    fun isCanEnterEditRole(): Boolean = (
            MyGroupPermission.createOrEditRole == true ||
                    MyGroupPermission.deleteRole == true ||
                    MyGroupPermission.rearrangeRoles == true)

    /**
     * 是否可以 新增/編輯 角色
     */
    fun isCanEditRole(): Boolean = (MyGroupPermission.createOrEditRole == true)

    /**
     * 是否可以 進入成員管理
     */
    fun isCanEnterMemberManager(): Boolean =
        (MyGroupPermission.createOrEditRole == true ||
                MyGroupPermission.assignRole == true ||
                MyGroupPermission.banOrKickMember == true)

    /**
     * 是否可以 刪除 角色
     */
    fun isCanDeleteRole(): Boolean = (MyGroupPermission.deleteRole == true)

    /**
     * 是否可以 禁言/踢出 社團
     */
    fun isCanBanKickMember(): Boolean = (MyGroupPermission.banOrKickMember == true)

    /**
     * 是否可以按 Emoji
     */
    fun isCanEmoji(): Boolean = (MyChannelPermission.canEmoji == true)

    /**
     * 是否可以 回覆訊息
     */
    fun isCanReply(): Boolean = (MyChannelPermission.canReply == true)

    /**
     * 是否可以 收回訊息
     */
    fun isCanTakeBack(): Boolean = (MyChannelPermission.canTakeback == true)

    /**
     * 是否可以 複製
     */
    fun isCanCopy(): Boolean = (MyChannelPermission.canCopy == true)

    /**
     * 是否可以 置頂/刪除 訊息
     */
    fun isCanManage(): Boolean = (MyChannelPermission.canManage == true)

    /**
     * 是否可以 封鎖用戶
     */
    fun isCanBlock(): Boolean = (MyChannelPermission.canBlock == true)

    /**
     * 是否可以 檢舉用戶
     */
    fun isCanReport(): Boolean = (MyChannelPermission.canReport == true)

    /**
     * 是否可以讀取聊天室內容
     */
    fun canReadMessage(): Boolean = (MyChannelPermission.canRead == true)

    /**
     * 是否可以發文
     */
    fun canPostMessage(): Boolean = (MyChannelPermission.canPost == true)

    /**
     * channel 下 是否被禁言
     */
    fun isBuffSilence(): Boolean {
        return MyChannelBuff.buffs?.let { buff ->
            buff.forEach { status ->
                if (status.name == "禁言") {
                    return@let true
                }
            }
            return@let false
        } ?: false
    }

    /**
     * 取得 頻道無法發言 原因
     */
    fun getChannelSilenceDesc(
        context: Context,
        default: String = context.getString(R.string.silence_result_desc)
    ): String {
        return MyChannelBuff.buffs?.let { buff ->
            buff.forEach { status ->
                if (status.name == "禁言") {
                    return@let status.description.orEmpty()
                }
            }
            return@let context.getString(R.string.silence_result_desc)
        } ?: default
    }

    val emojiLit = listOf(
        R.drawable.emoji_money,
        R.drawable.emoji_shock,
        R.drawable.emoji_laugh,
        R.drawable.emoji_angry,
        R.drawable.emoji_think,
        R.drawable.emoji_cry,
        R.drawable.emoji_like,
    )

}