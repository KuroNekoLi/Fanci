package com.cmoney.fanci.model

data class GroupModel(
    val groupId: String,
    val name: String,
    val description: String,
    val coverImageUrl: String,          //背景大圖
    val thumbnailImageUrl: String,      //小圖
    val categories: List<Category>      //群組 - 類別
){
    /**
     * 群組 -> 類別
     */
    data class Category(
        val categoryId: String,
        val groupId: String,
        val name: String,
        val channels: List<Channel>
    )

    /**
     * 群組 -> 類別 -> 頻道
     */
    data class Channel(
        val channelId: String,
        val creatorId: String,
        val groupId: String,
        val name: String
    )
}
