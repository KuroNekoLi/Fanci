package com.cmoney.fanci.repository

import com.cmoney.fanciapi.fanci.api.GroupApi
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

class NetworkImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val groupApi: GroupApi
) : Network {

    override suspend fun testGroup()  =
        withContext(dispatcher) {
            kotlin.runCatching {
                groupApi.apiV1GroupGet().checkResponseBody()
            }
        }


//        groupApi.apiV1GroupGet().enqueue(object: Callback<GroupPaging>{
//            override fun onResponse(call: Call<GroupPaging>, response: Response<GroupPaging>) {
//                KLog.i("TAG", "12313")
//            }
//
//            override fun onFailure(call: Call<GroupPaging>, t: Throwable) {
//                KLog.i("TAG", "onFailure")
//            }
//        })

    @Throws(
        HttpException::class,
        JsonSyntaxException::class
    )
    inline fun <reified T : Response<T1>, reified T1> T.checkResponseBody(): T1 {
        return when {
            this.isSuccessful -> {
                this.body() ?: throw EmptyBodyException()
            }
            else -> {
                when (code()) {
                    403, 404, 409 -> {
                        val errorBody = errorBody()?.string().orEmpty()

                        if (errorBody.contains("\"message\"") && errorBody.contains("\"type\"")) {
                            val error = Gson().fromJson(
                                errorBody,
                                GroupError::class.java
                            )
                            throw GroupServerException(this, error)
                        }

                        throw HttpException(this)
                    }
                }
                throw HttpException(this)
            }
        }
    }

    class GroupServerException(response: Response<*>, val groupError: GroupError) :
        HttpException(response)

    data class GroupError(val message: String, val type: Int)

    class EmptyBodyException : Exception("No response body from server")
}