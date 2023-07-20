abstract class ExtractLogEventTask : DefaultTask() {
    @TaskAction
    fun invoke() {
        val targetFile = File("fancylog/events", "log_event_20230720.csv")
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
        val outputFile = File(parentDirectoryFile, "Page.kt")
        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }
        outputFile.outputStream().use { outputStream ->
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
                    if (row != null) {
                        val columns = row.split(",")
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
    }
}

tasks.register<ExtractLogEventTask>("generateLogEvent")
