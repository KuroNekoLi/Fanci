package com.cmoney.kolfanci.model.usecase

import com.cmoney.fanciapi.fanci.api.ChannelApi
import com.cmoney.fanciapi.fanci.api.GroupApi
import com.cmoney.fanciapi.fanci.api.RoleUserApi
import com.cmoney.fanciapi.fanci.api.VipApi
import com.cmoney.fanciapi.fanci.model.AccessorTypes
import com.cmoney.fanciapi.fanci.model.ChannelAuthType
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.PutAuthTypeRequest
import com.cmoney.fanciapi.fanci.model.RoleChannelAuthType
import com.cmoney.fanciapi.fanci.model.RoleParam
import com.cmoney.kolfanci.extension.checkResponseBody
import com.cmoney.kolfanci.extension.isVip
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanInfoModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanPermissionModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanPermissionOptionModel
import kotlin.random.Random

class VipManagerUseCase(
    private val groupApi: GroupApi,
    private val vipApi: VipApi,
    private val roleUserApi: RoleUserApi,
    private val channelApi: ChannelApi
) {

    /**
     * 取得用戶所購買的所有方案
     */
    suspend fun getUserAllVipPlan(userId: String) = kotlin.runCatching {
        vipApi.apiV1VipPurchasedSaleUserIdGet(userId).checkResponseBody()
    }

    /**
     * 取得所有VIP方案
     */
    fun getVipPlan(): Result<List<VipPlanModel>> {
        return kotlin.runCatching {
            getVipPlanMockData()
        }
    }

    /**
     * 取得 vip 方案清單
     *
     * @param group 社團
     */
    suspend fun getVipPlan(group: Group) = kotlin.runCatching {
        groupApi.apiV1GroupGroupIdVipRoleGet(groupId = group.id.orEmpty()).checkResponseBody()
    }

    /**
     * 取得該vip 的銷售方案
     *
     * @param roleId 角色 id (vip相當於角色)
     */
    suspend fun getVipSales(roleId: String) = kotlin.runCatching {
        vipApi.apiV1VipVipSalesRoleIdGet(roleId = roleId).checkResponseBody()
    }

    /**
     * 取得該 vip 方案 詳細資訊, 過濾掉非vip 的成員
     *
     * @param vipPlanModel 選擇的方案
     */
    suspend fun getVipPlanInfo(vipPlanModel: VipPlanModel) = kotlin.runCatching {
        val mockData = getVipPlanInfoMockData()
        val filterPlanModel = mockData.copy(
            members = mockData.members.filter {
                it.isVip()
            }
        )
        filterPlanModel
    }

    /**
     * 取得某社團下該 vip 方案的詳細資訊
     *
     * @param group 指定社團
     * @param vipPlanModel 選擇的方案
     * @return 社團所有頻道此方案下的權限設定
     */
    suspend fun getPermissions(
        group: Group,
        vipPlanModel: VipPlanModel
    ) = kotlin.runCatching {
        //取得該角色 在此社團下 所有頻道的權限
        groupApi.apiV1GroupGroupIdRoleIdChannelAuthTypeGet(
            groupId = group.id.orEmpty(),
            roleId = vipPlanModel.id
        ).checkResponseBody().map { roleChannelAuthType ->
            VipPlanPermissionModel(
                id = roleChannelAuthType.channelId.orEmpty(),
                name = roleChannelAuthType.channelName.orEmpty(),
                canEdit = roleChannelAuthType.isPublic != true,
                permissionTitle = "",
                authType = roleChannelAuthType.authType ?: ChannelAuthType.noPermission
            )
        }
    }

    /**
     * 取得某社團下該 vip 方案的詳細資訊
     *
     * @param group 指定社團
     * @param vipPlanModel 選擇的方案
     * @return 社團所有頻道此方案下的權限設定
     */
    suspend fun getPermissionWithAuthTitle(
        group: Group,
        vipPlanModel: VipPlanModel
    ) = kotlin.runCatching {
        //權限清單表
        val permissionList = getPermissionOptions(vipPlanModel = vipPlanModel).getOrNull().orEmpty()

        //取得該角色 在此社團下 所有頻道的權限
        groupApi.apiV1GroupGroupIdRoleIdChannelAuthTypeGet(
            groupId = group.id.orEmpty(),
            roleId = vipPlanModel.id
        ).checkResponseBody().map { roleChannelAuthType ->
            val authType = roleChannelAuthType.authType ?: ChannelAuthType.noPermission
            VipPlanPermissionModel(
                id = roleChannelAuthType.channelId.orEmpty(),
                name = roleChannelAuthType.channelName.orEmpty(),
                canEdit = roleChannelAuthType.isPublic != true,
                permissionTitle = getPermissionTitle(
                    roleChannelAuthType = roleChannelAuthType,
                    authType = authType,
                    permissionList = permissionList
                ),
                authType = authType
            )
        }
    }

    /**
     * 取得 authType 對應的顯示名稱
     * @param authType
     * @param permissionList 權限表
     */
    private fun getPermissionTitle(
        authType: ChannelAuthType,
        permissionList: List<VipPlanPermissionOptionModel>,
        roleChannelAuthType: RoleChannelAuthType
    ): String {
        return if (roleChannelAuthType.isPublic == true) {
            "公開頻道"
        } else {
            permissionList.first {
                it.authType == authType
            }.name
        }
    }

    /**
     * 取得選擇的VIP方案下可選的權限選項
     *
     * @param vipPlanModel 選擇的方案
     * @return 選項
     */
    suspend fun getPermissionOptions(vipPlanModel: VipPlanModel): Result<List<VipPlanPermissionOptionModel>> {
        return kotlin.runCatching {
            channelApi.apiV2ChannelAccessTypeGet(
                isWithNoPermission = true
            ).checkResponseBody().map { channelAccessOptionV2 ->
                VipPlanPermissionOptionModel(
                    name = channelAccessOptionV2.title.orEmpty(),
                    description = channelAccessOptionV2.allowedAction.orEmpty(),
                    authType = channelAccessOptionV2.authType ?: ChannelAuthType.noPermission
                )
            }
        }
    }


    /**
     * 設定 vip角色 在此頻道 的權限
     *
     * @param channelId 頻道id
     * @param vipRoleId 要設定的vip角色id
     * @param authType 權限
     */
    suspend fun setChannelVipRolePermission(
        channelId: String,
        vipRoleId: String,
        authType: ChannelAuthType
    ) = kotlin.runCatching {
        channelApi.apiV1ChannelChannelIdWhiteListAccessorTypeAccessorIdPut(
            channelId = channelId,
            accessorType = AccessorTypes.vipRole,
            accessorId = vipRoleId,
            putAuthTypeRequest = PutAuthTypeRequest(
                authType = authType
            )
        ).checkResponseBody()
    }

    /**
     * 取得該會員已購買的vip方案清單
     *
     * @param groupId 所在的社團
     * @param groupMember 要查的會員
     * @return vip 方案清單
     */
    suspend fun getAlreadyPurchasePlan(groupId: String, groupMember: GroupMember) =
        kotlin.runCatching {
            roleUserApi.apiV1RoleUserGroupGroupIdUserIdVipRoleGet(
                groupId = groupId,
                userId = groupMember.id.orEmpty()
            ).checkResponseBody()
        }

    /**
     * 更換 vip 名稱
     *
     * @param groupId 社團 id
     * @param roleId 角色 id (vip 方案 id)
     * @param name 要更改的名稱
     */
    suspend fun changeVipRoleName(groupId: String, roleId: String, name: String) =
        kotlin.runCatching {
            groupApi.apiV1GroupGroupIdRoleRoleIdPut(
                groupId = groupId,
                roleId = roleId,
                roleParam = RoleParam(
                    name = name
                )
            )
        }


    /**
     * 取得該vip 方案下的成員清單
     *
     * @param groupId 社團 id
     * @param roleId 角色 id (vip 方案 id)
     */
    suspend fun getVipPlanMember(groupId: String, roleId: String) = kotlin.runCatching {
        roleUserApi.apiV1RoleUserGroupGroupIdRoleRoleIdGet(
            groupId = groupId,
            roleId = roleId
        ).checkResponseBody()
    }


    companion object {
        fun getVipPlanMockData() = listOf(
            VipPlanModel(
                id = "1",
                name = "高級學員",
                memberCount = 10,
                description = "99元月訂閱方案"
            ),
            VipPlanModel(
                id = "2",
                name = "進階學員",
                memberCount = 5,
                description = "120元月訂閱方案"
            ),
            VipPlanModel(
                id = "3",
                name = "初階學員",
                memberCount = 0,
                description = "30 元月訂閱方案"
            )
        )

        fun getVipPlanInfoMockData() = VipPlanInfoModel(
            name = "高級學員",
            memberCount = 10,
            planSourceDescList = listOf(
                "・30元訂閱促銷方案",
                "・99元月訂閱方案"
            ),
            members = listOf(
                GroupMember(
                    name = "王力宏",
                    thumbNail = "https://picsum.photos/${
                        Random.nextInt(
                            100,
                            300
                        )
                    }/${Random.nextInt(100, 300)}",
                    serialNumber = Random.nextLong(
                        1000,
                        3000
                    ),
                    roleInfos = listOf(
                        FanciRole(
                            name = "高級學員",
                            userCount = 10
                        )
                    )
                ),
                GroupMember(
                    name = "王力宏1",
                    thumbNail = "https://picsum.photos/${
                        Random.nextInt(
                            100,
                            300
                        )
                    }/${Random.nextInt(100, 300)}",
                    serialNumber = Random.nextLong(
                        1000,
                        3000
                    ),
                    roleInfos = listOf(
                        FanciRole(
                            name = "高級學員",
                            userCount = 10
                        )
                    )
                ),
                GroupMember(
                    name = "王力宏2",
                    thumbNail = "https://picsum.photos/${
                        Random.nextInt(
                            100,
                            300
                        )
                    }/${Random.nextInt(100, 300)}",
                    serialNumber = Random.nextLong(
                        1000,
                        3000
                    ),
                    roleInfos = listOf(
                        FanciRole(
                            name = "高級學員",
                            userCount = 10
                        )
                    )
                ),
                GroupMember(
                    name = "Kevin",
                    thumbNail = "https://picsum.photos/${
                        Random.nextInt(
                            100,
                            300
                        )
                    }/${Random.nextInt(100, 300)}",
                    serialNumber = Random.nextLong(
                        1000,
                        3000
                    ),
                    roleInfos = listOf(
                        FanciRole(
                            name = "高級學員",
                            userCount = 10
                        )
                    )
                ),
                GroupMember(
                    name = "Kevin1",
                    thumbNail = "https://picsum.photos/${
                        Random.nextInt(
                            100,
                            300
                        )
                    }/${Random.nextInt(100, 300)}",
                    serialNumber = Random.nextLong(
                        1000,
                        3000
                    ),
                    roleInfos = listOf(
                        FanciRole(
                            name = "高級學員",
                            userCount = 10
                        )
                    )
                ),
                GroupMember(
                    name = "Kevin2",
                    thumbNail = "https://picsum.photos/${
                        Random.nextInt(
                            100,
                            300
                        )
                    }/${Random.nextInt(100, 300)}",
                    serialNumber = Random.nextLong(
                        1000,
                        3000
                    ),
                    roleInfos = listOf(
                        FanciRole(
                            name = "高級學員",
                            userCount = 10
                        )
                    )
                )
            )
        )

        fun getVipPlanPermissionOptionsMockData(): List<VipPlanPermissionOptionModel> {
            return listOf(
                VipPlanPermissionOptionModel(
                    name = "無權限",
                    description = "不可進入此頻道",
                    authType = ChannelAuthType.noPermission
                ),
                VipPlanPermissionOptionModel(
                    name = "基本權限",
                    description = "可以進入此頻道，並且瀏覽",
                    authType = ChannelAuthType.basic
                ),
                VipPlanPermissionOptionModel(
                    name = "中階權限",
                    description = "可以進入此頻道，並在貼文留言",
                    authType = ChannelAuthType.inter
                ),
                VipPlanPermissionOptionModel(
                    name = "進階權限",
                    description = "可以進入此頻道，可以聊天、發文與留言",
                    authType = ChannelAuthType.advance
                )
            )
        }
    }
}