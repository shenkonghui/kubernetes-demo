#!/bin/bash
PASSWORD=HarmonyCloud-Caas
NAME=*.caas-system.svc # 域名
DEPT=skyview  # 部门名
COMPANY=harmonycloud  # 公司名
CITY=HangZhou  # 城市
PROVINCE=Zhejiang  # 省份
COUNTRY=CN  # 国家
SUBJ="/C=$COUNTRY/ST=$PROVINCE/L=$CITY/O=$COMPANY/OU=$DEPT/CN=$NAME"

#1
echo "生成服务器端的私钥"
# server.key=harmonycloud
openssl genrsa -des3 -out server.key 2048
echo "去除server.key文件口令"
openssl rsa -in server.key -out server.key

#2
echo "用server.key生成证书"
openssl req -new -key server.key -out server.csr -days 36500 -subj "$SUBJ" -passin pass:"$PASSWORD" -passout pass:"$PASSWORD"

#3
echo "生成客户端私钥"
# client.key=caas
openssl genrsa -des3 -out client.key 2048
echo "client.key文件口令"
openssl rsa -in client.key -out client.key
echo "使用client.key生成客户端证书"
openssl req -new -key client.key -out client.csr -days 36500 -subj "$SUBJ" -passin pass:"$PASSWORD" -passout pass:"$PASSWORD"

#4
echo "生成CA证书"
openssl req -new -x509 -keyout ca.key -out ca.crt -days 36500 -subj "$SUBJ" -passin pass:"$PASSWORD" -passout pass:"$PASSWORD"

#5.0
if ! grep \/etc\/pki\/CA /etc/pki/tls/openssl.cnf|grep -q ^dir;then
        echo "请在/etc/pki/tls/openssl.cnf修改dir = /etc/pki/CA"
        exit 2
fi
/bin/rm -rf  /etc/pki/CA/index.txt && touch /etc/pki/CA/index.txt
echo 01 > /etc/pki/CA/serial
/bin/rm -rf /etc/pki/CA/newcerts && mkdir /etc/pki/CA/newcerts
echo  "unique_subject = no" >/etc/pki/CA/index.txt.attr

#5
echo "用生成的CA的证书为server.csr签名"
openssl ca -in server.csr -out server.crt -cert ca.crt -keyfile ca.key -days 3650 -passin pass:"$PASSWORD"

#6
echo "用生成的CA的证书为client.crt签名"
openssl ca -in client.csr -out client.crt -cert ca.crt -keyfile ca.key -days 3650 -passin pass:"$PASSWORD"

#7.
echo "验证client证书"
openssl verify -CAfile ca.crt client.crt 

#8. 
echo "生成tomcat证书"
openssl pkcs12 -export -in server.crt -inkey server.key -out server.p12 -name tomcat -CAfile ca.crt -caname root -chain

