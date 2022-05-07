#! /bin/bash

TARGET_NAME=$1

PROJECT_ROOT=/Users/hanumantha.banda/GitHub/log4j-example

CP=${CLASSPATH}:${PROJECT_ROOT}/out/production/log4j-example:${PROJECT_ROOT}/lib/log4j-1.2.15.jar:${PROJECT_ROOT}/lib/jmxtools.jar:${PROJECT_ROOT}/lib/mailapi.jar:${PROJECT_ROOT}/lib/smtp.jar:${PROJECT_ROOT}/lib/hibernate3.jar:${PROJECT_ROOT}/lib/mysql-connector-java-5.0.5-bin.jar:${PROJECT_ROOT}/lib/dom4j-1.6.1.jar:${PROJECT_ROOT}/lib/commons-logging-1.1.jar:${PROJECT_ROOT}/lib/commons-collections-3.2.jar:${PROJECT_ROOT}/lib/c3p0-0.9.1.jar:${PROJECT_ROOT}/lib/ehcache-1.2.3.jar:${PROJECT_ROOT}/lib/jta.jar:${PROJECT_ROOT}/lib/joda-time-1.4.jar

. ${PROJECT_ROOT}/setvars.sh

echo "Starting Build ${TARGET_NAME} ..."

ant -f ${PROJECT_ROOT}/build.xml ${TARGET_NAME}

echo "Using classpath $CP"

java -classpath ${CP} com.makotogroup.log4j.Controller myapp.xml ${TARGET_NAME}
