echo "欢迎进入脚本启动器"
# 获取当前脚本的绝对路径
SHELL_FOLDER=$(dirname $(readlink "$0"))
echo "当前脚本真实路径=$SHELL_FOLDER\n"

# kotlinc -script $SHELL_FOLDER/kotlinScript/projectZip.kts
chmod +x $SHELL_FOLDER/kotlinScript/projectZip.main.kts
$SHELL_FOLDER/kotlinScript/projectZip.main.kts
