# 校园志愿服务管理平台

这是一个 Spring Boot 3 + Spring Cloud Alibaba + Vue 3 的微服务示例项目。当前仓库只保留三种部署方式：

| 部署方式 | 入口 | 适合场景 |
| --- | --- | --- |
| 本机部署 | `deploy/local` | 开发调试 |
| Docker 单栈 | `docker-compose.yml` / `deploy/docker` | 默认单机运行 |
| Kubernetes | `deploy/k8s` | kind 多节点、多副本、Ingress、自动扩容演示 |

## 项目结构

```text
cloud-demo/
├─ services/              # 后端微服务
├─ frontend2/             # Vue 前端
├─ deploy/
│  ├─ common/             # 共用 SQL
│  ├─ local/              # 本机部署
│  ├─ docker/             # Docker 单栈部署
│  └─ k8s/                # Kubernetes 部署
├─ docker-compose.yml     # 默认 Docker 单栈入口
├─ .env.example           # Docker 默认环境变量模板
└─ docs/deploy/DEPLOY.md  # 三种部署方式说明
```

## 默认 Docker 启动

在项目根目录执行：

```powershell
Copy-Item .env.example .env
docker compose up -d --build
```

访问：

- 前台：`http://localhost:8081/`
- 监控后台：`http://localhost:8081/monitor/`
- MCP：`http://localhost:8081/mcp`
- Grafana：`http://localhost:3000`
- Prometheus：`http://localhost:9090`
- Nacos：`http://localhost:8848/nacos`
- MinIO Console：`http://localhost:9006`

关闭：

```powershell
docker compose down
```

## 本机部署

```powershell
mysql -u root -p < deploy/common/bootstrap-db.sql
mvn clean install -DskipTests
cd frontend2
npm install
npm run dev
```

本机 Nginx 配置在 `deploy/local/nginx/cloud-demo.local.conf`。

## Kubernetes 部署

本机推荐使用 kind 模拟 1 个 control-plane + 3 个 worker：

```powershell
powershell -ExecutionPolicy Bypass -File deploy\k8s\scripts\kind-up.ps1
```

它会创建 `cloud-demo` kind 集群、加载本地镜像、安装 Ingress Controller / Metrics Server、部署项目并初始化数据库。

查看节点：

```powershell
kubectl get nodes -o wide
```

也可以手动部署到已有集群：

先准备密钥：

```powershell
Copy-Item deploy\k8s\cloud-demo\secret.example.yaml deploy\k8s\cloud-demo\secret.yaml
```

然后替换 `secret.yaml` 内的密码和密钥，执行：

```powershell
powershell -ExecutionPolicy Bypass -File deploy\k8s\scripts\apply-all.ps1
powershell -ExecutionPolicy Bypass -File deploy\k8s\scripts\init-db.ps1
```

本机访问：

```powershell
kubectl -n ingress-nginx port-forward svc/ingress-nginx-controller 18081:80
```

hosts 增加：

```text
127.0.0.1 cloud-demo.local grafana.cloud-demo.local prometheus.cloud-demo.local
```

访问：

- `http://cloud-demo.local:18081/`
- `http://grafana.cloud-demo.local:18081/`
- `http://prometheus.cloud-demo.local:18081/`

Kubernetes 部署已包含 HPA 自动扩容配置。HPA 依赖集群安装 Metrics Server，可用下面命令查看状态：

```powershell
kubectl -n cloud-demo get hpa
```

## 更多部署说明

部署文档：

- `docs/deploy/DEPLOY.md`
- `docs/deploy/KIND_MULTI_NODE_REPORT.md`
