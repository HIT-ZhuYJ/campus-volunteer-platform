# 🚀 快速开始指南

本指南将帮助你在 **10 分钟内** 快速启动校园志愿服务管理平台。

## 📋 前置检查清单

在开始之前，请确保已安装以下软件：

- ✅ JDK 17+
- ✅ Maven 3.6+
- ✅ MySQL 8.0+
- ✅ Redis 5.0+
- ✅ Nacos 2.x
- ✅ Node.js 16+（前端）

---

## 🎯 快速启动步骤

### 步骤 1：初始化数据库（2 分钟）

1. 启动 MySQL 服务

2. 导入数据库
```bash
# 方式1：命令行导入
mysql -u root -p < database/init.sql

# 方式2：MySQL Workbench / Navicat
# 打开 database/init.sql 文件，执行全部语句
```

> **重要**：`init.sql` 会先 **`DROP DATABASE IF EXISTS volunteer_platform`**，再重建库、表、索引、视图与测试数据。若该库名在本机已有重要数据，请先备份。

3. 验证数据库
```sql
USE volunteer_platform;
SHOW TABLES;
-- 期望看到：sys_user、vol_activity、vol_registration、v_activity_statistics（视图）
SELECT COUNT(*) FROM sys_user;        -- 应为 11（1 管理员 + 10 志愿者）
SELECT COUNT(*) FROM vol_activity;    -- 应为 20
SELECT COUNT(*) FROM vol_registration; -- 应为 54
```

4. 修改数据库连接密码（默认为 `123888`）

若需要修改，编辑各服务的 `src/main/resources/application.properties`：
```properties
spring.datasource.password=你的密码
```

---

### 步骤 2：启动 Redis（1 分钟）

```bash
# Windows
redis-server.exe redis.windows.conf

# Linux/Mac
redis-server

# 验证（应返回 PONG）
redis-cli ping
```

---

### 步骤 3：启动 Nacos（2 分钟）

```bash
# Windows
cd nacos/bin
startup.cmd -m standalone

# Linux/Mac
cd nacos/bin
sh startup.sh -m standalone
```

验证：访问 http://localhost:8848/nacos（账号/密码：`nacos/nacos`）

---

### 步骤 4：编译后端项目（2 分钟）

在 `services` 目录下执行：
```bash
cd services
mvn clean install -DskipTests
```

或在仓库根目录：
```bash
mvn clean install -DskipTests
```

---

### 步骤 5：启动后端服务（按顺序，3 分钟）

#### 5.1 user-service（端口 8100）
```bash
cd services/user-service
mvn spring-boot:run
```
验证：日志出现 `nacos registry ... user-service ... register finished`

#### 5.2 activity-service（端口 8200）
```bash
cd services/activity-service
mvn spring-boot:run
```
> **AI 文案功能**：若需使用 AI 生成活动描述，在启动前设置环境变量 `DEEPSEEK_API_KEY`；未设置则自动回退到内置模板。

#### 5.3 gateway-service（端口 9000）
```bash
cd services/gateway-service
mvn spring-boot:run
```
验证：日志出现 `Netty started on port 9000`

#### 5.4 monitor-service（端口 9100，可选）
```bash
cd services/monitor-service
mvn spring-boot:run
```

---

### 步骤 6：启动前端（2 分钟）

```bash
cd frontend
npm install
npm run dev
```

验证：终端输出 `Local: http://localhost:3000/`，浏览器打开即可。

---

## ✅ 验证安装

### 1. 检查 Nacos 服务注册

访问 http://localhost:8848/nacos → "服务管理" → "服务列表"，应看到：
- ✅ `user-service`
- ✅ `activity-service`
- ✅ `gateway-service`
- ✅ `monitor-service`（如果启动了）

### 2. 测试登录接口

```bash
curl -X POST http://localhost:9000/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
# 返回 token 字段即为成功
```

### 3. 访问前端

打开浏览器：http://localhost:3000

你应该看到渐变背景 + 动态浮动元素的现代化登录页面，底部有三个快速登录按钮。

---

## 🎉 测试账号

所有账号密码均为 `password123`。

### 管理员账号

| 账号 | 姓名 | 角色 |
|------|------|------|
| admin | 管理员 | ADMIN |

### 志愿者账号（共 10 个）

