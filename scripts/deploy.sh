#!/bin/bash

echo ">>> Deploying with Docker Compose"

cd /home/ubuntu/app

# 최신 이미지 pull
docker-compose pull

# 컨테이너 재시작
docker-compose down
docker-compose up -d