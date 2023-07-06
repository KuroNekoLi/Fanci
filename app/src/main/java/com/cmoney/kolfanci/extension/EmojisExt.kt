package com.cmoney.kolfanci.extension

import com.cmoney.fanciapi.fanci.model.Emojis
import com.cmoney.fanciapi.fanci.model.IEmojiCount

fun Emojis.clickCount(emojiCount: Int, orgEmoji: IEmojiCount?): IEmojiCount? {
    return when (this) {
        Emojis.like -> orgEmoji?.copy(like = orgEmoji.like?.plus(emojiCount))
        Emojis.dislike -> orgEmoji?.copy(
            dislike = orgEmoji.dislike?.plus(
                emojiCount
            )
        )

        Emojis.laugh -> orgEmoji?.copy(laugh = orgEmoji.laugh?.plus(emojiCount))
        Emojis.money -> orgEmoji?.copy(money = orgEmoji.money?.plus(emojiCount))
        Emojis.shock -> orgEmoji?.copy(shock = orgEmoji.shock?.plus(emojiCount))
        Emojis.cry -> orgEmoji?.copy(cry = orgEmoji.cry?.plus(emojiCount))
        Emojis.think -> orgEmoji?.copy(think = orgEmoji.think?.plus(emojiCount))
        Emojis.angry -> orgEmoji?.copy(angry = orgEmoji.angry?.plus(emojiCount))
    }
}