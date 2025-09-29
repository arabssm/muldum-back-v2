# ===== base image =====
FROM openjdk:21-jdk-slim

# ===== install tools (awscli, bash) =====
RUN apt-get update \
 && apt-get install -y --no-install-recommends awscli ca-certificates bash \
 && rm -rf /var/lib/apt/lists/*

# ===== dirs =====
RUN mkdir -p /app /secrets/google
WORKDIR /app

# ===== app files =====
# wait-for-it 스크립트와 JAR 복사 (실행 JAR 이름에 맞게 조정 가능)
COPY wait-for-it.sh /wait-for-it.sh
COPY build/libs/app.jar /app/app.jar
RUN chmod +x /wait-for-it.sh

# ===== runtime env (선택) =====
# GOOGLE_SHEETS_KEY_FILE_PATH 는 앱이 로컬 파일 경로로 읽도록 유지
ENV GOOGLE_SHEETS_KEY_FILE_PATH=/secrets/google/credentials.json

# ===== network =====
EXPOSE 8080

# ===== entrypoint =====
# 1) S3에서 credentials.json 내려받기
# 2) postgres 올라올 때까지 대기
# 3) Spring Boot 실행
# SHEETS_S3_URI 환경변수를 전달하면 그 값을 사용하고, 없으면 기본값 
ENTRYPOINT ["/bin/sh", "-c", "\
  aws s3 cp ${SHEETS_S3_URI:-s3://muldumarabucket/club-2025-1deb98e262f6.json} /secrets/google/credentials.json && \
  exec /wait-for-it.sh postgres:5432 --timeout=60 --strict -- java -jar /app/app.jar \
"]
