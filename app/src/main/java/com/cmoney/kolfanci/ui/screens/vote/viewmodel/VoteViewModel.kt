package com.cmoney.kolfanci.ui.screens.vote.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.IUserVoteInfo
import com.cmoney.fanciapi.fanci.model.IVotingOptionStatisticWithVoter
import com.cmoney.fanciapi.fanci.model.Voting
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.ChatMessageWrapper
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.cmoney.kolfanci.model.usecase.VoteUseCase
import com.cmoney.kolfanci.model.vote.VoteModel
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VoteViewModel(
    val context: Application,
    private val voteUseCase: VoteUseCase,
    private val groupUseCase: GroupUseCase
) :
    AndroidViewModel(context) {
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

    //投票結果
    private val _voteResultInfo =
        MutableStateFlow<List<IVotingOptionStatisticWithVoter>>(emptyList())
    val voteResultInfo = _voteResultInfo.asStateFlow()

    //完成結束投票
    private val _closeVoteSuccess = MutableStateFlow(false)
    val closeVoteSuccess = _closeVoteSuccess.asStateFlow()

    //投票者
    private val _voterGroupMember = MutableStateFlow<List<GroupMember>>(emptyList())
    val voterGroupMember = _voterGroupMember.asStateFlow()

    //貼文投票成功
    private val _postVoteSuccess = MutableStateFlow<BulletinboardMessage?>(null)
    val postVoteSuccess = _postVoteSuccess.asStateFlow()

    //聊天訊息投票成功
    private val _chatVoteSuccess = MutableStateFlow<ChatMessageWrapper?>(null)
    val chatVoteSuccess = _chatVoteSuccess.asStateFlow()

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
                    ""
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

    /**
     * 選擇 投票
     *
     * @param content 文本 object, 可能是投票/聊天
     * @param channelId 頻道id
     * @param votingId 投票id
     * @param choice 所選擇的項目ids
     */
    fun voteQuestion(
        content: Any?,
        channelId: String,
        votingId: String,
        choice: List<String>
    ) {
        KLog.i(TAG, "voteQuestion: channelId = $channelId, votingId = $votingId, choice = $choice")
        viewModelScope.launch {
            voteUseCase.choiceVote(
                channelId = channelId,
                votingId = votingId,
                choice = choice
            ).onSuccess {
                KLog.i(TAG, "voteQuestion onSuccess")

                content?.let {
                    //貼文
                    if (content is BulletinboardMessage) {
                        val newVoting = updateVotingModel(
                            votings = content.votings.orEmpty(),
                            votingId = votingId,
                            choice = choice
                        )

                        val newContent = content.copy(
                            votings = newVoting
                        )

                        _postVoteSuccess.update {
                            newContent
                        }

                    } else if (content is ChatMessageWrapper) {
                        val newVoting = updateVotingModel(
                            votings = content.message.votings.orEmpty(),
                            votingId = votingId,
                            choice = choice
                        )

                        val newContent = content.copy(
                            content.message.copy(
                                votings = newVoting
                            )
                        )

                        _chatVoteSuccess.update {
                            newContent
                        }
                    }
                }
            }.onFailure {
                KLog.e(TAG, it)
            }
        }
    }

    /**
     * 使用者投票之後,更新 Voting model 資料,
     *
     * @param votings 投票文
     * @param votingId 投票id
     * @param choice 所選擇的項目ids
     */
    private fun updateVotingModel(
        votings: List<Voting>,
        votingId: String,
        choice: List<String>
    ): List<Voting> {
        val newVoting = votings.map { voting ->
            //找出對應投票文
            if (voting.id == votingId) {
                //新的投票選項
                val newVotingOptionStatistics =
                    voting.votingOptionStatistics?.map { votingOptionStatistic ->
                        //找出對應的選項
                        if (choice.contains(votingOptionStatistic.optionId)) {
                            votingOptionStatistic.copy(
                                voteCount = votingOptionStatistic.voteCount?.plus(1)
                            )
                        } else {
                            votingOptionStatistic
                        }
                    }

                //新的總投票數
                val newVotersCount = voting.votersCount?.plus(choice.size)

                //已經投過
                val newUserVote = IUserVoteInfo(selectedOptions = choice)

                voting.copy(
                    votingOptionStatistics = newVotingOptionStatistics,
                    votersCount = newVotersCount,
                    userVote = newUserVote
                )
            } else {
                voting
            }
        }
        return newVoting
    }

    /**
     * 取得 投票 目前結果
     */
    fun fetchVoteChoiceInfo(
        votingId: String,
        channelId: String
    ) {
        KLog.i(TAG, "fetchVoteChoiceMember")
        viewModelScope.launch {
            voteUseCase.summaryVote(
                channelId = channelId,
                votingId = votingId
            ).onSuccess {
                _voteResultInfo.value = it
            }.onFailure {
                KLog.e(TAG, it)
            }
        }
    }

    /**
     * 結束投票
     *
     * @param votingId 投票 id
     * @param channelId 頻道 id
     */
    fun closeVote(
        votingId: String,
        channelId: String
    ) {
        KLog.i(TAG, "closeVote")
        viewModelScope.launch {
            voteUseCase.closeVote(
                votingId = votingId,
                channelId = channelId
            ).onSuccess {
                _closeVoteSuccess.value = true
            }
        }
    }

    /**
     * 取得 投票選項的用戶清單
     *
     * @param groupId 社團 id
     * @param voterIds 會員 id list
     */
    fun getChoiceGroupMember(groupId: String, voterIds: List<String>) {
        KLog.i(TAG, "getChoiceGroupMember:$groupId")
        viewModelScope.launch {
            groupUseCase.getGroupMembers(
                groupId = groupId,
                userIds = voterIds
            ).onSuccess {
                _voterGroupMember.value = it
            }.onFailure {
                KLog.e(TAG, it)
            }
        }
    }

    fun postVoteSuccessDone() {
        KLog.i(TAG, "postVoteSuccessDone")
        _postVoteSuccess.update { null }
    }

    fun chatVoteSuccessDone() {
        KLog.i(TAG, "postVoteSuccessDone")
        _chatVoteSuccess.update { null }
    }
}