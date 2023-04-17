package com.cmoney.kolfanci.model

import com.cmoney.fanciapi.fanci.model.ChannelPermission
import com.cmoney.fanciapi.fanci.model.GroupPermission
import com.cmoney.fanciapi.fanci.model.User

object Constant {

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
    var MyChannelPermission: ChannelPermission = ChannelPermission()
//    var MyChannelPermission: ChannelPermission = ChannelPermission(
//        canRead = true,
//        canPost = false,
//        canReply = false,
//        canEmoji = false,
//        canManage = false,
//        canCopy = false,
//        canBlock = false,
//        canReport = false,
//        canTakeback = false
//    )


    /**
     * 是否可以管理 加入申請
     */
    fun isShowApproval(): Boolean = MyGroupPermission.approveJoinApplies == true

    /**
     * 是否可以 角色管理
     */
    fun isShowRoleManage(): Boolean = MyGroupPermission.createOrEditRole == true

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
    fun isChannelEditPermission(): Boolean =
        (MyGroupPermission.createOrEditChannel == true || MyGroupPermission.deleteChannel == true)

    /**
     * 是否可以 增加頻道
     */
    fun isAddChannelPermission(): Boolean = (MyGroupPermission.createOrEditChannel == true)

    /**
     * 是否可以進入編輯分類 新增/刪除
     */
    fun isEnterEditCategoryPermission(): Boolean = (MyGroupPermission.createOrEditCategory == true ||
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
}