package com.cmoney.fanci.ui.screens.follow.viewmodel

import android.content.res.Configuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
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
import com.cmoney.fanci.extension.dp
import com.cmoney.fanci.extension.px

data class FollowUiState(
    val spaceHeight: Int = 190.px,  //滑動時的間距
    val imageOffset: Int = 0,       //滑動時圖片Offset
    val visibleAvatar: Boolean = false,  //是否要顯示 大頭貼
    val lazyColumnScrollEnabled: Boolean = false    //LazyColumn 是否可以滑動
)

class FollowViewModel(private val groupUseCase: GroupUseCase) : ViewModel() {
    private val TAG = FollowViewModel::class.java.simpleName

//    private val _followData = MutableLiveData<Group>()
//    val followData: LiveData<Group> = _followData

    private val _myGroupList = MutableLiveData<List<GroupItem>>()
    val myGroupList: LiveData<List<GroupItem>> = _myGroupList

    private val _groupList = MutableLiveData<List<Group>>()
    val groupList: LiveData<List<Group>> = _groupList

    var uiState by mutableStateOf(FollowUiState())
        private set

    init {
        fetchMyGroup()
    }

    /**
     * 取得 我的群組
     */
    private fun fetchMyGroup() {
        KLog.i(TAG, "fetchMyGroup")
        viewModelScope.launch {
            groupUseCase.groupToSelectGroupItem().fold({
                if (it.isNotEmpty()) {
                    //所有群組
                    _myGroupList.value = it
//                    _followData.value = it.first().groupModel
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
//        _followData.value = groupItem.groupModel
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
                } else {
                    KLog.e(TAG, it)
                }
            })
        }
    }


    /**
     * 滑動位移量, 來調整 top space
     *
     * @param offset 位移量
     */
    fun scrollOffset(offset: Float, localDensity: Density, configuration: Configuration) {
        val maxSpaceUpPx = 190.px
        val newSpaceOffset = uiState.spaceHeight + offset

        val screenWidth = configuration.screenWidthDp.px
        // ToolBar 最大向上位移量
        val maxUpPx = screenWidth + 10
        // ToolBar 最小向上位移量
        val minUpPx = 0f
        //位移偏差值
        val offsetVariable = 2f
        val newOffset = uiState.imageOffset + offset * offsetVariable

        //是否顯示 大頭貼
        val isVisibleAvatar = (uiState.spaceHeight <= 10f)

        uiState = uiState.copy(
            spaceHeight = newSpaceOffset.coerceIn(0f, maxSpaceUpPx.toFloat()).toInt(),
            imageOffset = newOffset.coerceIn(-maxUpPx.toFloat(), -minUpPx).toInt(),
            visibleAvatar = isVisibleAvatar,
            lazyColumnScrollEnabled = isVisibleAvatar
        )

    }

    /**
     * 滑動到最上方 觸發
     */
    fun lazyColumnAtTop() {
        uiState = uiState.copy(
            lazyColumnScrollEnabled = false,
            visibleAvatar = false
        )
    }
}