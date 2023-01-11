package com.cmoney.fanci.ui.screens.group.search.apply.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.extension.EmptyBodyException
import com.cmoney.fanci.model.usecase.GroupApplyUseCase
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanciapi.fanci.model.GroupRequirementAnswer
import com.cmoney.fanciapi.fanci.model.IGroupRequirementQuestion
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class UiState(
    val questionList: List<IGroupRequirementQuestion>? = null, //題目
    val answerList: List<String>? = null,       //作答清單
    val warning: String = "",                   //錯誤訊息
    val isComplete: Boolean = false,            //是否成功 送出
    val isFromBackCheck: Boolean = false        //是否為返回時的檢查
)

class ApplyForGroupViewModel(
    val groupUseCase: GroupUseCase,
    val groupApplyUseCase: GroupApplyUseCase
) : ViewModel() {

    private val TAG = ApplyForGroupViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    /**
     * 抓取所有題目
     * @param groupId 社團 id
     */
    fun fetchAllQuestion(groupId: String) {
        viewModelScope.launch {
            groupUseCase.fetchGroupRequirement(groupId = groupId).fold({
                uiState = uiState.copy(
                    questionList = it.questions,
                    answerList = it.questions?.map { "" }
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 輸入作答
     *
     * @param index 第幾題
     * @param answer 作答
     */
    fun editAnswer(index: Int, answer: String) {
        KLog.i(TAG, "editAnswer:$index, $answer")
        uiState.answerList?.let { answerList ->
            if (index < answerList.size) {
                val newList = answerList.toMutableList()
                newList[index] = answer
                uiState = uiState.copy(
                    answerList = newList
                )
            }
        }
    }

    /**
     * 加入申請
     */
    fun onApply(groupId: String) {
        KLog.i(TAG, "onApply click.")
        uiState.answerList?.let { answerList ->
            val isExistsNoAnswer = answerList.any {
                it.isEmpty()
            }

            if (isExistsNoAnswer) {
                warning("尚有題目未完成")
                return
            }

            //ready to send, check size is the same
            viewModelScope.launch {
                uiState.questionList?.let { question ->
                    if (answerList.size == question.size) {
                        val requestParam = answerList.mapIndexed { index, answer ->
                            GroupRequirementAnswer(
                                question = question[index].question,
                                answer = answer
                            )
                        }

                        groupApplyUseCase.joinGroupWithQuestion(
                            groupId = groupId,
                            groupRequirementAnswer = requestParam
                        ).fold({
                            uiState = uiState.copy(
                                isComplete = true
                            )
                        }, {
                            if (it is EmptyBodyException) {
                                uiState = uiState.copy(
                                    isComplete = true
                                )
                            } else {
                                KLog.e(TAG, it)
                            }
                        })
                    }
                }
            }
        }
    }

    private fun warning(text: String, isFromBackCheck: Boolean = false) {
        uiState = uiState.copy(
            warning = text,
            isFromBackCheck = isFromBackCheck
        )
    }

    fun dismissWarning() {
        uiState = uiState.copy(
            warning = "",
            isFromBackCheck = false
        )
    }

    /**
     * 檢查 答題狀態
     */
    fun checkAnswer() {
        KLog.i(TAG, "checkAnswer")
        uiState.answerList?.let { answerList ->
            val isExistsNoAnswer = answerList.any {
                it.isEmpty()
            }

            if (isExistsNoAnswer) {
                warning("答案未送出", isFromBackCheck = true)
            }
        }
    }

}