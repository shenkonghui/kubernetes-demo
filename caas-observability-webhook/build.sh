#!/bin/bash
# 此脚本可用来本地打包并构建镜像
# git pull --rebase origin/caas-admission-webhook
image=caas-admission-webhook:v1.0.0
jarName=caas-admission-webhook-0.0.1-SNAPSHOT.jar
jarPath=target/$jarName

# mvn打包项目
mvn clean package -DskipTests
cd target
ls | grep -v $jarName | xargs rm -rf
cd ..

# 构建镜像
harbor=10.10.101.175
docker build -t $harbor/k8s-deploy/$image .

# 按需是否推送镜像
#harbor_password=Harbor12345
#docker login $harbor --username admin --password $harbor_password
#docker push $harbor/k8s-deploy/$image
