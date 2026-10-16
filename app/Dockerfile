# 멤버용 앱 이미지: JDK 21 + Node 22 + git + python3 + Gradle 배포판·러너 의존성 프리페치
# 빌드 컨텍스트는 레포 루트 (docker-compose.yml 참고). 레지스트리에 올리지 않고 각자 로컬에서 빌드한다.
FROM node:22-bookworm-slim AS node

FROM eclipse-temurin:21-jdk-noble

ENV DEBIAN_FRONTEND=noninteractive \
    LANG=C.UTF-8 \
    TZ=Asia/Seoul

RUN apt-get update \
 && apt-get install -y --no-install-recommends git python3 ca-certificates tini \
 && rm -rf /var/lib/apt/lists/*

# Node 는 공식 이미지에서 바이너리만 가져온다 (glibc 기반이라 그대로 동작)
COPY --from=node /usr/local/bin/node /usr/local/bin/node
COPY --from=node /usr/local/lib/node_modules /usr/local/lib/node_modules
RUN ln -s /usr/local/lib/node_modules/npm/bin/npm-cli.js /usr/local/bin/npm \
 && ln -s /usr/local/lib/node_modules/npm/bin/npx-cli.js /usr/local/bin/npx \
 && node --version && npm --version && java -version && git --version && python3 --version

# /workspace 는 호스트 사용자 소유라 git 이 "dubious ownership" 으로 거부하지 않도록 허용
RUN git config --system safe.directory '*'

# Gradle 배포판 + 러너 의존성(JUnit, Gson)을 이미지 안에 미리 받아 둔다.
# /gradle-home 은 compose 에서 named volume 으로 잡히며, 볼륨이 처음 만들어질 때 이 내용이 복사된다.
ENV GRADLE_USER_HOME=/gradle-home
COPY runner /tmp/runner
RUN cd /tmp/runner && chmod +x gradlew \
 && ./gradlew --no-daemon -q compileTestJava \
 && rm -rf /tmp/runner /gradle-home/daemon /gradle-home/.tmp

# 앱
WORKDIR /app
COPY app/package.json app/package-lock.json ./
RUN npm ci --omit=dev --no-audit --no-fund
COPY app/server.js ./
COPY app/src ./src
COPY app/public ./public

ENV REPO_ROOT=/workspace \
    DATA_DIR=/data \
    PORT=15015
EXPOSE 15015
VOLUME ["/data", "/gradle-home"]

ENTRYPOINT ["tini", "--"]
CMD ["node", "server.js"]
