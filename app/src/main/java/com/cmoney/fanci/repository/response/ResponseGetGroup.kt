package com.cmoney.fanci.repository.response

data class ResponseGetGroup(
    val haveNextPage: Boolean,
    val items: List<GroupItem>,
    val nextWeight: Int
)