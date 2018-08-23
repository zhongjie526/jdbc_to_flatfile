#!/bin/bash
cd "$(dirname "$0")"
cd ..

. ./scripts/setenv.sh


if [[ -z "${JVM_OPTS}" ]]; then
  MY_JVM_OPTS=-Xmx8G
else
  MY_JVM_OPTS="${JVM_OPTS}"
fi

export LD_LIBRARY_PATH=/prodlib/SCM/lib:/app/oraclient/1120x32/lib:$LD_LIBRARY_PATH
LIBPATH=$LD_LIBRARY_PATH


myProfile=$1
processExists=`ps aux | grep ${myProfile} | grep CpmBatch | grep -v grep`
echo ${processExists}
if [[ -z "${processExists}" ]]; then
    java -Dspring.profiles.active=base,${myProfile} ${MY_JVM_OPTS} -jar ./lib/cpm-batch*.jar com.uob.dge.cpmbatch.CpmBatch
else
    echo "${myProfile} application is already running. Please stop the application and start again"
fi
