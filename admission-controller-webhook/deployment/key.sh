#!/bin/bash
# 创建ca私钥
openssl genrsa  -out ca.key
# 创建ca是证书, 一直回车
openssl req -new -x509 -days 365 -key ca.key -out ca.crt -extensions v3_req -config openssl.cnf -batch

# 创建服务端私钥证书
openssl genrsa -out server.key 2048

# 创建服务端证书请求，并生成服务端证书
openssl req -new -subj "/CN=server" -key server.key -out server.csr -config openssl.cnf -extensions v3_req -batch
openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out server.crt -days 5000 -extfile openssl.cnf  -extensions v3_req

# 可以用这个命令检查证书信息
openssl x509 -in ca.crt -noout -text