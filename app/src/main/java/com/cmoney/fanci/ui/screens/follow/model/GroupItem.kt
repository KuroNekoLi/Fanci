package com.cmoney.fanci.ui.screens.follow.model

import android.os.Parcelable
import com.cmoney.fanciapi.fanci.model.Group
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupItem(val groupModel: Group, val isSelected: Boolean) : Parcelable