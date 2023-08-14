package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.IUser
import com.cmoney.fanciapi.fanci.model.PurchasedSale
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
     * 取得授權的用戶 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param roleId 
     * @return [kotlin.collections.List<IUser>]
     */
    @GET("api/v1/Vip/VipRole/{roleId}/Authorization")
    suspend fun apiV1VipVipRoleRoleIdAuthorizationGet(@Path("roleId") roleId: kotlin.String): Response<kotlin.collections.List<IUser>>

    /**
     * 透過角色Id判斷是否購買VIP __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param roleId 
     * @param userId 
     * @return [kotlin.Boolean]
     */
    @GET("api/v1/Vip/VipRole/{roleId}/Authorization/{userId}")
    suspend fun apiV1VipVipRoleRoleIdAuthorizationUserIdGet(@Path("roleId") roleId: kotlin.String, @Path("userId") userId: kotlin.String): Response<kotlin.Boolean>

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
