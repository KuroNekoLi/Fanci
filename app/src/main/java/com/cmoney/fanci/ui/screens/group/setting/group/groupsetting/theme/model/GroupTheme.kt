package com.cmoney.fanci.ui.screens.group.setting.group.groupsetting.theme.model

import com.cmoney.fanci.ui.theme.FanciColor

data class GroupTheme(
    val id: String,
    val isSelected: Boolean,    //是否為目前選擇的主題
    val theme: FanciColor,      //色卡
    val name: String,           //主題名稱
    val preview: List<String>   //預覽
)
