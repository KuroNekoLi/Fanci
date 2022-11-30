package com.cmoney.fanci.model.usecase

import android.content.Context
import android.net.Uri
import com.cmoney.fanci.BuildConfig
import com.cmoney.fanci.extension.checkResponseBody
import com.cmoney.fanci.ui.screens.follow.model.GroupItem
import com.cmoney.fanciapi.fanci.api.GroupApi
import com.cmoney.fanciapi.fanci.api.GroupMemberApi
import com.cmoney.fanciapi.fanci.model.EditGroupParam
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.imagelibrary.UploadImage
import com.cmoney.xlogin.XLoginHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class GroupUseCase(
    val context: Context,
    private val groupApi: GroupApi,
    private val groupMemberApi: GroupMemberApi
) {

    /**
     * 更換 社團縮圖
     * @param uri 圖片Uri
     * @param group 社團 model
     */
    suspend fun changeGroupAvatar(uri: Uri, group: Group): Flow<String> {
        return flow {
//            withContext(Dispatchers.IO) {
                val uploadImage =
                    UploadImage(context, listOf(uri), XLoginHelper.accessToken, BuildConfig.DEBUG)

                uploadImage.upload().collect {
                    val uri = it.first
                    val imageUrl = it.second
                    emit(imageUrl)

                    groupApi.apiV1GroupGroupIdPut(
                        groupId = group.id.orEmpty(), editGroupParam = EditGroupParam(
                            name = group.name.orEmpty(),
                            description = group.description,
                            coverImageUrl = group.coverImageUrl,
                            thumbnailImageUrl = imageUrl
                        )
                    )
                }
//            }
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