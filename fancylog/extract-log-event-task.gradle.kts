abstract class ExtractLogEventTask : DefaultTask() {
    @TaskAction
    fun invoke() {
        val targetFile = File("fancylog/events", "log_event_20230714.csv")
        val outputFile = File("fancylog/src/main/java/com/cmoney/fancylog", "Page.kt")
        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }
        outputFile.outputStream().use { outputStream ->
            targetFile.inputStream().use { inputStream ->
                val indent = "    "
                val writer = outputStream.bufferedWriter()
                writer.write("package com.cmoney.fancylog")
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
