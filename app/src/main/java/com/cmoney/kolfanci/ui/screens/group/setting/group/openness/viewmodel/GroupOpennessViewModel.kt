package com.cmoney.kolfanci.ui.screens.group.setting.group.openness.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.kolfanci.extension.EmptyBodyException
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.cmoney.fanciapi.fanci.model.Group
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class UiState(
    val groupQuestionList: List<String>? = null,
    val isNeedApproval: Boolean = true,
    val isEditMode: Boolean = false,
    val orgQuestion: String = "",
    val saveComplete: Boolean = false,
    val isFirstFetchQuestion: Boolean = true
)

class GroupOpennessViewModel(val group: Group, val groupUseCase: GroupUseCase) : ViewModel() {
    private val TAG = GroupOpennessViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    init {
        uiState = uiState.copy(
            isNeedApproval = group.isNeedApproval ?: false
        )
    }

    /**
     * 抓取 加入社團 題目
     */
    fun fetchGroupQuestion(group: Group) {
        KLog.i(TAG, "fetchGroupQuestion:$group")
        viewModelScope.launch {
            groupUseCase.fetchGroupRequirement(
                groupId = group.id.orEmpty()
            ).fold({
                uiState = uiState.copy(
                    groupQuestionList = it.questions?.map { requirementQuestion ->
                        requirementQuestion.question.orEmpty()
                    },
                    isFirstFetchQuestion = false
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 新增 問題
     *
     * @param question 問題
     */
    fun addQuestion(question: String) {
        KLog.i(TAG, "addQuestion:$question")
        val newList = uiState.groupQuestionList.orEmpty().toMutableList()
        newList.add(question)
        uiState = uiState.copy(
            groupQuestionList = newList
        )
    }

    /**
     * 點擊 公開 or 不公開
     */
    fun onSwitchClick(checked: Boolean) {
        KLog.i(TAG, "onSwitchClick:$checked")
        uiState = uiState.copy(
            isNeedApproval = checked
        )
    }

    /**
     * 移除 問提
     */
    fun removeQuestion(question: String) {
        KLog.i(TAG, "removeQuestion:$question")
        val filterQuestion = uiState.groupQuestionList?.filter {
            it != question
        }
        uiState = uiState.copy(
            groupQuestionList = filterQuestion
        )
    }

    /**
     * 編輯 問題
     * @param question 原始問題
     * @param update 更新問題
     */
    fun editQuestion(question: String, update: String) {
        KLog.i(TAG, "editQuestion:$question to $update")
        val newList = uiState.groupQuestionList?.map {
            if (it == question) {
                update
            } else {
                it
            }
        }
        uiState = uiState.copy(
            groupQuestionList = newList,
            isEditMode = false
        )
    }

    /**
     * 打開 編輯模式
     */
    fun openEditMode(question: String) {
        KLog.i(TAG, "openEditMode:$question")
        uiState = uiState.copy(
            isEditMode = true,
            orgQuestion = question
        )
    }

    /**
     * 儲存 設定結果
     * @param group 社團
     */
    fun onSave(group: Group) {
        KLog.i(TAG, "onSave:$uiState")
        val groupId = group.id.orEmpty()
        viewModelScope.launch {
            //公開 or 不公開
            val isNeedApproval = uiState.isNeedApproval

            //設定 公開度 to server
            groupUseCase.setGroupNeedApproval(
                groupId = groupId,
                isNeedApproval = isNeedApproval
            ).fold({
            }, {
                if (it is EmptyBodyException) {
                    //設定 問題清單
                    if (isNeedApproval) {
                        //問題清單
                        val questionList = uiState.groupQuestionList.orEmpty()
                        groupUseCase.setGroupRequirementQuestion(
                            groupId = groupId,
                            question = questionList
                        ).fold({
                            uiState = uiState.copy(saveComplete = true)
                        }, {
                            KLog.e(TAG, it)
                        })
                    } else {
                        uiState = uiState.copy(saveComplete = true)
                    }
                } else {
                    KLog.e(TAG, it)
                }
            })
        }

    }
}