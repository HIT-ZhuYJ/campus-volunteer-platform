# 校园志愿服务管理平台

## 项目简介

基于Spring Cloud Alibaba微服务架构的校园志愿服务管理平台，支持志愿活动发布、报名管理、时长核销等完整流程，集成AI智能生成活动文案功能。

## 技术栈

- **基础框架**: Spring Boot 3.3.4 (Java 17)
- **微服务治理**: Spring Cloud Alibaba 2023.0.3.2
- **注册中心**: Nacos 2.x
- **网关**: Spring Cloud Gateway
- **持久层**: MySQL 8.0.45 + MyBatis-Plus 3.5.5
- **缓存**: Redis (用于防超卖)
- **服务调用**: OpenFeign
- **监控**: Spring Boot Admin 3.3.4
- **认证**: JWT

## 系统架构

```
├── gateway-service      (9000)  - API网关 + JWT鉴权
├── user-service         (8100)  - 用户服务
├── activity-service     (8200)  - 活动服务
├── monitor-service      (9100)  - 监控中心
└── common                       - 公共模块
```

## 核心功能

### 1. 用户服务 (user-service)
- 用户注册/登录
- JWT Token生成与验证
- 用户信息管理
- 志愿时长累计

### 2. 活动服务 (activity-service)
- 活动发布与管理
- 活动列表查询（支持筛选）
- 志愿活动报名（Redis防超卖）
- 我的志愿足迹
- 时长核销
- AI智能生成活动文案

### 3. 网关服务 (gateway-service)
- 统一流量入口
- 路由转发
- 全局JWT鉴权
- 跨域处理

### 4. 监控服务 (monitor-service)
- 服务健康监控
- JVM性能监控
- 实时日志查看

## 数据库设计

### 核心表结构

1. **sys_user** - 用户表
   - 存储用户基本信息、角色、累计志愿时长

2. **vol_activity** - 志愿活动表
   - 存储活动详情、招募人数、时间安排、状态

3. **vol_registration** - 报名流水表
   - 记录报名关系、签到状态、时长核销状态

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 5.0+
- Nacos 2.x

### 1. 启动基础设施

#### 启动Nacos
```bash
# Windows
cd nacos/bin
startup.cmd -m standalone

# Linux/Mac
cd nacos/bin
sh startup.sh -m standalone
```
访问: http://localhost:8848/nacos (默认账号密码: nacos/nacos)

#### 启动Redis
```bash
# Windows
redis-server.exe redis.windows.conf

# Linux/Mac
redis-server
```

### 2. 初始化数据库

```bash
# 连接MySQL
mysql -u root -p

# 执行初始化脚本
source database/init.sql
```

### 3. 修改配置

根据实际环境修改各服务的 `application.properties`:
- 数据库连接信息
- Nacos地址
- Redis地址

### 4. 编译项目

```bash
mvn clean install -DskipTests
```

### 5. 启动服务

按以下顺序启动服务：

```bash
# 1. 启动监控服务
cd services/monitor-service
mvn spring-boot:run

# 2. 启动网关服务
cd services/gateway-service
mvn spring-boot:run

# 3. 启动用户服务
cd services/user-service
mvn spring-boot:run

# 4. 启动活动服务
cd services/activity-service
mvn spring-boot:run
```

### 6. 验证服务

- Nacos控制台: http://localhost:8848/nacos
- 监控中心: http://localhost:9100
- API网关: http://localhost:9000

## API接口文档

### 用户接口

#### 1. 用户注册
```http
POST http://localhost:9000/user/register
Content-Type: application/json

{
  "username": "student01",
  "password": "password123",
  "realName": "张三",
  "studentNo": "2024001",
  "phone": "13800138001",
  "email": "zhangsan@university.edu"
}
```

#### 2. 用户登录
```http
POST http://localhost:9000/user/login
Content-Type: application/json

{
  "username": "student01",
  "password": "password123"
}
```

