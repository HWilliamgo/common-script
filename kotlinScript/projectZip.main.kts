#!/usr/bin/env kotlin

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

println("当前工作目录：" + runCommands("pwd"))

// <editor-fold defaultstate="collapsed" desc="获取目标压缩目录">
var fileToZip: File? = null
while (fileToZip == null) {
    println("输入合法的要压缩的目录")
    val inputDir = readLine()
    fileToZip = checkInputDirValid(inputDir)
}
val finalFileToZip = fileToZip!!;

/**
 * 检查输入的文件路径参数是否为目录
 * [inputDir]是输入的路径
 * @return null表示不合法，非null表示合法
 */
fun checkInputDirValid(inputDir: String?): File? {
    if (inputDir == null) {
        return null
    } else {
        val inputFile = File(inputDir)
        if (inputFile.exists() && inputFile.isDirectory) {
            return inputFile
        } else {
            return null
        }
    }
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="获取输出压缩文件">
fun getFinalOutputZipFile(): File {
    println("输入 \n    1==>默认输出到桌面\n    2==>自定义输出路径")
    val choice = readLine()
    return when (choice) {
        "1" -> {
            File("/Users/HWilliam/Desktop/archive.zip")
        }
        "2" -> {
            var outputFile: File? = null
            while (outputFile == null) {
                println("输入压缩文件的保存地址")
                var inputString = readLine()
                if (inputString != null) {
                    if (inputString!!.contains("./")) {
                        inputString = inputString!!.replace("./", getPwd())
                    }
                    println(inputString)
                }
                outputFile = checkIsFile(inputString)
            }
            outputFile
        }
        else -> {
            throw Exception()
        }
    }
}

fun checkIsFile(inputFile: String?): File? {
    if (inputFile == null) {
        return null
    } else {
        val tmp = File(inputFile)
        if (tmp.exists()) {
            tmp.delete()
        }
        tmp.createNewFile()
        return tmp
    }
}

val finalOutputFile = getFinalOutputZipFile()
println("目标压缩路径为：" + finalFileToZip.absoluteFile)
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="执行gradle clean">
fun runGradleClean(dir: String) {
    try {
        println(runCommands("chmod +x ./gradlew", dir))
        println(runCommands("./gradlew clean", dir))
    } catch (e: Exception) {
        e.printStackTrace()
        return
    }
}
runGradleClean(finalFileToZip.absolutePath)
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="执行压缩">
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
makeZip(finalFileToZip, finalOutputFile, listOf("/.gradle/", "/.idea/", "/.git/"))
// </editor-fold>


////////////////////////////////////////////////////
//工具函数集合
////////////////////////////////////////////////////
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
    process.waitFor()
    var result = ""
    process.inputStream.bufferedReader().forEachLine {
        result += it + "\n"
    }
    val errorResult = process.exitValue() == 0
    if (!errorResult) {
        throw IllegalStateException(result)
    }
    return result
}
// </editor-fold>

