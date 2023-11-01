package com.cmoney.kolfanci.model.usecase

import android.net.Uri
import com.cmoney.kolfanci.repository.Network

class AttachmentUseCase(private val network: Network) {

    suspend fun uploadFile(uri: Uri) = network.uploadFile(uri)


}