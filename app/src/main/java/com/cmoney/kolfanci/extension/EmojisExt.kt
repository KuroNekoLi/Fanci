package com.cmoney.kolfanci.extension

import com.cmoney.fanciapi.fanci.model.Emojis
import com.cmoney.fanciapi.fanci.model.IEmojiCount

fun Emojis.clickCount(emojiCount: Int, orgEmoji: IEmojiCount?): IEmojiCount? {
    return when (this) {
        Emojis.like -> orgEmoji?.copy(like = orgEmoji.like?.plus(emojiCount)?.coerceAtLeast(0))
        Emojis.dislike -> orgEmoji?.copy(
            dislike = orgEmoji.dislike?.plus(
                emojiCount
            )?.coerceAtLeast(0)
        )

        Emojis.laugh -> orgEmoji?.copy(laugh = orgEmoji.laugh?.plus(emojiCount)?.coerceAtLeast(0))
        Emojis.money -> orgEmoji?.copy(money = orgEmoji.money?.plus(emojiCount)?.coerceAtLeast(0))
        Emojis.shock -> orgEmoji?.copy(shock = orgEmoji.shock?.plus(emojiCount)?.coerceAtLeast(0))
        Emojis.cry -> orgEmoji?.copy(cry = orgEmoji.cry?.plus(emojiCount)?.coerceAtLeast(0))
        Emojis.think -> orgEmoji?.copy(think = orgEmoji.think?.plus(emojiCount)?.coerceAtLeast(0))
        Emojis.angry -> orgEmoji?.copy(angry = orgEmoji.angry?.plus(emojiCount)?.coerceAtLeast(0))
    }
}