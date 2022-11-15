package com.cmoney.fanci.extension

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import retrofit2.Response

//fun <T : Any?> Response<T>.runCatching() = kotlin.runCatching {
//    this
//}

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