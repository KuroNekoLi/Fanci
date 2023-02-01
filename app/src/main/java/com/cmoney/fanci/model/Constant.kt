package com.cmoney.fanci.model

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
}