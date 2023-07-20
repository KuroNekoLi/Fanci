package com.cmoney.kolfanci.devtools.ui.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

/**
 * 螢幕截圖目標
 */
interface ScreenshotTarget {
    /**
     * 位於哪個資料夾下(相對路徑), 空字串的話則直接儲存於預設的資料夾下
     */
    val relativePath: String

    /**
     * 檔案名稱(會加上時間戳記後綴)
     *
     * ex: XXX-millisTime.jpg
     */
    val name: String

    /**
     * 顯示內容
     */
    val content: @Composable () -> Unit
}

/**
 * 創建螢幕截圖目標
 *
 * @param relativePath 檔案儲存相對路徑
 * @param name 檔案名稱
 * @param content 顯示內容
 * @return 螢幕截圖目標
 */
fun ScreenshotTarget(
    relativePath: String = "",
    name: String,
    content: @Composable () -> Unit
): ScreenshotTarget {
    return ScreenshotTargetImpl(relativePath = relativePath, name = name, content = content)
}

/**
 * 螢幕截圖目標內部實作
 *
 * @property relativePath 檔案路徑
 * @property name 檔案名稱
 * @property content 顯示內容
 */
@Immutable
private data class ScreenshotTargetImpl(
    override val relativePath: String,
    override val name: String,
    override val content: @Composable () -> Unit
): ScreenshotTarget
