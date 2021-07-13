#!/bin/bash
#channels=(anzhi zhihuiyun wo taobao)
channels=(web 360 xiaomi wandoujia baidu tencent zhihuiyun anzhi oppo lenovo sougouzhushou sougoushichang jifeng mumayi nduo miezu uc wangyi jinli mm wo letv 91
 anzhuo vivo apphui kuchuan sunning wangxunanzhuo paojiao mogu yingyongzhan tongyianzhuo zhuoyi tianyi googleplay wifixinhao suoping zhengqianba duote shoujizhijia
 mogushichang yiyoushichang anbeishichang anzhuozhijia anzhuoyuan liqushichang jike chuanyizhushou aiqiyi)

if [ ! -d kdsp_apks ];then
   mkdir kdsp_apks
fi
rm -rf kdsp_apks/*

for ch in "${channels[@]}"; do
	PROFILE_OPTION=""
	if [ "$ch"x = "wo"x ];then
		PROFILE_OPTION=" -r wo "
	fi
	echo "profile is "$PROFILE_OPTION;
   sh build_channel.sh -c $ch $PROFILE_OPTION
done