| 账号 | 姓名 | 学号 | 累计时长 | 说明 |
|------|------|------|---------|------|
| student01 | 张三 | 2021101 | 11.00h | 参与活动最多 |
| student02 | 李四 | 2021102 | 6.00h  | |
| student03 | 王五 | 2021103 | 6.50h  | |
| student04 | 赵六 | 2021104 | 7.00h  | |
| student05 | 陈七 | 2021105 | 6.50h  | |
| student06 | 孙八 | 2021106 | 5.50h  | |
| student07 | 周九 | 2021107 | 0.00h  | 新用户，有报名但无核销 |
| student08 | 吴十 | 2021108 | 0.00h  | 新用户 |
| student09 | 郑十一 | 2021109 | 0.00h | 新用户 |
| student10 | 王十二 | 2021110 | 0.00h | 新用户 |

---

## 🗂️ 测试数据场景说明

`database/init.sql` 中内置了 **20 个活动**，覆盖所有业务状态，方便逐一测试：

| 场景 | 活动ID | 说明 |
|------|--------|------|
| **招募中**（首页/活动列表可见） | 1～5 | 截止日：3/28、3/30、4/5、4/10、4/20，按紧迫度排序 |
| **招募未开始** | 6、7 | 招募窗口分别在 5 月、7 月开放 |
| **招募截止，活动未开始** | 8、9 | 报名窗口已关闭但活动尚未举行 |
| **活动进行中**（签到模块可见） | 10、11 | 活动 10 = 今日全天；活动 11 跨三天，各有签到/未签到记录 |
| **已结束，待核销**（时长核销模块可见） | 12、13、14 | 有的已部分核销，有的尚未核销，测试核销操作 |
| **已结项 COMPLETED** | 15～18 | 15–17 全员完结；18 部分待核销（演示结项后不可再选） |
| **已取消 CANCELLED** | 19、20 | 不出现在任何操作模块中 |

---

## 🖥️ 系统功能说明

### 志愿者端功能

#### 首页（`/home`）
- 展示个人统计：累计时长、参与活动数、**已签到**数、已核销数
- "最新活动"区域仅显示**当前处于招募窗口内**的活动，按报名截止日升序排列（最紧急优先）

#### 活动列表（`/activities`）
- 支持按**活动状态**、**招募阶段**、**活动类型**三组条件筛选，交互采用胶囊 Pill 样式
- 全部活动按**报名截止时间升序**排列，最快截止的排在最前
- 表格直接显示报名进度条，名额告急时高亮红色

#### 活动详情（`/activity/:id`）
- 顶部 Hero 区：活动标题、阶段标签、类别标签
- 右侧：名额进度、报名操作、志愿时长
- 报名按钮仅在"招募中"状态且有剩余名额时可用

#### 个人中心（`/my`）
- 个人信息卡片（带渐变头像区）
- 4 项统计：累计时长 / 参与活动 / 已签到 / 已核销
- 志愿足迹表格：展示全部报名记录及签到、核销状态

---

### 管理员端功能（`/admin/*`）

#### 活动管理（`/admin/activities`）
- 展示全部活动，**已结项（绿色背景）** 和 **已取消（红色删除线）** 有明显视觉区分
- 操作按钮：查看 / 编辑 / 取消活动 / **结项** / 删除
- 编辑、取消、结项按钮仅对 `RECRUITING` 状态活动可见
- 表单含完整的时间跨字段校验：`开始 < 结束`、`招募开始 < 截止 ≤ 活动开始`

#### 发布活动（`/admin/create`）
- 分区布局：左侧"基本信息 + 活动介绍"，右侧"时间安排"
- AI 生成活动描述：填写类型和地点后输入关键词即可生成（需配置 `DEEPSEEK_API_KEY`）

#### 活动签到（`/admin/checkin`）
- 仅列出**当前进行中**的活动（`start_time ≤ 现在 ≤ end_time`，且未取消）
- 展示报名名单，逐人标记签到，已签到记录不可重复操作

#### 时长核销（`/admin/confirm`）
- 仅列出**已结束但尚未结项**的活动（`end_time < 现在`，排除 CANCELLED 和 COMPLETED）
- 展示"已签到且未核销"的可核销人员
- 核销后显示**核销时间**列
- > **推荐工作流**：活动结束 → 在此模块完成所有核销 → 回到"活动管理"点击**结项**

#### 时长查询（`/admin/hours`）
- 查看所有志愿者的累计时长排行，支持按姓名/学号/用户名模糊搜索

