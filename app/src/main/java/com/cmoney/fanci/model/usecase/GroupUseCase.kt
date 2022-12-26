package com.cmoney.fanci.model.usecase

import android.content.Context
import android.net.Uri
import com.cmoney.fanci.BuildConfig
import com.cmoney.fanci.extension.checkResponseBody
import com.cmoney.fanci.ui.screens.follow.model.GroupItem
import com.cmoney.fanciapi.fanci.api.*
import com.cmoney.fanciapi.fanci.model.*
import com.cmoney.imagelibrary.UploadImage
import com.cmoney.xlogin.XLoginHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GroupUseCase(
    val context: Context,
    private val groupApi: GroupApi,
    private val groupMemberApi: GroupMemberApi,
    private val defaultImageApi: DefaultImageApi,
    private val permissionApi: PermissionApi,
    private val roleUserApi: RoleUserApi
) {

    /**
     * 移除使用者的角色
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
     * 指派 多個使用者 角色身份
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
        )
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
     */
    suspend fun getGroupMember(groupId: String, skipCount: Int = 0) = kotlin.runCatching {
        groupMemberApi.apiV1GroupMemberGroupGroupIdGet(
            groupId = groupId,
            skip = skipCount
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
                val uploadImage =
                    UploadImage(context, listOf(uri), XLoginHelper.accessToken, BuildConfig.DEBUG)

                val uploadResult = uploadImage.upload().first()
                val uri = uploadResult.first
                imageUrl = uploadResult.second
                emit(imageUrl)
            } else if (uri is String) {
                imageUrl = uri
                emit(uri)
            }

            if (imageUrl.isNotEmpty()) {
                groupApi.apiV1GroupGroupIdPut(
                    groupId = group.id.orEmpty(), editGroupParam = EditGroupParam(
                        name = group.name.orEmpty(),
                        description = group.description,
                        coverImageUrl = imageUrl,
                        thumbnailImageUrl = group.thumbnailImageUrl
                    )
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
        return flow {
            var imageUrl = ""
            if (uri is Uri) {
                val uploadImage =
                    UploadImage(context, listOf(uri), XLoginHelper.accessToken, BuildConfig.DEBUG)

                val uploadResult = uploadImage.upload().first()
                val uri = uploadResult.first
                imageUrl = uploadResult.second
                emit(imageUrl)
            } else if (uri is String) {
                imageUrl = uri
                emit(uri)
            }

            if (imageUrl.isNotEmpty()) {
                groupApi.apiV1GroupGroupIdPut(
                    groupId = group.id.orEmpty(), editGroupParam = EditGroupParam(
                        name = group.name.orEmpty(),
                        description = group.description,
                        coverImageUrl = group.coverImageUrl,
                        thumbnailImageUrl = imageUrl
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 更換 社團簡介
     * @param desc 更換的簡介
     * @param group 社團 model
     */
    suspend fun changeGroupDesc(desc: String, group: Group) = kotlin.runCatching {
        groupApi.apiV1GroupGroupIdPut(
            groupId = group.id.orEmpty(), editGroupParam = EditGroupParam(
                name = group.name.orEmpty(),
                description = desc,
                coverImageUrl = group.coverImageUrl,
                thumbnailImageUrl = group.thumbnailImageUrl
            )
        ).checkResponseBody()
    }

    /**
     * 更換 社團名字
     * @param name 更換的名字
     * @param group 社團 model
     */
    suspend fun changeGroupName(name: String, group: Group) = kotlin.runCatching {
        groupApi.apiV1GroupGroupIdPut(
            groupId = group.id.orEmpty(), editGroupParam = EditGroupParam(
                name = name,
                description = group.description,
                coverImageUrl = group.coverImageUrl,
                thumbnailImageUrl = group.thumbnailImageUrl
            )
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
     * 取得社團列表
     */
    suspend fun getGroup() =
        kotlin.runCatching {
            groupApi.apiV1GroupGet().checkResponseBody()
        }

    /**
     * 取得我加入的社團頻道清單
     */
    suspend fun getMyJoinGroup() =
        kotlin.runCatching {
            groupApi.apiV1GroupMeGet(pageSize = 100).checkResponseBody()
        }

    /**
     * 取得我加入的社團頻道清單, 包含目前所選擇的社團
     */
    suspend fun groupToSelectGroupItem() =
        kotlin.runCatching {
            getMyJoinGroup().getOrNull()?.items?.mapIndexed { index, group ->
                GroupItem(group, index == 0)
            }.orEmpty()
        }
}