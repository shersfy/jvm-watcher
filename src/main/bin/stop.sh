#!/bin/bash
#author yuehan1@lenovo.com
#date 2016-04-28

mainClass=com.lenovo.datahub.client.ClientMain
pid=`ps aux | grep $mainClass | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ];then
  kill -9 $pid
  echo "Datahub client stopped success"
else
  echo "No datahub client process exists"
fi
