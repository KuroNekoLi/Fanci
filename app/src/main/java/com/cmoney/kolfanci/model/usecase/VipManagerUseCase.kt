package com.cmoney.kolfanci.model.usecase

import com.cmoney.fanciapi.fanci.api.GroupApi
import com.cmoney.fanciapi.fanci.api.RoleUserApi
import com.cmoney.fanciapi.fanci.api.VipApi
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChannelPrivacy
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
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
    private val roleUserApi: RoleUserApi
) {

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
//        getVipPlanMockData()
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
    fun getPermissions(
        group: Group,
        vipPlanModel: VipPlanModel
    ): Result<List<VipPlanPermissionModel>> {
        return kotlin.runCatching {
            group.categories?.fold(mutableListOf<Channel>()) { acc, category ->
                acc.addAll(category.channels.orEmpty())
                acc
            }?.mapNotNull { channel ->
                // TODO 取得每個頻道目前方案下設定的權限
                VipPlanPermissionModel(
                    id = channel.id ?: return@mapNotNull null,
                    name = channel.name ?: return@mapNotNull null,
                    canEdit = when (channel.privacy) {
                        ChannelPrivacy.public -> false
                        ChannelPrivacy.private -> true
                        null -> false
                    },
                    permissionTitle = "基本權限",
                    authType = "basic"
                )
            }.orEmpty()
        }
    }

    /**
     * 取得選擇的VIP方案下可選的權限選項
     *
     * @param vipPlanModel 選擇的方案
     * @return 選項
     */
    fun getPermissionOptions(vipPlanModel: VipPlanModel): Result<List<VipPlanPermissionOptionModel>> {
        return kotlin.runCatching {
            getVipPlanPermissionOptionsMockData()
        }
    }

    /**
     * 取得該會員已購買的vip方案清單
     *
     * @param groupMember 要查的會員
     * @return vip 方案清單
     */
    fun getAlreadyPurchasePlan(groupMember: GroupMember) = kotlin.runCatching {
        getVipPlanMockData()
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
                    authType = "none"
                ),
                VipPlanPermissionOptionModel(
                    name = "基本權限",
                    description = "可以進入此頻道，並且瀏覽",
                    authType = "basic"
                ),
                VipPlanPermissionOptionModel(
                    name = "中階權限",
                    description = "可以進入此頻道，並在貼文留言",
                    authType = "middle"
                ),
                VipPlanPermissionOptionModel(
                    name = "進階權限",
                    description = "可以進入此頻道，可以聊天、發文與留言",
                    authType = "advanced"
                )
            )
        }
    }
}