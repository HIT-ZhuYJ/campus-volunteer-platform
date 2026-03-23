# 校园志愿服务管理平台 - 前端

基于 Vue 3 + Element Plus 的现代化前端应用

## 技术栈

- **Vue 3** - 渐进式JavaScript框架
- **Vite** - 下一代前端构建工具
- **Vue Router** - 官方路由管理器
- **Pinia** - Vue状态管理
- **Element Plus** - 基于Vue 3的组件库
- **Axios** - HTTP客户端
- **ECharts** - 数据可视化（可选）
- **Day.js** - 日期处理库

## 项目结构

```
frontend/
├── public/                  # 静态资源
├── src/
│   ├── api/                # API接口
│   │   ├── user.js         # 用户相关接口
│   │   └── activity.js     # 活动相关接口
│   ├── components/         # 公共组件
│   │   └── Layout.vue      # 布局组件
│   ├── router/             # 路由配置
│   │   └── index.js
│   ├── store/              # 状态管理
│   │   └── user.js         # 用户状态
│   ├── utils/              # 工具函数
│   │   └── request.js      # axios封装
│   ├── views/              # 页面组件
│   │   ├── Login.vue       # 登录页
│   │   ├── Register.vue    # 注册页
│   │   ├── Home.vue        # 首页
│   │   ├── ActivityList.vue    # 活动列表
│   │   ├── ActivityDetail.vue  # 活动详情
│   │   ├── MyCenter.vue    # 个人中心
│   │   └── admin/          # 管理员页面
│   │       ├── AdminLayout.vue
│   │       ├── ActivityManage.vue
│   │       ├── ActivityCreate.vue
│   │       └── HoursConfirm.vue
│   ├── App.vue             # 根组件
│   └── main.js             # 入口文件
├── index.html              # HTML模板
├── package.json            # 依赖配置
├── vite.config.js          # Vite配置
└── README.md
```

## 快速开始

### 1. 安装依赖

```bash
cd frontend
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

访问: http://localhost:3000

### 3. 构建生产版本

```bash
npm run build
```

## 功能模块

### 用户端功能

#### 1. 认证模块
- **登录** (`/login`)
  - 用户名/密码登录
  - 自动跳转（管理员→后台，志愿者→首页）
- **注册** (`/register`)
  - 用户信息填写
  - 表单验证

#### 2. 首页 (`/home`)
- 数据统计卡片
  - 累计志愿时长
  - 参与活动数
  - 已完成活动
  - 已核销次数
- 最新活动展示

#### 3. 活动列表 (`/activities`)
- 活动筛选（状态、类型）
- 分页展示
- 快速查看详情

#### 4. 活动详情 (`/activity/:id`)
- 完整活动信息
- 报名状态显示
- 一键报名功能

#### 5. 个人中心 (`/my`)
- 个人信息展示
- 累计时长统计
- 志愿足迹列表
- 签到/核销状态

### 管理员功能

#### 1. 活动管理 (`/admin/activities`)
- 活动列表展示
- 查看/删除活动
- 分页管理

#### 2. 发布活动 (`/admin/create`)
- 完整表单填写
- AI智能生成文案
- 活动信息配置

#### 3. 时长核销 (`/admin/confirm`)
- 报名记录查看
- 一键核销时长
- 状态管理

## 核心特性

### 1. 自动Token管理
```javascript
// 请求自动携带Token
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})
```

### 2. 路由守卫
```javascript
// 自动检查登录状态和权限
router.beforeEach((to, from, next) => {
  if (to.meta.requireAuth && !token) {
    next('/login')
  } else if (to.meta.requireAdmin && role !== 'ADMIN') {
    next('/home')
  } else {
    next()
  }
})
```

### 3. 统一错误处理
```javascript
// 401自动跳转登录
// 其他错误统一提示
axios.interceptors.response.use(
  response => { /* ... */ },
  error => {
    ElMessage.error(error.message)
    return Promise.reject(error)
  }
)
```

## API代理配置

开发环境自动代理到后端：

```javascript
// vite.config.js
export default {
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:9000',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
}
```

所有请求路径前缀 `/api` 会自动转发到 `http://localhost:9000`

## 测试账号

| 用户名 | 密码 | 角色 | 功能 |
|--------|------|------|------|
| admin | password123 | 管理员 | 全部功能 |
| student01 | password123 | 志愿者 | 用户端功能 |
| student02 | password123 | 志愿者 | 用户端功能 |

## 开发建议

### 1. 组件开发规范
- 使用组合式API (Composition API)
- 合理拆分组件
- 统一代码风格

### 2. 命名规范
- 组件名：大驼峰 (PascalCase)
- 文件名：大驼峰 (PascalCase.vue)
- 变量名：小驼峰 (camelCase)
- 常量名：全大写下划线 (UPPER_SNAKE_CASE)

### 3. 状态管理
- 全局状态使用 Pinia
- 组件状态使用 ref/reactive
- 避免过度使用全局状态

## 常见问题

### 1. 安装依赖失败
```bash
# 清除缓存
npm cache clean --force
# 重新安装
npm install
```

### 2. 端口被占用
修改 `vite.config.js`:
```javascript
export default {
  server: {
    port: 3001  // 改为其他端口
  }
}
```

### 3. 跨域问题
确保后端Gateway已启动在 http://localhost:9000

### 4. Token过期
Token有效期24小时，过期后自动跳转登录页

## 浏览器支持

- Chrome (推荐)
- Firefox
- Safari
- Edge

不支持IE11及以下版本

## 部署

### 开发环境
```bash
npm run dev
```

### 生产构建
```bash
npm run build
# 构建产物在 dist/ 目录
```

### Nginx部署示例
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
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 贡献指南

欢迎提交Issue和Pull Request！

## 许可证

MIT License
