package com.cmoney.fanci.repository.response

data class GroupItem(
    val categories: List<Category>,
    val coverImageUrl: String,
    val createUnixTime: Int,
    val creatorId: String,
    val description: String,
    val groupId: String,
    val isNeedApproval: Boolean,
    val name: String,
    val thumbnailImageUrl: String,
    val updateUnixTime: Int,
    val weight: Int
) {
    data class Category(
        val categoryId: String,
        val channels: List<Channel>,
        val createUnixTime: Int,
        val creatorId: String,
        val groupId: String,
        val isDeleted: Boolean,
        val isNeedApproval: Boolean,
        val name: String,
        val orderIndex: Int,
        val updateUnixTime: Int,
        val weight: Int
    ) {
        data class Channel(
            val category: String,
            val channelId: String,
            val channelType: String,
            val createUnixTime: Int,
            val creatorId: String,
            val groupId: String,
            val isNeedApproval: Boolean,
            val name: String,
            val updateUnixTime: Int,
            val weight: Int
        )
    }
}