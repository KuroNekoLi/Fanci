package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.GroupMemberRoleInfos
import com.cmoney.fanciapi.fanci.model.PurchasedRole
import com.cmoney.fanciapi.fanci.model.RoleIdsParam
import com.cmoney.fanciapi.fanci.model.UseridsParam
import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.Response
import retrofit2.http.*

interface RoleUserApi {
    /**
     * 取得頻道中具有Vip角色身分的用戶清單 (任一種VIP)
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 找不到指派的角色
     *
     * @param groupId 
     * @param channelId 
     * @return [kotlin.collections.List<GroupMember>]
     */
    @GET("api/v1/RoleUser/Channel/{channelId}/VipRole")
    suspend fun apiV1RoleUserChannelChannelIdVipRoleGet(@Path("groupId") groupId: kotlin.String, @Path("channelId") channelId: kotlin.String): Response<kotlin.collections.List<GroupMember>>

    /**
     * 判斷用戶是否擁有社團的Vip角色
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 找不到指派的角色
     *
     * @param groupId 
     * @param userId 
     * @return [kotlin.Boolean]
     */
    @GET("api/v1/RoleUser/Group/{groupId}/CheckVipRole/{userId}")
    suspend fun apiV1RoleUserGroupGroupIdCheckVipRoleUserIdGet(@Path("groupId") groupId: kotlin.String, @Path("userId") userId: kotlin.String): Response<kotlin.Boolean>

    /**
     * 取得用戶ID清單的角色列表
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 
     * @param userIds Fanci 用戶 ID (optional)
     * @return [kotlin.collections.List<GroupMemberRoleInfos>]
     */
    @GET("api/v1/RoleUser/Group/{groupId}")
    suspend fun apiV1RoleUserGroupGroupIdGet(@Path("groupId") groupId: kotlin.String, @Query("UserIds") userIds: kotlin.collections.List<kotlin.String>? = null): Response<kotlin.collections.List<GroupMemberRoleInfos>>

    /**
     * 移除使用者的(多個)角色身分 __________🔒 指派身分
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 找不到指派的角色
     *
     * @param groupId 社團Id
     * @param userId 成員Id
     * @param roleIdsParam 使用者清單 (optional)
     * @return [Unit]
     */
    @DELETE("api/v1/RoleUser/Group/{groupId}/Member/{userId}")
    suspend fun apiV1RoleUserGroupGroupIdMemberUserIdDelete(@Path("groupId") groupId: kotlin.String, @Path("userId") userId: kotlin.String, @Body roleIdsParam: RoleIdsParam? = null): Response<Unit>

    /**
     * 指派一個使用者的角色身分(可以設定多個role) __________🔒 指派身分
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 找不到指派的角色
     *
     * @param groupId 社團Id
     * @param userId 成員Id
     * @param roleIdsParam 使用者清單 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/RoleUser/Group/{groupId}/Member/{userId}")
    suspend fun apiV1RoleUserGroupGroupIdMemberUserIdPut(@Path("groupId") groupId: kotlin.String, @Path("userId") userId: kotlin.String, @Body roleIdsParam: RoleIdsParam? = null): Response<Unit>

    /**
     * 取得社團中\&quot;不\&quot;具有此角色身分的用戶清單 (可搜尋)
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 找不到指派的角色
     *
     * @param groupId 
     * @param roleId 
     * @param search  (optional, default to "")
     * @return [kotlin.collections.List<GroupMember>]
     */
    @GET("api/v1/RoleUser/Group/{groupId}/NotInRole/{roleId}")
    suspend fun apiV1RoleUserGroupGroupIdNotInRoleRoleIdGet(@Path("groupId") groupId: kotlin.String, @Path("roleId") roleId: kotlin.String, @Query("search") search: kotlin.String? = ""): Response<kotlin.collections.List<GroupMember>>

    /**
     * 移除使用者的角色身分 __________🔒 指派身分
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 找不到指派的角色
     *
     * @param groupId 社團Id
     * @param roleId 角色Id
     * @param useridsParam 使用者清單 (optional)
     * @return [Unit]
     */
    @HTTP(method = "DELETE", path = "api/v1/RoleUser/Group/{groupId}/Role/{roleId}", hasBody = true)
    suspend fun apiV1RoleUserGroupGroupIdRoleRoleIdDelete(@Path("groupId") groupId: kotlin.String, @Path("roleId") roleId: kotlin.String, @Body useridsParam: UseridsParam? = null): Response<Unit>

    /**
     * 取得社團中具有此角色身分的用戶清單
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 找不到指派的角色
     *
     * @param groupId 
     * @param roleId 
     * @return [kotlin.collections.List<GroupMember>]
     */
    @GET("api/v1/RoleUser/Group/{groupId}/Role/{roleId}")
    suspend fun apiV1RoleUserGroupGroupIdRoleRoleIdGet(@Path("groupId") groupId: kotlin.String, @Path("roleId") roleId: kotlin.String): Response<kotlin.collections.List<GroupMember>>

    /**
     * 指派使用者角色身分 __________🔒 指派身分
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 找不到指派的角色
     *
     * @param groupId 社團Id
     * @param roleId 角色Id
     * @param useridsParam 使用者清單 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/RoleUser/Group/{groupId}/Role/{roleId}")
    suspend fun apiV1RoleUserGroupGroupIdRoleRoleIdPut(@Path("groupId") groupId: kotlin.String, @Path("roleId") roleId: kotlin.String, @Body useridsParam: UseridsParam? = null): Response<Unit>

    /**
     * 取得用戶於社團中所擁有的角色清單 (不含Vip)
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 找不到指派的角色
     *
     * @param groupId 
     * @param userId 
     * @return [kotlin.collections.List<FanciRole>]
     */
    @GET("api/v1/RoleUser/Group/{groupId}/{userId}/Role")
    suspend fun apiV1RoleUserGroupGroupIdUserIdRoleGet(@Path("groupId") groupId: kotlin.String, @Path("userId") userId: kotlin.String): Response<kotlin.collections.List<FanciRole>>

    /**
     * 取得用戶所擁有的社團Vip角色清單
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 找不到指派的角色
     *
     * @param groupId 
     * @param userId 
     * @return [kotlin.collections.List<PurchasedRole>]
     */
    @GET("api/v1/RoleUser/Group/{groupId}/{userId}/VipRole")
    suspend fun apiV1RoleUserGroupGroupIdUserIdVipRoleGet(@Path("groupId") groupId: kotlin.String, @Path("userId") userId: kotlin.String): Response<kotlin.collections.List<PurchasedRole>>

    /**
     * 取得社團中具有Vip角色身分的用戶清單 (任一種VIP)
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 找不到指派的角色
     *
     * @param groupId 
     * @return [kotlin.collections.List<GroupMember>]
     */
    @GET("api/v1/RoleUser/Group/{groupId}/VipRole")
    suspend fun apiV1RoleUserGroupGroupIdVipRoleGet(@Path("groupId") groupId: kotlin.String): Response<kotlin.collections.List<GroupMember>>

}
