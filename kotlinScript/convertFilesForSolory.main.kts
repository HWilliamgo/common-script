#!/usr/bin/env kotlin

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

// 唯一变量，每次执行改这里的目录名字
val inputFileName = "元素少，有计划"


val outputParentDir = File("/Users/HWilliam/Desktop/converFileForSoloryOutputDir")
val inputDir = File("/Users/HWilliam/Desktop/$inputFileName")
val outputDir = File(outputParentDir, inputDir.name)

if (outputDir.exists()) {
    outputDir.deleteRecursively()
}
outputDir.mkdirs()

inputDir.listFiles()?.forEach { eachFile ->
    if (eachFile.name.endsWith(".docx")){
        val fileNameNoSuffix = eachFile.name.substringBefore(".")
        val cmd="pandoc ${eachFile.absolutePath} -o ${outputDir}/${fileNameNoSuffix}.txt"
        println(cmd)
        runCommands(cmd)
    }
}

////////////////////////////////////////////////////
//工具函数集合
////////////////////////////////////////////////////
/**
 * 打zip包
 * [inputDirectory]输入目录文件
 * [fileNamesToFilter] 要过滤掉的文件名
 */
fun makeZip(inputDirectory: File, outputZipFile: File, fileNamesToFilter: List<String>) {
    ZipOutputStream(BufferedOutputStream(FileOutputStream(outputZipFile))).use { zos ->
        inputDirectory.walkTopDown().forEach { file ->
            for (namesToFilter in fileNamesToFilter) {
                if (file.absolutePath.contains(namesToFilter)) {
                    return@forEach
                }
            }
            val zipFileName = file.absolutePath.removePrefix(inputDirectory.absolutePath).removePrefix("/")
            val entry = ZipEntry("$zipFileName${(if (file.isDirectory) "/" else "")}")
            zos.putNextEntry(entry)
            if (file.isFile) {
                file.inputStream().copyTo(zos)
            }
        }
    }
}

// <editor-fold defaultstate="collapsed" desc="cmd通用执行工具函数">
fun getPwd(): String {
    val cmd = "pwd"
    val runtime = Runtime.getRuntime()
    val process = runtime.exec(cmd)
    val br = BufferedReader(InputStreamReader(process.inputStream))
    var message: String = ""
    while (true) {
        message = br.readLine() ?: ""
        if (!message.isEmpty()) {
            break
        }
    }
    return message
}

fun runCommands(commands: String, dir: String? = null): String {
    val file = if (dir != null) {
        File(dir)
    } else {
        null
    }
    val process = Runtime.getRuntime().exec(commands, null, file)
    var result = ""
    val reader = process.inputStream.bufferedReader()
    while (process.isAlive) {
        var meetEmpty: Boolean = false
        do {
            val line: String? = reader.readLine()
            meetEmpty = line == null
            if (!meetEmpty) {
                result += line
                println(line)
            }
        } while (!meetEmpty)
    }
    val errorResult = process.exitValue() == 0
    if (!errorResult) {
        throw IllegalStateException(result)
    }
    return result
}
// </editor-fold>