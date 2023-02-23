package com.cmoney.kolfanci.model

import com.cmoney.fanciapi.fanci.model.ChannelPermission
import com.cmoney.fanciapi.fanci.model.GroupPermission
import com.cmoney.fanciapi.fanci.model.User

object Constant {

    //我的個人資訊
    var MyInfo: User? = null

    //我在目前社團的權限
//    var MyGroupPermission: GroupPermission = GroupPermission()
    var MyGroupPermission: GroupPermission = GroupPermission(
        editGroup = true,
        createOrEditApplyQuestions = true,
        rearrangeChannelCategory = true,
        setGroupPublicity = true,
        createOrEditCategory = true,
        deleteCategory = true,
        createOrEditChannel = true,
        setAnnouncement = true,
        deleteChannel = true,
        createOrEditRole = true,
        deleteRole = true,
        rearrangeRoles = true,
        assignRole = true,
        approveJoinApplies = true,
        deleteMemberMessage = true,
        banOrKickMember = true
    )

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
     * 是否可以編輯 頻道
     */
    fun isChannelEditPermission(): Boolean =
        (MyGroupPermission.createOrEditChannel == true || MyGroupPermission.deleteChannel == true)

    /**
     * 是否可以 增加頻道
     */
    fun isAddChannelPermission(): Boolean = (MyGroupPermission.createOrEditChannel == true)

    /**
     * 是否可以編輯分類 新增/刪除
     */
    fun isEditCategoryPermission(): Boolean = (MyGroupPermission.createOrEditCategory == true ||
            MyGroupPermission.deleteCategory == true)
}