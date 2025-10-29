# Local Load Test - API性能测试工具

基于JDK 25和JMH的API压测工具,提供简洁高效的性能测试能力。

## 特性

- 基于JMH(Java Microbenchmark Harness)框架
- 支持GET/POST/PUT/DELETE/PATCH方法
- 连接池管理,支持高并发
- 可配置超时和线程数
- 生成详细的性能报告(吞吐量、延迟等)

## 环境要求

- JDK 25
- Maven 3.6+

## 快速开始

### 1. 编译项目

```bash
mvn clean package
```

### 2. 运行压测

```bash
java -Dapi.baseUrl=http://localhost:8080 \
  -Dapi.endpoint=/api/v1/test \
  -Dapi.method=GET \
  -jar target/benchmarks.jar \
  -t 10 -wi 3 -w 5s -i 5 -r 10s \
  ApiLoadTestBenchmark
```

### 3. 配置参数

#### API配置参数(通过-D指定)

| 参数 | 说明 | 默认值 |
|------|------|--------|
| api.baseUrl | API基础URL | http://localhost:8080 |
| api.endpoint | API端点路径 | /api/v1/test |
| api.method | HTTP方法 | GET |
| api.body | 请求体(JSON) | "" |
| api.headers | HTTP Headers | "" |
| api.connectionTimeout | 连接超时(ms) | 5000 |
| api.socketTimeout | 响应超时(ms) | 10000 |

#### JMH性能测试参数(通过命令行选项指定)

| 参数 | 说明 | 默认值 | 示例 |
|------|------|--------|------|
| -t | 并发线程数 | 1 | -t 10 |
| -wi | 预热迭代次数 | 0 | -wi 3 |
| -w | 每次预热时长 | 10s | -w 5s |
| -i | 测试迭代次数 | 1 | -i 5 |
| -r | 每次测试时长 | 10s | -r 10s |
| -f | JVM fork次数 | 1 | -f 1 |

### 4. 示例

#### GET请求测试(10线程并发)
```bash
java -Dapi.baseUrl=https://api.example.com \
  -Dapi.endpoint=/users \
  -Dapi.method=GET \
  -jar target/benchmarks.jar \
  -t 10 -wi 3 -w 5s -i 5 -r 10s \
  ApiLoadTestBenchmark
```

#### POST请求测试(20线程,快速测试)
```bash
java -Dapi.baseUrl=https://api.example.com \
  -Dapi.endpoint=/users \
  -Dapi.method=POST \
  -Dapi.body='{"name":"test","email":"test@example.com"}' \
  -jar target/benchmarks.jar \
  -t 20 -wi 2 -w 3s -i 3 -r 5s \
  ApiLoadTestBenchmark
```

#### POST请求 + 自定义Headers
```bash
java -Dapi.baseUrl=https://api.example.com \
  -Dapi.endpoint=/api/login \
  -Dapi.method=POST \
  -Dapi.body='{"username":"test","password":"pass123"}' \
  -Dapi.headers='Content-Type:application/json,Authorization:Bearer token123,X-Request-ID:test-001' \
  -jar target/benchmarks.jar \
  -t 10 -wi 3 -w 5s -i 5 -r 10s \
  ApiLoadTestBenchmark
```

#### 高并发压测(50线程,长时间测试)
```bash
java -Dapi.baseUrl=https://api.example.com \
  -Dapi.endpoint=/api/test \
  -Dapi.method=GET \
  -jar target/benchmarks.jar \
  -t 50 -wi 5 -w 10s -i 10 -r 30s \
  ApiLoadTestBenchmark
```

## 常用测试场景

### 快速验证(单线程,无预热)
```bash
java -Dapi.baseUrl=http://localhost:8080 \
  -Dapi.endpoint=/health \
  -jar target/benchmarks.jar \
  -t 1 -wi 0 -i 1 -r 5s \
  ApiLoadTestBenchmark
```

### 标准压测(10线程)
```bash
java -Dapi.baseUrl=http://localhost:8080 \
  -Dapi.endpoint=/api/test \
  -jar target/benchmarks.jar \
  -t 10 -wi 3 -w 5s -i 5 -r 10s \
  ApiLoadTestBenchmark
```

### 高负载压测(100线程)
```bash
java -Dapi.baseUrl=http://localhost:8080 \
  -Dapi.endpoint=/api/test \
  -jar target/benchmarks.jar \
  -t 100 -wi 5 -w 10s -i 10 -r 30s \
  ApiLoadTestBenchmark
```

## Headers配置说明

Headers格式为：`Header1:Value1,Header2:Value2`，多个Header用逗号分隔。

### 常用Headers示例

#### 1. Content-Type指定
```bash
-Dapi.headers='Content-Type:application/json'
```

#### 2. 身份认证Token
```bash
-Dapi.headers='Authorization:Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...'
```

#### 3. 多个Headers组合
```bash
-Dapi.headers='Content-Type:application/json,Authorization:Bearer token123,X-API-Key:abc123,User-Agent:LoadTest/1.0'
```

#### 4. 完整POST示例（带Headers）
```bash
java -Dapi.baseUrl=https://api.example.com \
  -Dapi.endpoint=/api/orders \
  -Dapi.method=POST \
  -Dapi.body='{"productId":"12345","quantity":2,"userId":"user001"}' \
  -Dapi.headers='Content-Type:application/json,Authorization:Bearer YOUR_TOKEN,X-Trace-ID:test-trace-001' \
  -jar target/benchmarks.jar \
  -t 15 -wi 3 -w 5s -i 5 -r 10s \
  ApiLoadTestBenchmark
```

## 性能指标

JMH会输出以下指标:
- **Throughput**: 吞吐量(ops/sec)
- **Average Time**: 平均响应时间
- **P99/P95**: 百分位延迟
- **Min/Max**: 最小/最大响应时间

## 项目结构

```
local-load-test/
├── pom.xml                          # Maven配置
├── run-test.sh                      # 运行脚本
├── README.md                        # 说明文档
└── src/main/java/com/local/loadtest/
    ├── ApiConfig.java               # API配置类
    └── ApiLoadTestBenchmark.java    # JMH压测类
```

## 注意事项

1. 使用`-wi 0`可跳过预热阶段,适合快速验证
2. 线程数(`-t`)建议从小到大逐步增加,观察系统表现
3. 测试时长(`-r`)至少5秒以上才能获得稳定结果
4. 生产环境压测需提前报备并做好流量控制
5. 建议先在测试环境验证压测脚本正确性
