package com.cmoney.fanci.ui.screens.follow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.extension.EmptyBodyException
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanci.ui.screens.follow.model.GroupItem
import com.cmoney.fanciapi.fanci.model.Group
import com.socks.library.KLog
import kotlinx.coroutines.launch

class FollowViewModel(private val groupUseCase: GroupUseCase) : ViewModel() {
    private val TAG = FollowViewModel::class.java.simpleName

    private val _followData = MutableLiveData<Group>()
    val followData: LiveData<Group> = _followData

    private val _myGroupList = MutableLiveData<List<GroupItem>>()
    val myGroupList: LiveData<List<GroupItem>> = _myGroupList

    private val _groupList = MutableLiveData<List<Group>>()
    val groupList: LiveData<List<Group>> = _groupList

    init {
        fetchMyGroup()
    }

    /**
     * 取得 我的群組
     */
    private fun fetchMyGroup() {
        viewModelScope.launch {
            groupUseCase.groupToSelectGroupItem().fold({
                if (it.isNotEmpty()) {
                    //所有群組
                    _myGroupList.value = it
                    _followData.value = it.first().groupModel
                } else {
                    fetchAllGroupList()
                }
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 當沒有 社團的時候, 取得目前 所有群組
     */
    private fun fetchAllGroupList() {
        viewModelScope.launch {
            groupUseCase.getGroup().fold({
                _groupList.value = it.items
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 點擊 側邊 切換 群組
     */
    fun groupItemClick(groupItem: GroupItem) {
        _followData.value = groupItem.groupModel
        _myGroupList.value = _myGroupList.value?.map {
            return@map if (it.groupModel == groupItem.groupModel) {
                it.copy(isSelected = true)
            } else {
                it.copy(isSelected = false)
            }
        }
    }

    /**
     * 加入社團
     */
    fun joinGroup(group: Group) {
        KLog.i(TAG, "joinGroup:$group")
        viewModelScope.launch {
            groupUseCase.joinGroup(group).fold({
                fetchMyGroup()
            }, {
                if (it is EmptyBodyException) {
                    fetchMyGroup()
                }
                else {
                    KLog.e(TAG, it)
                }
            })
        }
    }
}