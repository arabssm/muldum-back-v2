#!/bin/bash

APP_NAME=muldum-0.0.1-SNAPSHOT.jar
APP_DIR=/home/ec2-user/app
JAR_PATH=$APP_DIR/$APP_NAME
LOG_PATH=$APP_DIR/app.log

echo ">>> Deploying $APP_NAME"

# 이미 실행 중인 프로세스 종료
PID=$(pgrep -f $APP_NAME)
if [ -n "$PID" ]; then
  echo ">>> Stopping existing process: $PID"
  kill -9 $PID
fi

# 새 JAR 실행
echo ">>> Starting new JAR..."
nohup java -jar $JAR_PATH > $LOG_PATH 2>&1 &