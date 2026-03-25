# 前端快速启动指南

## 🚀 快速开始

### 1. 确保后端已启动

前端需要连接后端API，请先启动后端服务：

```bash
# 在项目根目录
cd D:\clouddemo\cloud-demo

# 确保以下服务已启动：
# - MySQL (3306)
# - Redis (6379)
# - Nacos (8848)
# - gateway-service (9000)
# - user-service (8100)
# - activity-service (8200)
```

### 2. 安装Node.js

前端需要Node.js环境：

- 下载地址: https://nodejs.org/
- 推荐版本: Node.js 16+ 或 18+
- 验证安装:
  ```bash
  node -v
  npm -v
  ```

### 3. 安装前端依赖

```bash
# 进入前端目录
cd frontend

# 安装依赖（首次运行需要）
npm install
```

**注意**: 首次安装可能需要3-5分钟，请耐心等待。

### 4. 启动开发服务器

```bash
npm run dev
```

看到以下提示表示启动成功：
```
  VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:3000/
  ➜  Network: use --host to expose
```

### 5. 访问应用

浏览器打开: **http://localhost:3000**

## 📱 功能演示

### 测试账号

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | password123 | 管理员 | 发布活动、签到、核销时长 |
| student01 | password123 | 志愿者 | 张三，累计 11.0h |
| student02 | password123 | 志愿者 | 李四，累计 6.0h |
| student03～student10 | password123 | 志愿者 | 其他测试账号 |

> 💡 登录页面支持快速登录标签，点击即填充账号密码。

### 用户端流程

1. **注册新账号**
   - 访问 http://localhost:3000/register
   - 填写完整信息
   - 注册成功后跳转登录

2. **登录系统**
   - 使用 student01 / password123
   - 自动跳转到首页

3. **浏览活动**
   - 首页查看**当前招募中**的最新活动（按截止时间升序）
   - 点击"查看更多"进入活动列表
   - 可按状态（含已取消）和类型筛选，列表亦按截止时间升序

4. **报名活动**
   - 点击活动卡片进入详情页
   - 点击"立即报名"按钮
   - 报名成功后显示"已报名"

5. **查看个人中心**
   - 点击顶部导航"个人中心"
   - 查看累计时长和报名记录

### 管理员流程

1. **登录管理员账号**
   - 使用 admin / password123
   - 自动跳转到管理后台

2. **查看活动管理**
   - 左侧菜单"活动管理"
   - 查看所有活动列表

3. **发布新活动**
   - 点击"发布活动"，表单分组展示（基本信息 / 时间安排 / 活动介绍）
   - 可使用 AI 一键生成活动文案（需配置 **`DEEPSEEK_API_KEY`**，见根目录 `README.md`）
   - 时间顺序：招募开始 < 招募截止 ≤ 活动开始 < 活动结束（后端强制校验）

4. **签到管理**
   - 左侧菜单"活动签到"
   - 选择活动，为已参与的志愿者标记签到

5. **核销时长**
   - 点击"时长核销"，仅展示**已结束且未结项**的活动
   - 选择活动，查看报名记录
   - 点击"核销"完成时长核销，系统自动记录核销时间

6. **结项活动**
   - 在"活动管理"中找到已完成核销的活动
   - 点击"结项"将活动标记为 `COMPLETED`（绿色高亮行）
   - 已结项活动不再出现在时长核销模块

## 🛠️ 开发相关

### 热更新

代码修改后自动刷新，无需手动重启。

### 目录结构

```
frontend/src/
├── api/              # API接口定义
├── components/       # 公共组件
├── router/           # 路由配置
├── store/            # 状态管理
├── utils/            # 工具函数
├── views/            # 页面组件
│   ├── Login.vue
│   ├── Register.vue
│   ├── Home.vue
│   ├── ActivityList.vue
│   ├── ActivityDetail.vue
│   ├── MyCenter.vue
│   └── admin/
│       ├── AdminLayout.vue
│       ├── ActivityManage.vue   # 活动管理（结项/取消行高亮）
│       ├── ActivityCreate.vue   # 发布活动（分组卡片表单）
│       ├── ActivityCheckIn.vue  # 签到管理
│       ├── VolunteerHours.vue   # 时长统计
│       └── HoursConfirm.vue     # 时长核销（仅已结束未结项）
├── App.vue
└── main.js
```

### 页面路由

| 路径 | 页面 | 权限 |
|------|------|------|
| `/login` | 登录页 | 公开 |
| `/register` | 注册页 | 公开 |
| `/home` | 首页 | 需登录 |
| `/activities` | 活动列表 | 需登录 |
| `/activity/:id` | 活动详情 | 需登录 |
| `/my` | 个人中心 | 需登录 |
| `/admin/activities` | 活动管理 | 需管理员 |
| `/admin/create` | 发布活动 | 需管理员 |
| `/admin/checkin` | 签到管理 | 需管理员 |
| `/admin/hours` | 时长统计 | 需管理员 |
| `/admin/confirm` | 时长核销 | 需管理员 |

## ❓ 常见问题

### Q1: npm install 失败

**解决方法**:
```bash
# 清除缓存
npm cache clean --force

# 使用国内镜像
npm config set registry https://registry.npmmirror.com

# 重新安装
npm install
```

### Q2: 启动后页面空白

**检查项**:
1. 浏览器控制台是否有错误
2. 后端服务是否启动（必须先启动后端）
3. 网络代理配置是否正确

### Q3: 登录后提示401

**原因**: 后端Gateway未启动或地址不对

**解决**:
1. 确认 http://localhost:9000 可访问
2. 检查 vite.config.js 中的代理配置

### Q4: 接口请求失败

**检查**:
```bash
# 1. 后端是否启动
curl http://localhost:9000/activity/list?page=1&size=10

# 2. 查看浏览器Network面板
# F12 -> Network -> 查看请求详情
```

### Q5: 端口3000被占用

**修改端口**:
编辑 `vite.config.js`:
```javascript
export default {
  server: {
    port: 3001  // 改为其他端口
  }
}
```

## 📊 性能优化

### 1. 首次加载慢

首次安装依赖较慢是正常的，后续启动会很快。

### 2. 开发环境优化

```bash
# 清除缓存
npm cache clean --force

# 删除node_modules重新安装
rm -rf node_modules
npm install
```

## 🌐 生产构建

### 构建命令

```bash
npm run build
```

### 构建产物

```
dist/
├── index.html
├── assets/
│   ├── index-xxx.js
│   └── index-xxx.css
└── ...
```

### 部署到服务器

1. 将 `dist/` 目录上传到服务器
2. 配置Nginx:
```nginx
server {
    listen 80;
    server_name volunteer.example.com;
    root /var/www/frontend/dist;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:9000;
    }
}
```

## 📝 开发建议

1. **使用Vue DevTools**
   - Chrome扩展: Vue.js devtools
   - 方便调试Vue组件

2. **代码风格**
   - 使用ESLint规范代码
   - 统一缩进和命名

3. **组件复用**
   - 抽取公共组件
   - 避免重复代码

## 🎯 下一步

- ✅ 前端已完成，可以开始使用
- 📖 查看 `frontend/README.md` 了解更多
- 🔧 根据需求自定义页面样式
- 🚀 添加更多功能模块

---

**遇到问题？** 查看浏览器控制台错误信息，或检查后端服务是否正常运行。
