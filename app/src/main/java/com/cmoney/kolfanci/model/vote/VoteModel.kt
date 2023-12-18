package com.cmoney.kolfanci.model.vote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 投票 model
 *
 * @param id
 * @param question 問題
 * @param choice 選項清單
 * @param isSingleChoice 是否為單選題, 反之為多選
 */
@Parcelize
data class VoteModel(
    val id: String,
    val question: String,
    val choice: List<String>,
    val isSingleChoice: Boolean = true
): Parcelable
