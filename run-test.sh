#!/bin/bash

# API压测运行脚本
# 使用方法: ./run-test.sh

# 首先编译项目
echo "编译项目..."
mvn clean package

# 运行JMH测试
echo "开始压测..."
java -Dapi.baseUrl=http://localhost:8080 \
  -Dapi.endpoint=/api/v1/test \
  -Dapi.method=GET \
  -Dapi.connectionTimeout=5000 \
  -Dapi.socketTimeout=10000 \
  -jar target/benchmarks.jar \
  -t 10 \
  -wi 3 \
  -w 5s \
  -i 5 \
  -r 10s \
  ApiLoadTestBenchmark

# JMH参数说明:
# -t <threads>     : 线程数,默认10
# -wi <iterations> : 预热迭代次数,默认3
# -w <time>        : 每次预热时长,默认5s
# -i <iterations>  : 测试迭代次数,默认5
# -r <time>        : 每次测试时长,默认10s
# -f <forks>       : fork次数,默认1

# 自定义参数示例:
# java -Dapi.baseUrl=https://api.example.com \
#   -Dapi.endpoint=/users \
#   -Dapi.method=POST \
#   -Dapi.body='{"name":"test","email":"test@example.com"}' \
#   -jar target/benchmarks.jar \
#   -t 20 -wi 2 -w 3s -i 3 -r 10s \
#   ApiLoadTestBenchmark
