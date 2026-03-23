# 校园志愿服务管理平台 - 部署指南

## 环境准备清单

### 1. 必需软件
- [x] JDK 17+
- [x] Maven 3.8+
- [x] MySQL 8.0.45
- [x] Redis 5.0+
- [x] Nacos 2.x

### 2. 环境变量配置

```bash
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17
set MAVEN_HOME=C:\Program Files\Apache\maven
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

# Linux/Mac
export JAVA_HOME=/usr/lib/jvm/java-17
export MAVEN_HOME=/opt/maven
export PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH
```

## 部署步骤

### 第一步：安装基础设施

#### 1. 安装MySQL 8.0.45

```bash
# 下载并安装MySQL
# https://dev.mysql.com/downloads/mysql/

# 启动MySQL服务
# Windows
net start mysql

# Linux
systemctl start mysql
```

#### 2. 安装Redis

```bash
# Windows
# 下载Redis for Windows
# https://github.com/microsoftarchive/redis/releases

# 启动Redis
redis-server.exe redis.windows.conf

# Linux
sudo apt install redis-server
sudo systemctl start redis
```

#### 3. 安装Nacos

```bash
# 下载Nacos 2.x
# https://github.com/alibaba/nacos/releases

# 解压后启动（单机模式）
# Windows
cd nacos/bin
startup.cmd -m standalone

# Linux
cd nacos/bin
sh startup.sh -m standalone

# 访问控制台
http://localhost:8848/nacos
账号: nacos
密码: nacos
```

### 第二步：初始化数据库

```bash
# 1. 登录MySQL
mysql -u root -p

# 2. 执行初始化脚本
source D:/clouddemo/cloud-demo/database/init.sql

# 3. 验证
use volunteer_platform;
show tables;
```

### 第三步：修改配置文件

#### 1. user-service配置

编辑 `services/user-service/src/main/resources/application.properties`:

```properties
# 根据实际环境修改
spring.datasource.url=jdbc:mysql://localhost:3306/volunteer_platform?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的密码

spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848
```

#### 2. activity-service配置

编辑 `services/activity-service/src/main/resources/application.properties`:

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/volunteer_platform?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的密码

# Redis配置
spring.data.redis.host=127.0.0.1
spring.data.redis.port=6379

# Nacos配置
spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848

# AI API配置（可选）
ai.api.url=https://api.openai.com/v1/chat/completions
ai.api.key=你的API密钥
```

#### 3. gateway-service配置

编辑 `services/gateway-service/src/main/resources/application.properties`:

```properties
spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848
```

#### 4. monitor-service配置

编辑 `services/monitor-service/src/main/resources/application.properties`:

```properties
spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848
```

### 第四步：编译项目

```bash
# 在项目根目录执行
cd D:\clouddemo\cloud-demo
mvn clean install -DskipTests
```

### 第五步：启动服务

#### 方式一：使用启动脚本（推荐）

```bash
# Windows
start-all.bat

# Linux/Mac
chmod +x start-all.sh
./start-all.sh
```

#### 方式二：手动启动

```bash
# 1. 启动监控服务
cd services/monitor-service
mvn spring-boot:run

# 2. 新开终端，启动网关服务
cd services/gateway-service
mvn spring-boot:run

# 3. 新开终端，启动用户服务
cd services/user-service
mvn spring-boot:run

# 4. 新开终端，启动活动服务
cd services/activity-service
mvn spring-boot:run
```

#### 方式三：打包部署（生产环境）

```bash
# 1. 打包所有服务
mvn clean package -DskipTests

# 2. 启动jar包
java -jar services/monitor-service/target/monitor-service-0.0.1-SNAPSHOT.jar
java -jar services/gateway-service/target/gateway-service-0.0.1-SNAPSHOT.jar
java -jar services/user-service/target/user-service-0.0.1-SNAPSHOT.jar
java -jar services/activity-service/target/activity-service-0.0.1-SNAPSHOT.jar
```

### 第六步：验证部署

#### 1. 检查Nacos服务注册

访问: http://localhost:8848/nacos

在"服务管理 -> 服务列表"中应该看到：
- gateway-service
- user-service
- activity-service
- monitor-service

#### 2. 检查监控中心

访问: http://localhost:9100

应该看到所有服务的健康状态。

#### 3. 测试API接口

```bash
# 测试用户登录
curl -X POST http://localhost:9000/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'

