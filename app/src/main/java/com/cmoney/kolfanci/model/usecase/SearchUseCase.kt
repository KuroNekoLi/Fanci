package com.cmoney.kolfanci.model.usecase

import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.kolfanci.model.mock.MockData

class SearchUseCase {

    /**
     * 搜尋
     */
    suspend fun doSearch(keyword: String): Result<List<ChatMessage>> {
        return Result.success(MockData.mockListMessage)
    }
}