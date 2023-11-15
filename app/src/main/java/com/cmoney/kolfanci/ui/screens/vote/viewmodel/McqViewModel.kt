package com.cmoney.kolfanci.ui.screens.vote.viewmodel

import androidx.lifecycle.ViewModel
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class McqViewModel : ViewModel() {
    private val TAG = McqViewModel::class.java.simpleName

    //問題
    private val _question = MutableStateFlow("")
    val question = _question.asStateFlow()

    //選擇題清單, default:至少有2個選項
    private val _choice = MutableStateFlow(listOf("", ""))
    val choice = _choice.asStateFlow()

    //是否為單選題, 反之為多選
    private val _isSingleChoice = MutableStateFlow(true)
    val isSingleChoice = _isSingleChoice.asStateFlow()

    //最大選項數量
    private val MAX_COICE_COUNT = 5

    /**
     * 設定問題
     */
    fun setQuestion(question: String) {
        KLog.i(TAG, "setQuestion:$question")
        _question.value = question
    }

    /**
     * 新增 問題選項
     */
    fun addEmptyChoice() {
        KLog.i(TAG, "addEmptyQuestion")
        if (_choice.value.size < MAX_COICE_COUNT) {
            _choice.update {
                val newList = it.toMutableList()
                newList.add("")
                newList
            }
        }
    }

    /**
     * 設定 問題
     */
    fun setChoiceQuestion(index: Int, question: String) {
        _choice.update {
            it.toMutableList().let { list ->
                if (index < list.size) {
                    list[index] = question
                }
                list
            }
        }
    }

    /**
     * 移除 選項
     */
    fun removeChoice(index: Int) {
        _choice.update {
            it.toMutableList().let { list ->
                if (index < list.size) {
                    list.removeAt(index)
                }
                list
            }
        }
    }

    /**
     * 點擊 單選
     */
    fun onSingleChoiceClick() {
        KLog.i(TAG, "onSingleChoiceClick")
        _isSingleChoice.value = true
    }

    /**
     *  點擊 多選
     */
    fun onMultiChoiceClick() {
        KLog.i(TAG, "onMultiChoiceClick")
        _isSingleChoice.value = false
    }
}