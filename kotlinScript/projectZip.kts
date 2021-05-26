import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

println(getPwd())

var fileToZip: File? = null
while (fileToZip == null) {
    println("输入合法的要压缩的目录")
    var inputDir = readLine()
    fileToZip = checkInputDirValid(inputDir)
}
val finalFileToZip = fileToZip!!;

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
val finalOutputFile = outputFile!!


println("目标压缩路径为：" + finalFileToZip.absoluteFile)
runGradleClean(finalFileToZip.absolutePath)
makeZip(finalFileToZip, finalOutputFile, listOf("/.gradle/", "/.idea/", "/.git/"))

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

fun runGradleClean(dir: String) {
    var tmpDir = dir
    if (!tmpDir.endsWith("/")) {
        tmpDir = dir + "/"
    }
    val cmd = tmpDir + "gradlew clean"
    println(cmd)
    val runtime = Runtime.getRuntime()
    val process = runtime.exec(cmd)
    val br = BufferedReader(InputStreamReader(process.inputStream))
    var message: String = br.readLine() ?: ""
    val result = process.waitFor()
    println("cmd resulst=" + result)

//    while (true) {
//        if (!message.trim().isEmpty()) {
//            println(message)
//        }
//        message = br.readLine() ?: ""
//    }
}

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

