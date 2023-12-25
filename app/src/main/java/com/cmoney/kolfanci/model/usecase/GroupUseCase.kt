package com.cmoney.kolfanci.model.usecase

import android.content.Context
import android.net.Uri
import com.cmoney.fanciapi.fanci.api.DefaultImageApi
import com.cmoney.fanciapi.fanci.api.GroupApi
import com.cmoney.fanciapi.fanci.api.GroupMemberApi
import com.cmoney.fanciapi.fanci.api.GroupRequirementApi
import com.cmoney.fanciapi.fanci.api.PermissionApi
import com.cmoney.fanciapi.fanci.api.RoleUserApi
import com.cmoney.fanciapi.fanci.api.UserReportApi
import com.cmoney.fanciapi.fanci.model.Color
import com.cmoney.fanciapi.fanci.model.ColorTheme
import com.cmoney.fanciapi.fanci.model.EditGroupParam
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupParam
import com.cmoney.fanciapi.fanci.model.GroupRequirementParam
import com.cmoney.fanciapi.fanci.model.GroupRequirementQuestion
import com.cmoney.fanciapi.fanci.model.GroupRequirementQuestionType
import com.cmoney.fanciapi.fanci.model.OrderType
import com.cmoney.fanciapi.fanci.model.ReportProcessStatus
import com.cmoney.fanciapi.fanci.model.ReportStatusUpdateParam
import com.cmoney.fanciapi.fanci.model.RoleColor
import com.cmoney.fanciapi.fanci.model.RoleIdsParam
import com.cmoney.fanciapi.fanci.model.RoleParam
import com.cmoney.fanciapi.fanci.model.UpdateIsNeedApprovalParam
import com.cmoney.fanciapi.fanci.model.UseridsParam
import com.cmoney.kolfanci.extension.checkResponseBody
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.screens.follow.model.GroupItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class GroupUseCase(
    val context: Context,
    private val groupApi: GroupApi,
    private val groupMemberApi: GroupMemberApi,
    private val defaultImageApi: DefaultImageApi,
    private val permissionApi: PermissionApi,
    private val roleUserApi: RoleUserApi,
    private val groupRequirement: GroupRequirementApi,
    private val userReport: UserReportApi,
    private val uploadImageUseCase: UploadImageUseCase
) {

    /**
     * 取得會員資訊
     *
     * @param groupId 社團 id
     * @param userIds 會員 ids
     */
    suspend fun getGroupMembers(groupId: String, userIds: List<String>) = kotlin.runCatching {
        if (Constant.isOpenMock) {
            listOf(
                MockData.mockGroupMember,
                MockData.mockGroupMember,
                MockData.mockGroupMember,
                MockData.mockGroupMember
            )
        } else {
            groupMemberApi.apiV1GroupMemberGroupGroupIdUsersPost(
                groupId = groupId,
                useridsParam = UseridsParam(
                    userIds = userIds
                )
            ).checkResponseBody()
        }
    }

    /**
     * 取得特定 group
     */
    suspend fun getGroupById(groupId: String) = kotlin.runCatching {
        groupApi.apiV1GroupGroupIdGet(
            groupId = groupId
        ).checkResponseBody()
    }

    /**
     * 刪除/解散 社團
     */
    suspend fun deleteGroup(groupId: String) = kotlin.runCatching {
        groupApi.apiV1GroupGroupIdDelete(groupId = groupId).checkResponseBody()
    }

    /**
     * 刪除 角色
     * @param groupId 社團id
     * @param roleId 角色 id
     */
    suspend fun deleteRole(groupId: String, roleId: String) = kotlin.runCatching {
        groupApi.apiV1GroupGroupIdRoleRoleIdDelete(
            groupId = groupId,
            roleId = roleId
        ).checkResponseBody()
    }

    /**
     * 更新 處理狀態
     * @param channelId 頻道
     * @param reportId 檢舉 id
     * @param reportProcessStatus 檢舉狀態
     */
    suspend fun handlerReport(
        channelId: String,
        reportId: String,
        reportProcessStatus: ReportProcessStatus
    ) = kotlin.runCatching {
        userReport.apiV1UserReportChannelChannelIdIdPut(
            channelId = channelId,
            id = reportId,
            reportStatusUpdateParam = ReportStatusUpdateParam(
                status = reportProcessStatus
            )
        ).checkResponseBody()
    }

    /**
     * 取得 檢舉審核清單
     */
    suspend fun getReportList(groupId: String) = kotlin.runCatching {
        userReport.apiV1UserReportGroupGroupIdGet(
            groupId = groupId
        ).checkResponseBody()
    }

    /**
     * 建立社團
     *
     * @param name 社團名稱
     * @param description 社團描述, 一開始建立時,可以不用輸入 (需求)
     * @param isNeedApproval 是否需要認證
     * @param coverImageUrl 背景圖
     * @param thumbnailImageUrl 小圖
     * @param themeId 背景主題Id
     */
    suspend fun createGroup(
        name: String,
        description: String = "",
        isNeedApproval: Boolean,
        coverImageUrl: String,
        thumbnailImageUrl: String,
        themeId: String
    ) = kotlin.runCatching {
        groupApi.apiV1GroupPost(
            groupParam = GroupParam(
                name = name,
                description = description,
                isNeedApproval = isNeedApproval,
                coverImageUrl = coverImageUrl,
                thumbnailImageUrl = thumbnailImageUrl,
                colorSchemeGroupKey = ColorTheme.decode(themeId)
            )
        ).checkResponseBody()
    }

    /**
     * 設定 加入社團 問題清單
     * @param groupId 社團 id
     * @param question 問題清單
     */
    suspend fun setGroupRequirementQuestion(groupId: String, question: List<String>) =
        kotlin.runCatching {
            groupRequirement.apiV1GroupRequirementGroupGroupIdPut(
                groupId = groupId,
                groupRequirementParam = GroupRequirementParam(
                    questions = question.map {
                        GroupRequirementQuestion(
                            question = it,
                            type = GroupRequirementQuestionType.written
                        )
                    }
                )
            ).checkResponseBody()
        }

    /**
     * 抓取 加入要求題目
     */
    suspend fun fetchGroupRequirement(groupId: String) = kotlin.runCatching {
        groupRequirement.apiV1GroupRequirementGroupGroupIdGet(groupId = groupId).checkResponseBody()
    }


    /**
     *
     * 設定 社團 是否需要審核
     *
     * @param groupId 社團 id
     * @param isNeedApproval 是否需要審核
     */
    suspend fun setGroupNeedApproval(groupId: String, isNeedApproval: Boolean) =
        kotlin.runCatching {
            groupApi.apiV1GroupGroupIdIsNeedApprovalPut(
                groupId = groupId,
                updateIsNeedApprovalParam = UpdateIsNeedApprovalParam(
                    isNeedApproval = isNeedApproval
                )
            ).checkResponseBody()
        }

    /**
     * 剔除成員
     * @param groupId 社團id
     * @param userId 要被踢除的會員 id
     */
    suspend fun kickOutMember(groupId: String, userId: String) = kotlin.runCatching {
        groupMemberApi.apiV1GroupMemberGroupGroupIdUserIdDelete(
            groupId, userId
        ).checkResponseBody()
    }

    /**
     * 取得 具有該角色權限的使用者清單
     * @param groupId 社團id
     * @param roleId 角色id
     */
    suspend fun fetchRoleMemberList(
        groupId: String,
        roleId: String
    ) = kotlin.runCatching {
        roleUserApi.apiV1RoleUserGroupGroupIdRoleRoleIdGet(
            groupId = groupId,
            roleId = roleId
        ).checkResponseBody()
    }


    /**
     * 移除使用者多個 角色權限
     *
     * @param groupId 社團 id
     * @param userId 使用者id
     * @param roleIds 角色清單
     */
    suspend fun removeRoleFromUser(groupId: String, userId: String, roleIds: List<String>) =
        kotlin.runCatching {
            roleUserApi.apiV1RoleUserGroupGroupIdMemberUserIdDelete(
                groupId = groupId,
                userId = userId,
                roleIdsParam = RoleIdsParam(roleIds = roleIds)
            ).checkResponseBody()
        }

    /**
     * 移除多個使用者的角色
     * @param groupId 群組id
     * @param roleId 角色id
     * @param userId 要移除的 user 清單
     */
    suspend fun removeUserRole(
        groupId: String,
        roleId: String,
        userId: List<String>
    ) = kotlin.runCatching {
        roleUserApi.apiV1RoleUserGroupGroupIdRoleRoleIdDelete(
            groupId = groupId,
            roleId = roleId,
            useridsParam = UseridsParam(
                userIds = userId
            )
        ).checkResponseBody()
    }

    /**
     * 指派 使用者 多個角色
     * @param groupId 社團 id
     * @param userId 使用者id
     * @param roleIds 角色清單
     */
    suspend fun addRoleToMember(groupId: String, userId: String, roleIds: List<String>) =
        kotlin.runCatching {
            roleUserApi.apiV1RoleUserGroupGroupIdMemberUserIdPut(
                groupId = groupId,
                userId = userId,
                roleIdsParam = RoleIdsParam(
                    roleIds = roleIds
                )
            ).checkResponseBody()
        }

    /**
     * 指派 角色身份 給 多個使用者
     * @param groupId 社團 id
     * @param roleId 要分配的角色 id
     * @param memberList 多個使用者
     */
    suspend fun addMemberToRole(
        groupId: String,
        roleId: String,
        memberList: List<String>
    ) = kotlin.runCatching {
        roleUserApi.apiV1RoleUserGroupGroupIdRoleRoleIdPut(
            groupId = groupId,
            roleId = roleId,
            useridsParam = UseridsParam(
                userIds = memberList
            )
        ).checkResponseBody()
    }

    /**
     * 社團 新增角色
     *
     * @param groupId 社團 id
     * @param name 角色名稱
     * @param permissionIds 權限清單
     * @param colorCode 色碼代碼
     */
    suspend fun addGroupRole(
        groupId: String,
        name: String,
        permissionIds: List<String>,
        colorCode: Color
    ) = kotlin.runCatching {
        groupApi.apiV1GroupGroupIdRolePost(
            groupId = groupId,
            roleParam = RoleParam(
                name = name,
                permissionIds = permissionIds,
                color = RoleColor.decode(colorCode.name)
            )
        ).checkResponseBody()
    }

    /**
     * 社團 編輯角色
     *
     * @param groupId 社團 id
     * @param roleId 要編輯的角色id
     * @param name 角色名稱
     * @param permissionIds 權限清單
     * @param colorCode 色碼代碼
     */
    suspend fun editGroupRole(
        groupId: String,
        roleId: String,
        name: String,
        permissionIds: List<String>,
        colorCode: Color
    ) = kotlin.runCatching {
        groupApi.apiV1GroupGroupIdRoleRoleIdPut(
            groupId = groupId,
            roleId = roleId,
            roleParam = RoleParam(
                name = name,
                permissionIds = permissionIds,
                color = RoleColor.decode(colorCode.name)
            )
        ).checkResponseBody()
    }

    /**
     * 取得群組會員清單, 預設抓取20筆
     *
     * @param groupId 群組id
     * @param skipCount 因為分頁關係,要跳過前幾筆
     * @param search 關鍵字搜尋
     */
    suspend fun getGroupMember(groupId: String, skipCount: Int = 0, search: String? = null) =
        kotlin.runCatching {
            groupMemberApi.apiV1GroupMemberGroupGroupIdGet(
                groupId = groupId,
                skip = skipCount,
                search = if (search?.isEmpty() == true) {
                    null
                } else {
                    search
                }
            ).checkResponseBody()
        }

    /**
     * 取得管理權限清單
     */
    suspend fun fetchPermissionList() = kotlin.runCatching {
        permissionApi.apiV1PermissionGet().checkResponseBody()
    }

    /**
     * 取得 群組角色列表
     */
    suspend fun fetchGroupRole(groupId: String) = kotlin.runCatching {
        groupApi.apiV1GroupGroupIdRoleGet(groupId).checkResponseBody()
    }

    /**
     * 抓取 預設 大頭貼圖庫
     */
    suspend fun fetchGroupAvatarLib() = kotlin.runCatching {
        defaultImageApi.apiV1DefaultImageGet().checkResponseBody().defaultImages?.get("001")
            ?: emptyList()
    }

    /**
     * 抓取 預設 背景圖庫
     */
    suspend fun fetchGroupCoverLib() = kotlin.runCatching {
        defaultImageApi.apiV1DefaultImageGet().checkResponseBody().defaultImages?.get("002")
            ?: emptyList()
    }

    /**
     * 更換 社團背景圖
     * @param uri 圖片Uri
     * @param group 社團 model
     */
    suspend fun changeGroupBackground(uri: Any, group: Group): Flow<String> {
        return flow {
            var imageUrl = ""
            if (uri is Uri) {
                val uploadResult = uploadImageUseCase.uploadImage(listOf(uri)).first()
                imageUrl = uploadResult.second
                emit(imageUrl)
            } else if (uri is String) {
                imageUrl = uri
                emit(uri)
            }

            if (imageUrl.isNotEmpty()) {
                groupApi.apiV1GroupGroupIdPut(
                    groupId = group.id.orEmpty(),
                    editGroupParam = createEditGroupParam(group, coverImageUrl = imageUrl)
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 更換 社團縮圖
     * @param uri 圖片Uri, or 網址 (Fanci 預設)
     * @param group 社團 model
     */
    suspend fun changeGroupAvatar(uri: Any, group: Group): Flow<String> {
        return changeGroupImage(uri, group) { imageUrl ->
            createEditGroupParam(group, thumbnailImageUrl = imageUrl)
        }
    }

    /**
     * 更換 社團 Logo
     * @param uri 圖片Uri, or 網址 (Fanci 預設)
     * @param group 社團 model
     */
    suspend fun changeGroupLogo(uri: Any, group: Group): Flow<String> {
        return changeGroupImage(uri, group) { imageUrl ->
            createEditGroupParam(group, logoImageUrl = imageUrl)
        }
    }

    /**
     * 更換 社團簡介
     * @param desc 更換的簡介
     * @param group 社團 model
     */
    suspend fun changeGroupDesc(desc: String, group: Group) = kotlin.runCatching {
        groupApi.apiV1GroupGroupIdPut(
            groupId = group.id.orEmpty(), editGroupParam = createEditGroupParam(group, desc = desc)
        ).checkResponseBody()
    }

    /**
     * 更換 社團名字
     * @param name 更換的名字
     * @param group 社團 model
     */
    suspend fun changeGroupName(name: String, group: Group) = kotlin.runCatching {
        groupApi.apiV1GroupGroupIdPut(
            groupId = group.id.orEmpty(), editGroupParam = createEditGroupParam(group, name = name)
        ).checkResponseBody()
    }

    /**
     * 加入社團
     */
    suspend fun joinGroup(group: Group) = kotlin.runCatching {
        groupMemberApi.apiV1GroupMemberGroupGroupIdMePut(group.id.orEmpty())
            .checkResponseBody()
    }

    /**
     * 取得 最新 社團列表
     */
    suspend fun getNewestGroup(pageSize: Int = 100, startWeight: Long = Long.MAX_VALUE) =
        kotlin.runCatching {
            groupApi.apiV1GroupGet(
                startWeight = startWeight,
                orderType = OrderType.latest,
                pageSize = pageSize
            ).checkResponseBody()
        }

    /**
     * 取得 熱門 社團列表
     */
    suspend fun getPopularGroup(pageSize: Int = 100, startWeight: Long = Long.MAX_VALUE) =
        kotlin.runCatching {
            groupApi.apiV1GroupGet(
                startWeight = startWeight,
                orderType = OrderType.popular,
                pageSize = pageSize
            ).checkResponseBody()
        }

    /**
     * 取得我加入的社團頻道清單
     */
    private suspend fun getMyJoinGroup() =
        kotlin.runCatching {
            groupApi.apiV1GroupMeGet(pageSize = 100).checkResponseBody()
        }

    /**
     * 取得我加入的社團頻道清單, 並設定第一個為目前選中的社團
     */
    suspend fun groupToSelectGroupItem() =
        kotlin.runCatching {
            if (Constant.isOpenMock) {
                listOf(
                    MockData.mockGroup,
                    MockData.mockGroup,
                    MockData.mockGroup
                ).mapIndexed { index, group ->
                    GroupItem(group, index == 0)
                }
            } else {
                getMyJoinGroup().getOrNull()?.items?.mapIndexed { index, group ->
                    GroupItem(group, index == 0)
                }.orEmpty()
            }
        }

    /**
     * 退出社團
     *
     * @param id 欲退出的社團編號
     * @return Result.success 表示退出成功，Result.failure 表示退出失敗
     */
    suspend fun leaveGroup(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            groupMemberApi.apiV1GroupMemberGroupGroupIdMeDelete(groupId = id)
                .checkResponseBody()
        }
    }

    private fun createEditGroupParam(
        group: Group,
        name: String? = null,
        desc: String? = null,
        coverImageUrl: String? = null,
        logoImageUrl: String? = null,
        thumbnailImageUrl: String? = null
    ): EditGroupParam {
        return EditGroupParam(
            name = name ?: group.name.orEmpty(),
            description = desc ?: group.description,
            coverImageUrl = coverImageUrl ?: group.coverImageUrl,
            thumbnailImageUrl = thumbnailImageUrl ?: group.thumbnailImageUrl,
            colorSchemeGroupKey = ColorTheme.decode(group.colorSchemeGroupKey?.value),
            logoImageUrl = logoImageUrl ?: group.logoImageUrl
        )
    }

    private suspend fun processUriAndGetImageUrl(uri: Any): String {
        return when (uri) {
            is Uri -> {
                val uploadResult = uploadImageUseCase.uploadImage(listOf(uri)).first()
                uploadResult.second
            }

            is String -> uri
            else -> ""
        }
    }

    private suspend fun changeGroupImage(
        uri: Any,
        group: Group,
        editParam: (String) -> EditGroupParam
    ): Flow<String> {
        return flow {
            val imageUrl = processUriAndGetImageUrl(uri)
            if (imageUrl.isNotEmpty()) {
                emit(imageUrl)
                groupApi.apiV1GroupGroupIdPut(
                    groupId = group.id.orEmpty(),
                    editGroupParam = editParam(imageUrl)
                )
            }
        }.flowOn(Dispatchers.IO)
    }
}