# 测试活动列表
curl http://localhost:9000/activity/list?page=1&size=10
```

## 部署检查清单

### 启动前检查
- [ ] MySQL服务已启动
- [ ] Redis服务已启动
- [ ] Nacos服务已启动（单机模式）
- [ ] 数据库已初始化
- [ ] 配置文件已修改

### 启动后检查
- [ ] 所有服务已注册到Nacos
- [ ] 监控中心可以访问
- [ ] 网关服务可以访问
- [ ] 用户登录接口正常
- [ ] 活动列表接口正常

## 常见问题排查

### 1. 服务无法注册到Nacos

**症状**: 服务启动后在Nacos控制台看不到

**排查步骤**:
```bash
# 1. 检查Nacos是否启动
curl http://localhost:8848/nacos

# 2. 检查配置文件中的Nacos地址
cat services/user-service/src/main/resources/application.properties | grep nacos

# 3. 查看服务日志
tail -f logs/user-service.log
```

### 2. 数据库连接失败

**症状**: 服务启动报错 `Could not get JDBC Connection`

**解决方法**:
```bash
# 1. 检查MySQL服务状态
# Windows
net start | find "MySQL"

# Linux
systemctl status mysql

# 2. 测试数据库连接
mysql -u root -p -e "use volunteer_platform; show tables;"

# 3. 检查配置文件中的数据库信息
```

### 3. Redis连接失败

**症状**: 报名活动时报错 `Cannot get Jedis connection`

**解决方法**:
```bash
# 1. 检查Redis服务
redis-cli ping
# 应该返回: PONG

# 2. 检查端口占用
netstat -an | find "6379"

# 3. 查看Redis配置
```

### 4. 网关转发失败

**症状**: 访问接口返回 `503 Service Unavailable`

**排查步骤**:
```bash
# 1. 确认下游服务已启动并注册到Nacos
curl http://localhost:8848/nacos/v1/ns/instance/list?serviceName=user-service

# 2. 检查网关路由配置
cat services/gateway-service/src/main/resources/application.properties | grep routes

# 3. 查看网关日志
```

### 5. JWT验证失败

**症状**: 返回 `401 Unauthorized`

**解决方法**:
```bash
# 1. 确认Token格式正确
# 应该是: Bearer eyJhbGciOiJIUzI1NiJ9...

# 2. 检查Token是否过期（有效期24小时）

# 3. 重新登录获取新Token
```

## 端口占用说明

| 端口 | 服务 | 说明 |
|------|------|------|
| 8848 | Nacos | 注册中心 |
| 6379 | Redis | 缓存服务 |
| 3306 | MySQL | 数据库 |
| 9000 | gateway-service | API网关 |
| 8100 | user-service | 用户服务 |
| 8200 | activity-service | 活动服务 |
| 9100 | monitor-service | 监控中心 |

## 日志位置

```
logs/
├── monitor-service.log
├── gateway-service.log
├── user-service.log
└── activity-service.log
```

## 停止服务

```bash
# Windows - 关闭所有Java进程
taskkill /F /IM java.exe

# Linux - 根据端口号杀进程
kill -9 $(lsof -t -i:9000)  # gateway
kill -9 $(lsof -t -i:8100)  # user-service
kill -9 $(lsof -t -i:8200)  # activity-service
kill -9 $(lsof -t -i:9100)  # monitor-service
```

## 下一步

部署成功后，您可以：

1. 访问监控中心查看服务状态
2. 使用Postman/Apifox测试API接口
3. 开发前端页面对接API
4. 配置CI/CD自动化部署

如有问题，请查看日志文件或提交Issue。
