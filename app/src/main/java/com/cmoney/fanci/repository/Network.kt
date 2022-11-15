package com.cmoney.fanci.repository

import com.cmoney.fanciapi.fanci.model.GroupPaging

interface Network {
    suspend fun testGroup(): Result<GroupPaging>
}