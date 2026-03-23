# API测试文档

使用说明：可以使用Postman、Apifox或curl进行测试

## 测试环境

- 网关地址: http://localhost:9000
- 测试账号:
  - 管理员: admin / password123
  - 志愿者: student01 / password123

## 1. 用户相关接口

### 1.1 用户注册

```http
POST http://localhost:9000/user/register
Content-Type: application/json

{
  "username": "test001",
  "password": "password123",
  "realName": "测试用户",
  "studentNo": "2024999",
  "phone": "13900139000",
  "email": "test001@university.edu"
}
```

### 1.2 用户登录

```http
POST http://localhost:9000/user/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}
```

**返回示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTcxMTExMTExMSwiZXhwIjoxNzExMTk3NTExfQ.xxx",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "realName": "管理员",
      "studentNo": "2021001",
      "phone": "13800138000",
      "email": "admin@university.edu",
      "role": "ADMIN",
      "totalVolunteerHours": 0.00
    }
  }
}
```

**⚠️ 重要：将返回的token保存，后续请求需要使用！**

### 1.3 获取用户信息

```http
GET http://localhost:9000/user/info
Authorization: Bearer {替换为登录返回的token}
```

## 2. 活动相关接口

### 2.1 活动列表（无需登录）

```http
GET http://localhost:9000/activity/list?page=1&size=10
```

**带筛选条件**:
```http
GET http://localhost:9000/activity/list?page=1&size=10&status=RECRUITING&category=学长火炬
```

**参数说明**:
- page: 页码（默认1）
- size: 每页数量（默认10）
- status: 活动状态（RECRUITING-招募中, ONGOING-进行中, COMPLETED-已结项）
- category: 活动类型（学长火炬、书记驿站、爱心小屋、校友招商、暖冬行动）

### 2.2 活动详情

```http
GET http://localhost:9000/activity/1
Authorization: Bearer {token}
```

### 2.3 创建活动（需要管理员权限）

```http
POST http://localhost:9000/activity/create
Authorization: Bearer {admin的token}
Content-Type: application/json

{
  "title": "测试志愿活动",
  "description": "这是一个测试活动的详细描述...",
  "location": "图书馆一楼大厅",
  "maxParticipants": 20,
  "volunteerHours": 3.0,
  "startTime": "2024-12-01T14:00:00",
  "endTime": "2024-12-01T17:00:00",
  "registrationDeadline": "2024-11-28T23:59:59",
  "category": "书记驿站"
}
```

### 2.4 报名活动

```http
POST http://localhost:9000/activity/register/1
Authorization: Bearer {token}
```

**说明**: 路径参数 /1 表示报名ID为1的活动

### 2.5 我的报名记录

```http
GET http://localhost:9000/activity/myRegistrations
Authorization: Bearer {token}
```

### 2.6 核销时长（需要管理员权限）

```http
POST http://localhost:9000/activity/confirmHours/1
Authorization: Bearer {admin的token}
```

**说明**: 路径参数 /1 表示核销ID为1的报名记录

### 2.7 AI生成活动文案（需要管理员权限）

```http
POST http://localhost:9000/activity/ai/generate
Authorization: Bearer {admin的token}
Content-Type: application/json

{
  "location": "校医院门口",
  "category": "爱心小屋",
  "keywords": "献血车, 爱心服务, 周六"
}
```

**说明**: 如果没有配置AI API，会返回一个默认模板文案

## 3. 完整测试流程

### 场景一：志愿者注册、报名、查看记录

```bash
# 1. 注册新用户
POST /user/register
{
  "username": "volunteer01",
  "password": "password123",
  "realName": "志愿者一号",
  "studentNo": "2024888",
  "phone": "13900139001",
  "email": "volunteer01@university.edu"
}

# 2. 登录获取token
POST /user/login
{
  "username": "volunteer01",
  "password": "password123"
}
# 保存返回的token

# 3. 浏览活动列表
GET /activity/list?page=1&size=10&status=RECRUITING

# 4. 查看活动详情
GET /activity/1
Authorization: Bearer {token}

# 5. 报名活动
POST /activity/register/1
Authorization: Bearer {token}

