package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.PurchasedSale
import com.cmoney.fanciapi.fanci.model.VipRole
import com.cmoney.fanciapi.fanci.model.VipSale

interface VipApi {
    /**
     * 取得該用戶所購買的所有方案 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param userId 
     * @return [kotlin.collections.List<PurchasedSale>]
     */
    @GET("api/v1/Vip/PurchasedSale/{userId}")
    suspend fun apiV1VipPurchasedSaleUserIdGet(@Path("userId") userId: kotlin.String): Response<kotlin.collections.List<PurchasedSale>>

    /**
     * 當創建一個新的權限包時可打 權限包 &#x3D; VipRole  提供一個權限包ID 並綁定Group  作法 : 創建一個新的Role 綁定權限包並加入Group中  EX : 理財寶後台新增 9487 權限包       是對應到16190 Group       打此方法會新增一個Role       名稱來自理財寶設定的權限包名稱       此VipRole便綁定到該角色 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param vipRoleId 
     * @param groupId 
     * @return [VipRole]
     */
    @PUT("api/v1/Vip/VipRole/{vipRoleId}/{groupId}")
    suspend fun apiV1VipVipRoleVipRoleIdGroupIdPut(@Path("vipRoleId") vipRoleId: kotlin.String, @Path("groupId") groupId: kotlin.String): Response<VipRole>

    /**
     * 透過角色Id取得該權限包相關的VIP方案 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param roleId 
     * @return [kotlin.collections.List<VipSale>]
     */
    @GET("api/v1/Vip/VipSales/{roleId}")
    suspend fun apiV1VipVipSalesRoleIdGet(@Path("roleId") roleId: kotlin.String): Response<kotlin.collections.List<VipSale>>

}