---

## 🔄 完整业务流程

```
管理员发布活动
    │
    ▼
招募开始时间到 → 志愿者可报名
    │
    ▼
报名截止 → 活动开始 → 管理员在「活动签到」模块标记签到
    │
    ▼
活动结束 → 管理员在「时长核销」模块逐人核销时长
    │
    ▼
全部核销完毕 → 管理员在「活动管理」点击「结项」
    │
    ▼
活动变为 COMPLETED，从核销列表消失，在管理列表以绿色高亮显示
```

---

## 🐛 常见问题排查

### 问题 1：服务启动失败

**排查步骤**：
```bash
# Windows - 检查端口占用
netstat -ano | findstr :8100
netstat -ano | findstr :9000

# Linux/Mac
lsof -i :8100
```

### 问题 2：服务无法注册到 Nacos（`serverAddr='null'` / `Client not connected`）

**解决方案**：
1. 先启动 Nacos，再启动各微服务（Nacos 2.x 使用 8848 + 9848 端口）。
2. 确认配置中包含：
```properties
spring.cloud.nacos.server-addr=127.0.0.1:8848
spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848
spring.cloud.nacos.discovery.fail-fast=false
```
3. 若日志仍显示 `serverAddr=null`，检查系统/IDE 是否存在覆盖配置的空环境变量（如 `SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR`），删除或改为 `127.0.0.1:8848`。

### 问题 3：前端登录 503 错误

**解决方案**：
1. 确认 `user-service` 和 `gateway-service` 都已正常启动。
2. 在 Nacos 控制台确认 `user-service` 已注册。
3. 确认 `gateway-service` 的 pom.xml 中有 `spring-cloud-starter-loadbalancer` 依赖。
4. 按顺序重启：`user-service` → `gateway-service`。

### 问题 4：CORS 跨域错误

检查 `gateway-service/application.properties` 中是否使用了 `allowed-origin-patterns`（而非 `allowed-origins`）：
```properties
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-origin-patterns=*
spring.cloud.gateway.globalcors.cors-configurations.[/**].allow-credentials=true
```

### 问题 5：MyBatis-Plus 启动报错

确认使用 Spring Boot 3 专版并且 `common` 模块配置了 `<skip>true</skip>`：
```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.9</version>
</dependency>
```

### 问题 6：前端无法加载

```bash
# 清除依赖重装
rm -rf node_modules package-lock.json
npm install
npm run dev
```

检查 Node.js 版本（需 16+）：`node -v`

---

## 📊 服务端口一览

| 服务 | 端口 | 访问地址 | 说明 |
|------|------|---------|------|
| Nacos | 8848 | http://localhost:8848/nacos | 服务注册中心 |
| user-service | 8100 | — | 用户与认证服务 |
| activity-service | 8200 | — | 活动与报名服务 |
| gateway-service | 9000 | http://localhost:9000 | API 网关（统一入口） |
| monitor-service | 9100 | http://localhost:9100 | Spring Boot Admin 监控 |
| frontend | 3000 | http://localhost:3000 | Vue 3 前端 |

---

## 📝 启动检查清单

- [ ] MySQL 已启动并执行 `database/init.sql`
- [ ] Redis 已启动（`redis-cli ping` 返回 PONG）
- [ ] Nacos 已启动，控制台可访问
- [ ] `user-service`、`activity-service`、`gateway-service` 已启动并注册到 Nacos
- [ ] 前端 `npm run dev` 正常运行
- [ ] 使用 `admin / password123` 能成功登录
- [ ] 首页"最新活动"区域显示招募中的活动（按截止日升序）
- [ ] 管理后台能查看 20 条活动数据

全部完成？恭喜！🎉 现在可以开始探索系统的各项功能了！

---

## 💡 开发提示

1. **IDEA Run Dashboard**：同时管理多个 Spring Boot 服务，推荐使用
2. **响应式测试**：F12 → 设备模拟器，可验证移动端布局
3. **数据对账**：`v_activity_statistics` 视图可用于数据库层面核查各活动的实际报名/签到/核销数
4. **Redis Key**：活动名额以 `activity:stock:{id}` 为 key 缓存，可用 `redis-cli keys "activity:*"` 查看

---

💬 遇到问题？查看 [常见问题](#-常见问题排查) 或提交 Issue。
