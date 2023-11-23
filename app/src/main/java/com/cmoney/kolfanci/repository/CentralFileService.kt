package com.cmoney.kolfanci.repository

import com.cmoney.kolfanci.repository.response.FileUploadResponse
import com.cmoney.kolfanci.repository.response.FileUploadStatusResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Url

interface CentralFileService {

//    @Multipart
//    @POST("centralfileservice/files")
//    suspend fun uploadFile(
//        @Part("File") file: MultipartBody.Part,
//        @Part("type") type: RequestBody,
//        @Part("FileType") fileType: MultipartBody.Part
//    ): Response<FileUploadResponse>

    @Multipart
    @POST("centralfileservice/files")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("FileType") fileType: RequestBody
    ): Response<FileUploadResponse>

    @GET("centralfileservice/files/{fileType}/{externalId}/status")
    suspend fun checkUploadFileStatus(
        @Path("fileType") fileType: String,
        @Path("externalId") externalId: String
    ): Response<FileUploadStatusResponse>

    @GET
    suspend fun getContent(
        @Url url: String
    ): Response<String>
}