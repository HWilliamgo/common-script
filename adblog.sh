# 获取当前脚本的绝对路径
SHELL_FOLDER=$(cd "$(dirname "$0")";pwd)
echo "当前脚本路径=$SHELL_FOLDER"

applicationId=$1

time=`date +%m月%d日%H-%M-%S`

adb logcat --pid=`adb shell pidof -s $applicationId` > "$time.log"