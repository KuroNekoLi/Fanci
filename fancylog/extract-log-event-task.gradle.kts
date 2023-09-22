abstract class ExtractLogEventTask : DefaultTask() {
    @TaskAction
    fun invoke() {
        val targetFile = File("fancylog/events", "log_event_20230921.csv")
        val packageName = "com.cmoney.fancylog.model.data"
        val rootDirectory = "fancylog/src/main/java"
        val rootPackage = "com.cmoney.fancylog"
        val directoryPool = packageName.split(".")
        var index = 0
        var parentDirectoryFile = File("$rootDirectory/${rootDirectory.replace('.', '/')}")
        while (index < directoryPool.size) {
            val subDirectory = directoryPool.subList(0, index)
                .joinToString("/")
            val parentParentDirectoryPath = "$rootDirectory" + if (subDirectory.isNotEmpty()) {
                "/$subDirectory"
            } else {
                subDirectory
            }
            val parentName = directoryPool[index]
            println(parentParentDirectoryPath)
            println(parentName)
            parentDirectoryFile = File(parentParentDirectoryPath, parentName)
            parentDirectoryFile.mkdir()
            index++
        }
        // 創建 Page.kt
        val pageOutputFile = File(parentDirectoryFile, "Page.kt")
        if (!pageOutputFile.exists()) {
            pageOutputFile.createNewFile()
        }
        pageOutputFile.outputStream().use { outputStream ->
            targetFile.inputStream().use { inputStream ->
                val indent = "    "
                val writer = outputStream.bufferedWriter()
                writer.write("package $packageName")
                writer.newLine()
                writer.newLine()
                writer.write("/**")
                writer.newLine()
                writer.write(" * 頁面事件")
                writer.newLine()
                writer.write(" * ")
                writer.newLine()
                writer.write(" * @property eventName 事件名稱")
                writer.newLine()
                writer.write(" */")
                writer.newLine()
                writer.write("sealed class Page(val eventName: String) {")
                writer.newLine()
                writer.flush()
                val reader = inputStream.bufferedReader()
                // skip first
                reader.readLine()
                var row: String? = null
                do {
                    row = reader.readLine()
                    val nowRow = row
                    if (nowRow != null) {
                        val columns = nowRow.split(",")
                        // 5 中文事件名稱, 6 英文事件名稱
                        val chineseName = columns.getOrNull(5)
                        val englishOriginName = columns.getOrNull(6)
                        if (
                            chineseName != null &&
                            englishOriginName != null &&
                            (englishOriginName.endsWith("_viewed") || englishOriginName.endsWith("_Viewed"))
                        ) {
                            val englishName = englishOriginName.removeSuffix("_viewed")
                                .removeSuffix("_Viewed")
                            writer.write("$indent/**")
                            writer.newLine()
                            writer.write("$indent * $chineseName")
                            writer.newLine()
                            writer.write("$indent */")
                            writer.newLine()
                            val englishClassName = englishName.split(".")
                                .joinToString(separator = "") { split ->
                                    split.capitalize()
                                }
                            writer.write("${indent}object $englishClassName : Page(eventName = \"$englishName\")")
                            writer.newLine()
                            writer.newLine()
                            writer.flush()
                        }
                    }
                } while (row != null)
                writer.write("}")
                writer.newLine()
                writer.flush()
            }
        }
        // 創建 Clicked.kt
        val clickedOutputFile = File(parentDirectoryFile, "Clicked.kt")
        if (!clickedOutputFile.exists()) {
            clickedOutputFile.createNewFile()
        }
        clickedOutputFile.outputStream().use { outputStream ->
            targetFile.inputStream().use { inputStream ->
                val indent = "    "
                val writer = outputStream.bufferedWriter()
                writer.write("package $packageName")
                writer.newLine()
                writer.newLine()
                writer.write("import android.os.Parcelable")
                writer.newLine()
                writer.write("import kotlinx.parcelize.Parcelize")
                writer.newLine()
                writer.newLine()
                writer.write("/**")
                writer.newLine()
                writer.write(" * 點擊事件")
                writer.newLine()
                writer.write(" * ")
                writer.newLine()
                writer.write(" * @property eventName 事件名稱")
                writer.newLine()
                writer.write(" */")
                writer.newLine()
                writer.write("sealed class Clicked(val eventName: String) : Parcelable {")
                writer.newLine()
                writer.flush()
                val reader = inputStream.bufferedReader()
                // skip first
                reader.readLine()
                var row: String? = null
                do {
                    row = reader.readLine()
                    val nowRow = row
                    if (nowRow != null) {
                        val columns = nowRow.split(",")
                        // 5 中文事件名稱, 6 英文事件名稱
                        val chineseName = columns.getOrNull(5)
                        val englishOriginName = columns.getOrNull(6)
                        if (
                            chineseName != null &&
                            englishOriginName != null &&
                            (englishOriginName.endsWith("_clicked") || englishOriginName.endsWith("_Clicked"))
                        ) {
                            val englishName = englishOriginName.removeSuffix("_clicked")
                                .removeSuffix("_Clicked")
                            writer.write("$indent/**")
                            writer.newLine()
                            writer.write("$indent * $chineseName")
                            writer.newLine()
                            writer.write("$indent */")
                            writer.newLine()
                            writer.write("$indent@Parcelize")
                            writer.newLine()
                            val englishClassName = englishName.split(".", "_")
                                .joinToString(separator = "") { split ->
                                    split.capitalize()
                                }
                            writer.write("${indent}object $englishClassName : Clicked(eventName = \"$englishName\")")
                            writer.newLine()
                            writer.newLine()
                            writer.flush()
                        }
                    }
                } while (row != null)
                writer.write("}")
                writer.newLine()
                writer.flush()
            }
        }
        // 創建 From.kt
        val fromOutputFile = File(parentDirectoryFile, "From.kt")
        if (!fromOutputFile.exists()) {
            fromOutputFile.createNewFile()
        }
        fromOutputFile.outputStream().use { outputStream ->
            targetFile.inputStream().use { inputStream ->
                val indent = "    "
                val writer = outputStream.bufferedWriter()
                writer.write("package $packageName")
                writer.newLine()
                writer.newLine()
                writer.write("import android.os.Parcelable")
                writer.newLine()
                writer.write("import kotlinx.parcelize.Parcelize")
                writer.newLine()
                writer.newLine()
                writer.write("/**")
                writer.newLine()
                writer.write(" * 事件參數 From")
                writer.newLine()
                writer.write(" * ")
                writer.newLine()
                writer.write(" * @property parameterName 參數名稱")
                writer.newLine()
                writer.write(" */")
                writer.newLine()
                writer.write("sealed class From(val parameterName: String) : Parcelable {")
                writer.newLine()
                writer.write("${indent}fun asParameters(): Map<String, String> {")
                writer.newLine()
                writer.write("${indent}${indent}return mapOf(\"from\" to parameterName)")
                writer.newLine()
                writer.write("${indent}}")
                writer.newLine()
                writer.newLine()
                val reader = inputStream.bufferedReader()
                // skip first
                reader.readLine()
                var row: String? = null
                var argumentName: String? = null
                val fromParameterSet = mutableSetOf<String>()
                do {
                    row = reader.readLine()
                    val nowRow = row
                    if (nowRow != null) {
                        val columns = nowRow.split(",")
                        // 7 參數群組名稱 8 中文事件名稱, 9 英文事件名稱
                        val nowArgumentName = columns.getOrNull(7)
                        if (nowArgumentName != null && nowArgumentName.isNotEmpty()) {
                            argumentName = nowArgumentName
                        }
                        val chineseName = columns.getOrNull(8)
                        val englishName = columns.getOrNull(9)
                        if (chineseName != null && englishName != null && englishName.isNotEmpty() && !fromParameterSet.contains(englishName) && argumentName == "from") {
                            fromParameterSet.add(englishName)
                            writer.write("$indent/**")
                            writer.newLine()
                            writer.write("$indent * $chineseName")
                            writer.newLine()
                            writer.write("$indent */")
                            writer.newLine()
                            writer.write("$indent@Parcelize")
                            writer.newLine()
                            val englishClassName = englishName.split(".", "_")
                                .joinToString(separator = "") { split ->
                                    split.capitalize()
                                }
                            writer.write("${indent}object $englishClassName : From(parameterName = \"$englishName\")")
                            writer.newLine()
                            writer.newLine()
                            writer.flush()
                        }
                    }
                } while (row != null)
                writer.write("}")
                writer.newLine()
                writer.flush()
            }
        }
    }
}

tasks.register<ExtractLogEventTask>("generateLogEvent")
