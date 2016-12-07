#!/bin/sh
###################################
#JAVA_HOME="/usr/lib/jvm/java-1.6.0" 
MSISDN=$1
APP_HOME=/app/hbasetool
APP_MAINCLASS=com.cqx.HbaseExport
CLASSPATH=$APP_HOME
for i in "$APP_HOME"/lib/*.jar; do
  CLASSPATH="$CLASSPATH":"$i"
done
$JAVA_HOME/bin/java -classpath $CLASSPATH $APP_MAINCLASS -c $APP_HOME/conf -n gn_cdr_hn_3 -m $MSISDN -s 20160301000000 -e 20160304235959 -f $APP_HOME/$MSISDN.txt
