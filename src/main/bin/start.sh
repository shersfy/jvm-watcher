#!/bin/bash
#author yuehan1@lenovo.com
#date 2016-04-28
cd "$(dirname "$0")"
cd ..

javahome=${JAVA_HOME}
if [ -z $javahome ]; then
    echo error: JAVA_HOME not configured
    exit 1
fi
echo "JAVA_HOME=$javahome"

#read java version
if [ ! -e "${javahome}/bin/java" ]; then
    echo error: ${javahome}/bin/java not exist  
    exit 1
fi

if [ ! -d "logs" ]; then
  mkdir logs
fi

$javahome/bin/java -version 2>version/java.version
jversion=`head -1 version/java.version`
echo JAVA_VERSION=${jversion}

v1=1.8
v2=${jversion:14:3}
if [ $v1 != $v2 ] ;then
	echo error: Datahub Client rely on JRE version least is 1.8
	exit 1
fi

mainClass=com.lenovo.datahub.client.ClientMain
pid=`ps aux | grep $mainClass | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ];then
  echo error: Datahub Client already exists
  exit 1
fi
if [ ! -d "./logs" ]; then
  mkdir ./logs
fi
echo "Welcome to Datahub Client"
nohup $javahome/bin/java -cp lib/*:. $mainClass > logs/stdout.log 2>&1 &
echo "`date '+%Y-%m-%d %H:%M:%S'` Datahub Client is running..."