返回Token:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userInfo": {
      "id": 1,
      "username": "student01",
      "realName": "张三",
      "role": "VOLUNTEER",
      "totalVolunteerHours": 12.50
    }
  }
}
```

#### 3. 获取用户信息
```http
GET http://localhost:9000/user/info
Authorization: Bearer {token}
```

### 活动接口

#### 1. 活动列表
```http
GET http://localhost:9000/activity/list?page=1&size=10&status=RECRUITING&category=学长火炬
```

#### 2. 活动详情
```http
GET http://localhost:9000/activity/1
Authorization: Bearer {token}
```

#### 3. 创建活动（管理员）
```http
POST http://localhost:9000/activity/create
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "title": "学长火炬 - 新生入学引导",
  "description": "协助新生办理入学手续...",
  "location": "学校东门迎新点",
  "maxParticipants": 50,
  "volunteerHours": 4.0,
  "startTime": "2024-09-01T08:00:00",
  "endTime": "2024-09-01T18:00:00",
  "registrationDeadline": "2024-08-25T23:59:59",
  "category": "学长火炬"
}
```

#### 4. 报名活动
```http
POST http://localhost:9000/activity/register/1
Authorization: Bearer {token}
```

#### 5. 我的报名记录
```http
GET http://localhost:9000/activity/myRegistrations
Authorization: Bearer {token}
```

#### 6. AI生成活动文案（管理员）
```http
POST http://localhost:9000/activity/ai/generate
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "location": "校医院门口",
  "category": "爱心小屋",
  "keywords": "献血车, 爱心服务, 周六"
}
```

#### 7. 核销时长（管理员）
```http
POST http://localhost:9000/activity/confirmHours/1
Authorization: Bearer {admin_token}
```

## 测试账号

系统已预置测试账号：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | password123 | ADMIN | 管理员账号 |
| student01 | password123 | VOLUNTEER | 普通志愿者 |
| student02 | password123 | VOLUNTEER | 普通志愿者 |

## 核心业务流程

### 1. 志愿活动报名流程

```
1. 管理员创建活动 → vol_activity表
2. 活动库存写入Redis → activity:stock:{activityId}
3. 志愿者浏览活动列表
4. 志愿者报名 → Redis原子减库存
5. 报名成功 → vol_registration表
6. 更新活动当前人数
```

### 2. 时长核销流程

```
1. 活动结束后，管理员核销时长
2. 更新报名记录状态 → vol_registration.hours_confirmed = 1
3. 调用user-service更新用户累计时长
4. 志愿者在个人中心查看时长累计
```

### 3. AI智能生成流程

```
1. 管理员输入关键词（地点、类型、关键词）
2. 构建Prompt调用AI API
3. 返回生成的活动文案
4. 管理员编辑并创建活动
```

## 防超卖设计

使用Redis原子操作保证高并发下的报名安全：

```java
// 1. 活动创建时初始化库存
redisTemplate.opsForValue().set("activity:stock:1", "50");

// 2. 报名时原子递减
Long stock = redisTemplate.opsForValue().decrement("activity:stock:1");

// 3. 库存不足时回滚
if (stock < 0) {
    redisTemplate.opsForValue().increment("activity:stock:1");
    throw new BusinessException("名额已满");
}
```

## 安全机制

### JWT鉴权流程

```
1. 用户登录成功 → 生成JWT Token
2. 前端存储Token
3. 每次请求携带Token → Authorization: Bearer {token}
4. Gateway全局过滤器验证Token
5. 解析Token获取userId、username、role
6. 将用户信息写入请求头传递给下游服务
7. 下游服务从请求头获取用户信息
```

### 白名单配置

以下接口无需认证：
- `/user/login` - 登录
- `/user/register` - 注册
- `/activity/list` - 活动列表

## 监控指标

访问 http://localhost:9100 查看：

- 服务健康状态
- JVM内存使用
- 线程状态
- HTTP请求追踪
- 日志实时查看

## 扩展功能

### 1. CI/CD集成

项目支持Jenkins自动化部署：

```groovy
pipeline {
    agent any
    stages {
        stage('Pull') {
            steps {
                git 'https://github.com/your-repo/volunteer-platform.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('Deploy') {
            steps {
                sh './deploy.sh'
            }
        }
    }
}
```

### 2. 数据统计大屏

可扩展开发数据看板功能：
- 累计志愿总时长
- 活动类型分布
- 参与度排行榜
- 月度趋势图表

## 常见问题

### 1. Nacos注册失败
- 检查Nacos是否启动
- 确认配置文件中的server-addr正确
- 检查网络连接

### 2. Redis连接失败
- 确认Redis服务已启动
- 检查端口是否被占用
- 验证密码配置

### 3. 数据库连接失败
- 检查MySQL服务状态
- 确认数据库已创建
- 验证用户名密码

### 4. 网关转发失败
- 确认下游服务已注册到Nacos
- 检查路由配置
- 查看网关日志

## 项目结构

```
cloud-demo/
├── database/                   # 数据库脚本
│   └── init.sql
├── services/
│   ├── common/                # 公共模块
│   │   └── src/main/java/org/example/common/
│   │       ├── result/        # 统一返回结果
│   │       ├── util/          # 工具类（JWT）
│   │       ├── exception/     # 异常定义
│   │       └── constant/      # 常量定义
│   ├── gateway-service/       # 网关服务
│   │   └── src/main/java/org/example/
│   │       ├── filter/        # 全局过滤器
│   │       └── config/        # 配置类
│   ├── user-service/          # 用户服务
│   │   └── src/main/java/org/example/
│   │       ├── entity/        # 实体类
│   │       ├── dto/           # 请求DTO
│   │       ├── vo/            # 返回VO
│   │       ├── mapper/        # MyBatis Mapper
│   │       ├── service/       # 业务逻辑
│   │       └── controller/    # 控制器
│   ├── activity-service/      # 活动服务
│   │   └── src/main/java/org/example/
│   │       ├── entity/        # 实体类
│   │       ├── dto/           # 请求DTO
│   │       ├── vo/            # 返回VO
│   │       ├── mapper/        # MyBatis Mapper
│   │       ├── service/       # 业务逻辑
│   │       ├── feign/         # Feign客户端
│   │       └── controller/    # 控制器
│   └── monitor-service/       # 监控服务
└── pom.xml                    # 父POM
```

## 贡献指南

欢迎提交Issue和Pull Request！

## 许可证

MIT License

## 联系方式

如有问题请提交Issue或联系项目维护者。
