package com.cmoney.fanci.ui.screens.follow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.model.GroupModel
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanci.ui.screens.follow.model.GroupItem
import kotlinx.coroutines.launch

class FollowViewModel(private val groupUseCase: GroupUseCase) : ViewModel() {
    private val TAG = FollowViewModel::class.java.simpleName

    private val _followData = MutableLiveData<GroupModel>()
    val followData: LiveData<GroupModel> = _followData

    private val _groupList = MutableLiveData<List<GroupItem>>()
    val groupList: LiveData<List<GroupItem>> = _groupList

    init {
        viewModelScope.launch {
            val groupList = groupUseCase.getGroupMockData()
            _followData.value = groupList.first()
            _groupList.value = groupList.mapIndexed { index, groupModel ->
                GroupItem(
                    groupModel = groupModel,
                    isSelected = index == 0
                )
            }
        }
    }

    /**
     * 點擊 側邊 切換 群組
     */
    fun groupItemClick(groupItem: GroupItem) {
        _followData.value = groupItem.groupModel
        _groupList.value = _groupList.value?.map {
            return@map if (it.groupModel == groupItem.groupModel) {
                it.copy(isSelected = true)
            }
            else {
               it.copy(isSelected = false)
            }
        }
    }

}