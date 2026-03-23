# 快速启动指南 ⚡

> 5分钟快速启动校园志愿服务管理平台

## 前置检查清单

在开始之前，请确保已安装：

- [ ] JDK 17
- [ ] Maven 3.8+
- [ ] MySQL 8.0
- [ ] Redis
- [ ] Nacos 2.x

## 快速启动步骤

### 第1步：启动基础设施 (3分钟)

#### 1.1 启动MySQL
```bash
# 确保MySQL服务正在运行
# Windows: 服务管理器中启动MySQL
# Linux: sudo systemctl start mysql
```

#### 1.2 启动Redis
```bash
# Windows
redis-server.exe

# Linux
sudo systemctl start redis
# 或
redis-server
```

#### 1.3 启动Nacos
```bash
# Windows
cd nacos/bin
startup.cmd -m standalone

# Linux/Mac
cd nacos/bin
sh startup.sh -m standalone
```

**验证**: 访问 http://localhost:8848/nacos (用户名密码: nacos/nacos)

### 第2步：初始化数据库 (1分钟)

```bash
# 方式一：命令行执行
mysql -u root -p < database/init.sql

# 方式二：MySQL客户端执行
# 打开MySQL Workbench或其他工具
# 打开并执行 database/init.sql
```

**验证**:
```sql
USE volunteer_platform;
SHOW TABLES;
-- 应该看到3个表: sys_user, vol_activity, vol_registration
```

### 第3步：编译项目 (1分钟)

```bash
cd D:\clouddemo\cloud-demo
mvn clean install -DskipTests
```

### 第4步：启动服务 (1分钟)

#### 方式A：一键启动（推荐）

```bash
# Windows
start-all.bat

# Linux/Mac
chmod +x start-all.sh
./start-all.sh
```

#### 方式B：手动启动

打开4个命令行窗口，分别执行：

```bash
# 窗口1 - 监控服务
cd services/monitor-service
mvn spring-boot:run

# 窗口2 - 网关服务
cd services/gateway-service
mvn spring-boot:run

# 窗口3 - 用户服务
cd services/user-service
mvn spring-boot:run

# 窗口4 - 活动服务
cd services/activity-service
mvn spring-boot:run
```

### 第5步：验证服务 (30秒)

#### 5.1 检查Nacos服务注册
访问: http://localhost:8848/nacos

在"服务管理 -> 服务列表"中应该看到4个服务：
- gateway-service
- user-service  
- activity-service
- monitor-service

#### 5.2 检查监控中心
访问: http://localhost:9100

应该看到所有服务的健康状态为UP。

#### 5.3 测试API接口

**测试登录接口**:
```bash
curl -X POST http://localhost:9000/user/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"password123\"}"
```

**预期返回**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "realName": "管理员",
      "role": "ADMIN"
    }
  }
}
```

**测试活动列表**:
```bash
curl http://localhost:9000/activity/list?page=1&size=10
```

## 🎉 恭喜！系统启动成功

现在你可以：

1. **使用Postman/Apifox测试API** - 参考 `API_TEST.md`
2. **查看监控面板** - http://localhost:9100
3. **开发前端页面** - 对接网关地址 http://localhost:9000
4. **查看Nacos控制台** - http://localhost:8848/nacos

## 测试账号

| 用户名 | 密码 | 角色 | 功能 |
|--------|------|------|------|
| admin | password123 | 管理员 | 创建活动、核销时长 |
| student01 | password123 | 志愿者 | 报名活动、查看记录 |
| student02 | password123 | 志愿者 | 报名活动、查看记录 |

## 常见问题快速解决

### ❌ 服务启动失败

**现象**: 服务启动报错

**解决方法**:
```bash
# 1. 检查端口占用
netstat -ano | findstr "9000"  # Windows
lsof -i:9000                    # Linux

# 2. 检查Nacos是否启动
curl http://localhost:8848/nacos

# 3. 查看日志
tail -f logs/user-service.log
```

### ❌ 数据库连接失败

**现象**: `Could not get JDBC Connection`

**解决方法**:
```bash
# 1. 检查MySQL服务
mysql -u root -p -e "SELECT 1"

# 2. 检查数据库是否创建
mysql -u root -p -e "SHOW DATABASES LIKE 'volunteer%'"

# 3. 修改配置文件中的密码
# services/user-service/src/main/resources/application.properties
# spring.datasource.password=你的密码
```

### ❌ Redis连接失败

**现象**: `Cannot get Jedis connection`

**解决方法**:
```bash
# 检查Redis服务
redis-cli ping
# 应该返回: PONG
```

### ❌ 服务未注册到Nacos

**现象**: Nacos控制台看不到服务

**解决方法**:
```bash
# 1. 确认Nacos地址配置正确
# application.properties中:
# spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848

# 2. 检查网络连接
curl http://localhost:8848/nacos/v1/ns/instance/list?serviceName=user-service

# 3. 查看服务日志
```

## 下一步

### 📖 深入学习
- 阅读 `README.md` 了解项目详情
- 阅读 `ARCHITECTURE.md` 理解架构设计
- 阅读 `API_TEST.md` 学习接口测试

### 🔧 开发调试
- 使用IDEA/Eclipse导入项目
- 配置断点调试
- 查看SQL执行日志

### 🚀 功能扩展
- 开发前端界面
- 添加更多业务功能
- 集成更多中间件

### 📊 性能测试
- 使用JMeter测试并发
- 优化慢查询SQL
- 调整JVM参数

## 💡 开发技巧

### 修改代码后重启服务
```bash
# 1. 停止服务 (Ctrl+C)
# 2. 重新启动
mvn spring-boot:run
```

### 查看实时日志
```bash
# Windows (PowerShell)
Get-Content logs/user-service.log -Wait -Tail 50

# Linux
tail -f logs/user-service.log
```

### 快速清理重建
```bash
# 清理编译缓存
mvn clean

# 重新编译
mvn install -DskipTests

# 重启服务
```

## 📞 获取帮助

遇到问题？

1. 检查日志文件 `logs/*.log`
2. 查看 `DEPLOY.md` 详细部署指南
3. 搜索错误信息
4. 提交Issue描述问题

---

**祝你使用愉快！** 🎊
