#!/bin/bash
#从命令行获取渠道,打包中加入VersionName，由于manifest文件中没有，因此每次需手动设置
export VERSION_NAME="2.1"
export UMENG_CHANNEL="web"
export PROFILE="release"

while getopts "hc:" arg #c is used to specify channel
do
	case $arg in
		c)
			UMENG_CHANNEL=$OPTARG
			;;
		h)
			echo "-c channel name";
			exit 0;
			;;
	esac
done

echo "开始打包渠道${UMENG_CHANNEL}"
#先取出版本库中的配置文件，所以打包前确保AndroidManifest.xml已经提交到版本库
git co -f AndroidManifest.xml
#设置AndroidManifest,用sed就可以，但考虑到兼容性，还是用java了
java -cp . SetUMengChannel ./AndroidManifest.xml ${UMENG_CHANNEL}
echo '修改AndroidManifest.xml成功！开始编译……'
#使用gradle打包
gradle -q assembleRelease
#移动到上述文件夹中
mv build/outputs/apk/*release.apk kdsp_apks/kdsp.${VERSION_NAME}.${UMENG_CHANNEL}.apk
echo '编译结束，apk包已经在kdsp_apks中！'

#判断ysrl_apks文件夹是否存在
if [ ! -d kdsp_apks ]; then
   mkdir kdsp_apks
fi

git co -f AndroidManifest.xml

