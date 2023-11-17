package com.cmoney.kolfanci.ui.screens.vote.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.vote.VoteModel
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VoteViewModel(val context: Application) : AndroidViewModel(context) {
    private val TAG = VoteViewModel::class.java.simpleName

    //問題
    private val _question = MutableStateFlow("")
    val question = _question.asStateFlow()

    //選擇題清單, default:至少有2個選項
    private val _choice = MutableStateFlow(listOf("", ""))
    val choice = _choice.asStateFlow()

    //是否為單選題, 反之為多選
    private val _isSingleChoice = MutableStateFlow(true)
    val isSingleChoice = _isSingleChoice.asStateFlow()

    //toast message
    private val _toast = MutableSharedFlow<String>()
    val toast = _toast.asSharedFlow()

    //建立投票 model
    private val _voteModel = MutableStateFlow<VoteModel?>(null)
    val voteModel = _voteModel.asStateFlow()

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

    /**
     * 點擊建立 投票, 檢查輸入 是否符合規範
     * 問題不為空
     * 至少有二個項
     *
     * @param question 問題
     * @param choice 選項
     * @param isSingleChoice 是否為單選題
     * @param id 識別是否為更新
     */
    fun onConfirmClick(
        question: String,
        choice: List<String>,
        isSingleChoice: Boolean,
        id: String? = null
    ) {
        viewModelScope.launch {
            if (question.isEmpty()) {
                _toast.emit(context.getString(R.string.vote_question_error))
                return@launch
            }

            val choiceSize = choice.filter {
                it.isNotEmpty()
            }.size

            if (choiceSize < 2) {
                _toast.emit(context.getString(R.string.vote_choice_error))
                return@launch
            }

            _voteModel.value = VoteModel(
                id = (if (id.isNullOrEmpty()) {
                    System.currentTimeMillis().toString()
                } else id),
                question = question,
                choice = choice,
                isSingleChoice = isSingleChoice
            )
        }
    }

    /**
     * 設定 初始化吃資料
     */
    fun setVoteModel(voteModel: VoteModel) {
        KLog.i(TAG, "setVoteModel:$voteModel")
        viewModelScope.launch {
            _question.value = voteModel.question
            _choice.value = voteModel.choice
            _isSingleChoice.value = voteModel.isSingleChoice
        }
    }
}