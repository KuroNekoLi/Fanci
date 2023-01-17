package com.cmoney.fanci.model.usecase

import com.cmoney.fanci.extension.checkResponseBody
import com.cmoney.fanciapi.fanci.api.RelationApi
import com.cmoney.fanciapi.fanci.model.Relation
import com.cmoney.fanciapi.fanci.model.User
import com.cmoney.fanciapi.fanci.model.UserPaging

class RelationUseCase(private val relationApi: RelationApi) {
    /**
     * 取得 我封鎖 以及 封鎖我 的清單
     * @return Pair<Blocking, Blocker>
     */
    suspend fun getMyRelation(): Pair<List<User>, List<User>> {
        val blockingList = mutableListOf<UserPaging>()
        val firstBlocking = getBlockingRelation()
        blockingList.add(firstBlocking)
        var hasNextBlockingPage = firstBlocking.haveNextPage == true
        while (hasNextBlockingPage) {
            val blockingPage = getBlockingRelation(skip = blockingList.size)
            blockingList.add(blockingPage)
            hasNextBlockingPage = blockingPage.haveNextPage == true
        }

        val blockerList = mutableListOf<UserPaging>()
        val firstBlocker = getBlockerRelation()
        blockerList.add(firstBlocker)
        var hasNextBlockerPage = firstBlocker.haveNextPage == true
        while (hasNextBlockerPage) {
            val blockerPage = getBlockingRelation(skip = blockerList.size)
            blockerList.add(blockerPage)
            hasNextBlockerPage = blockerPage.haveNextPage == true
        }

        return Pair(
            blockingList.distinctBy {
                it.items
            }.map {
                it.items.orEmpty()
            }.flatten(),
            blockerList.distinctBy {
                it.items
            }.map {
                it.items.orEmpty()
            }.flatten()
        )
    }


    /**
     * 取得 我封鎖的清單
     */
    private suspend fun getBlockingRelation(skip: Int = 0) =
        relationApi.apiV1RelationRelationMeGet(
            skip = skip,
            relation = Relation.blocking
        ).checkResponseBody()


    /**
     * 取得 封鎖我的清單
     */
    private suspend fun getBlockerRelation(skip: Int = 0) =
        relationApi.apiV1RelationRelationMeGet(
            skip = skip,
            relation = Relation.blocker
        ).checkResponseBody()

    /**
     * 封鎖對方
     * @param userId 被封鎖的人
     */
    suspend fun blocking(userId: String) = kotlin.runCatching {
        relationApi.apiV1RelationRelationMeBlockUserIdPut(
            relation = Relation.blocking,
            blockUserId = userId
        ).checkResponseBody()
    }
}