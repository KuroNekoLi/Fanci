package com.cmoney.kolfanci.model.usecase

import android.content.Context
import com.cmoney.fanciapi.fanci.api.BulletinBoardApi
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.BulletinboardMessagePaging
import com.cmoney.fanciapi.fanci.model.BulletingBoardMessageParam
import com.cmoney.fanciapi.fanci.model.Media
import com.cmoney.fanciapi.fanci.model.MediaType
import com.cmoney.fanciapi.fanci.model.MessageIdParam
import com.cmoney.fanciapi.fanci.model.OrderType
import com.cmoney.kolfanci.extension.checkResponseBody
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.model.attachment.toUploadMedia

class PostUseCase(
    private val context: Context,
    private val bulletinBoardApi: BulletinBoardApi
) {

    /**
     * 取得 置頂貼文
     */
    suspend fun getPinMessage(channelId: String) = kotlin.runCatching {
        bulletinBoardApi.apiV1BulletinBoardChannelIdPinnedMessageGet(channelId).checkResponseBody()
    }

    /**
     * 取消置頂貼文
     */
    suspend fun unPinPost(channelId: String) = kotlin.runCatching {
        bulletinBoardApi.apiV1BulletinBoardChannelIdPinnedMessageDelete(
            channelId = channelId
        ).checkResponseBody()
    }

    /**
     * 置頂貼文
     */
    suspend fun pinPost(channelId: String, messageId: String) = kotlin.runCatching {
        bulletinBoardApi.apiV1BulletinBoardChannelIdPinnedMessagePut(
            channelId = channelId,
            messageIdParam = MessageIdParam(
                messageId = messageId
            )
        ).checkResponseBody()
    }

    /**
     * 撰寫po 文
     *
     * @param channelId 頻道 id
     * @param text 內文
     * @param attachment 附加檔案
     */
    suspend fun writePost(
        channelId: String,
        text: String,
        attachment: List<Pair<AttachmentType, AttachmentInfoItem>>
    ): Result<BulletinboardMessage> {
        val medias = attachment.toUploadMedia(context)

        val bulletingBoardMessageParam = BulletingBoardMessageParam(
            text = text,
            medias = medias
        )

        return kotlin.runCatching {
            bulletinBoardApi.apiV1BulletinBoardChannelIdMessagePost(
                channelId = channelId,
                bulletingBoardMessageParam = bulletingBoardMessageParam
            ).checkResponseBody()
        }
    }

    /**
     * 取得貼文
     *
     *  @param channelId 頻道id
     *  @param order 排序 (預設新到舊)
     *  @param fromSerialNumber 分頁序號
     */
    suspend fun getPost(
        channelId: String,
        order: OrderType = OrderType.latest,
        fromSerialNumber: Long? = null
    ): Result<BulletinboardMessagePaging> {
        return kotlin.runCatching {
            bulletinBoardApi.apiV1BulletinBoardChannelIdMessageGet(
                channelId = channelId,
                order = order,
                fromSerialNumber = fromSerialNumber
            ).checkResponseBody()
        }
    }

    /**
     * 取得 該貼文 留言
     *
     *  @param channelId 頻道id
     *  @param messageId 原始貼文id
     *  @param order 排序 (預設舊到新)
     *  @param fromSerialNumber 分頁序號
     */
    suspend fun getComments(
        channelId: String,
        messageId: String,
        order: OrderType = OrderType.oldest,
        fromSerialNumber: Long? = null
    ): Result<BulletinboardMessagePaging> {
        return kotlin.runCatching {
            bulletinBoardApi.apiV1BulletinBoardChannelIdMessageMessageIdCommentsGet(
                channelId = channelId,
                messageId = messageId,
                order = order,
                fromSerialNumber = fromSerialNumber
            ).checkResponseBody()
        }
    }

    /**
     * 取得 該留言 的回覆
     *
     *  @param channelId 頻道id
     *  @param commentId 原始留言id
     *  @param order 排序 (預設舊到新)
     *  @param fromSerialNumber 分頁序號
     */
    suspend fun getCommentReply(
        channelId: String,
        commentId: String,
        order: OrderType = OrderType.oldest,
        fromSerialNumber: Long? = null
    ): Result<BulletinboardMessagePaging> {
        return kotlin.runCatching {
            bulletinBoardApi.apiV1BulletinBoardChannelIdMessageMessageIdCommentsGet(
                channelId = channelId,
                messageId = commentId,
                order = order,
                fromSerialNumber = fromSerialNumber
            ).checkResponseBody()
        }
    }

    /**
     * 針對貼文 留言
     *
     *  @param channelId 頻道id
     *  @param messageId 原始貼文id
     *  @param text message
     *  @param attachment attach
     */
    suspend fun writeComment(
        channelId: String,
        messageId: String,
        text: String,
        attachment: List<Pair<AttachmentType, AttachmentInfoItem>>
    ): Result<BulletinboardMessage> {
        val medias = attachment.toUploadMedia(context)
        val bulletingBoardMessageParam = BulletingBoardMessageParam(
            text = text,
            medias = medias
        )

        return kotlin.runCatching {
            bulletinBoardApi.apiV1BulletinBoardChannelIdMessageMessageIdCommentPost(
                channelId = channelId,
                messageId = messageId,
                bulletingBoardMessageParam = bulletingBoardMessageParam
            ).checkResponseBody()
        }
    }
}