# 6. 查看我的报名记录
GET /activity/myRegistrations
Authorization: Bearer {token}

# 7. 查看个人信息（时长更新）
GET /user/info
Authorization: Bearer {token}
```

### 场景二：管理员创建活动、核销时长

```bash
# 1. 管理员登录
POST /user/login
{
  "username": "admin",
  "password": "password123"
}
# 保存admin的token

# 2. 使用AI生成活动文案（可选）
POST /activity/ai/generate
Authorization: Bearer {admin_token}
{
  "location": "图书馆",
  "category": "书记驿站",
  "keywords": "值班, 咨询服务, 周日下午"
}

# 3. 创建活动
POST /activity/create
Authorization: Bearer {admin_token}
{
  "title": "书记驿站值班服务",
  "description": "在图书馆书记驿站提供咨询服务...",
  "location": "图书馆三楼",
  "maxParticipants": 15,
  "volunteerHours": 3.0,
  "startTime": "2024-12-08T14:00:00",
  "endTime": "2024-12-08T18:00:00",
  "registrationDeadline": "2024-12-06T23:59:59",
  "category": "书记驿站"
}

# 4. 查看报名情况
GET /activity/list

# 5. 活动结束后核销时长
POST /activity/confirmHours/2
Authorization: Bearer {admin_token}
```

## 4. 常见响应码

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 401 | 未授权（Token无效或过期） |
| 403 | 无权限（如普通用户访问管理员接口） |
| 500 | 服务器错误 |

## 5. 业务错误示例

### 用户名已存在
```json
{
  "code": 500,
  "message": "用户名已存在",
  "data": null
}
```

### 活动名额已满
```json
{
  "code": 500,
  "message": "活动名额已满",
  "data": null
}
```

### Token无效
```json
{
  "code": 401,
  "message": "认证令牌无效或已过期",
  "data": null
}
```

### 权限不足
```json
{
  "code": 403,
  "message": "只有管理员才能创建活动",
  "data": null
}
```

## 6. Postman导入配置

可以创建Environment变量：

```json
{
  "gateway_url": "http://localhost:9000",
  "admin_token": "",
  "user_token": ""
}
```

然后在请求中使用：
- URL: {{gateway_url}}/user/login
- Authorization: Bearer {{admin_token}}

## 7. curl命令示例

### 登录
```bash
curl -X POST http://localhost:9000/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

### 带Token的请求
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X GET http://localhost:9000/user/info \
  -H "Authorization: Bearer $TOKEN"
```

### 报名活动
```bash
curl -X POST http://localhost:9000/activity/register/1 \
  -H "Authorization: Bearer $TOKEN"
```

## 8. 测试数据

系统已预置5个志愿活动：

1. 学长火炬 - 新生入学引导（ID: 1）
2. 书记驿站 - 图书馆值班（ID: 2）
3. 爱心小屋献血车志愿服务（ID: 3）
4. 校友招商大会引导服务（ID: 4）
5. 暖冬行动 - 关爱留守儿童（ID: 5）

可以直接使用这些活动进行测试。

## 9. 注意事项

1. **Token有效期**: 24小时，过期后需要重新登录
2. **时区**: 所有时间使用Asia/Shanghai时区
3. **分页**: page从1开始，不是0
4. **枚举值**: 
   - 角色: ADMIN, VOLUNTEER
   - 活动状态: RECRUITING, ONGOING, COMPLETED, CANCELLED
5. **Redis**: 报名功能需要Redis运行，否则会报错

## 10. 调试技巧

1. **查看服务日志**: 在各服务的控制台查看详细日志
2. **检查Nacos**: 访问 http://localhost:8848/nacos 查看服务注册情况
3. **查看监控**: 访问 http://localhost:9100 查看服务健康状态
4. **数据库验证**: 直接查询数据库确认数据是否写入成功

```sql
-- 查看用户表
SELECT * FROM sys_user;

-- 查看活动表
SELECT * FROM vol_activity;

-- 查看报名记录
SELECT * FROM vol_registration;

-- 查看统计视图
SELECT * FROM v_activity_statistics;
```
