# 部署说明

本项目只保留三种部署形式：本机部署、Docker 单栈部署、Kubernetes 部署。

## 1. 本机部署

目录：

- `deploy/local`
- `deploy/local/nginx/cloud-demo.local.conf`

基本步骤：

```powershell
mysql -u root -p < deploy/common/bootstrap-db.sql
mvn clean install -DskipTests
cd frontend2
npm install
npm run dev
```

后端建议按下面顺序启动：

1. `UserApplication`
2. `ActivityApplication`
3. `AnnouncementApplication`
4. `FeedbackApplication`
5. `GatewayApplication`
6. `MonitorApplication`
7. `McpApplication`

访问：

- 前端开发模式：`http://localhost:3000`
- 本机 Nginx 模式：`http://localhost/`
- 监控后台：`http://localhost/monitor/`
- MCP：`http://localhost/mcp`

## 2. Docker 单栈部署

目录：

- `docker-compose.yml`
- `deploy/docker`

启动：

```powershell
Copy-Item .env.example .env
docker compose up -d --build
```

关闭：

```powershell
docker compose down
```

访问：

- 前台：`http://localhost:8081/`
- 监控后台：`http://localhost:8081/monitor/`
- MCP：`http://localhost:8081/mcp`
- Grafana：`http://localhost:3000`
- Prometheus：`http://localhost:9090`
- Nacos：`http://localhost:8848/nacos`
- MinIO Console：`http://localhost:9006`

流量路径：

```text
Browser -> edge-nginx -> gateway-service -> business services
```

## 3. Kubernetes 部署

目录：

- `deploy/k8s`
- `deploy/k8s/cloud-demo`
- `deploy/k8s/observability`
- `deploy/k8s/scripts`

### kind 多节点部署

本机推荐用 kind 创建 1 个 control-plane + 3 个 worker：

```powershell
powershell -ExecutionPolicy Bypass -File deploy\k8s\scripts\kind-up.ps1
```

脚本会完成：

- 创建 `cloud-demo` kind 集群
- 安装 kind 专用 ingress-nginx
- 加载 `cloud-demo/*:latest` 本地镜像
- 生成本地演示用 `secret.yaml`
- 部署项目资源
- 初始化数据库

查看节点：

```powershell
kubectl get nodes -o wide
```

多节点方案报告见：

```text
docs/deploy/KIND_MULTI_NODE_REPORT.md
```

### 已有 Kubernetes 集群部署

准备密钥：

```powershell
Copy-Item deploy\k8s\cloud-demo\secret.example.yaml deploy\k8s\cloud-demo\secret.yaml
```

替换 `secret.yaml` 内的密码和密钥，然后部署：

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

清理：

```powershell
powershell -ExecutionPolicy Bypass -File deploy\k8s\scripts\delete-all.ps1
```

## Kubernetes 自动扩容

Kubernetes 部署已经加入 HPA：

- `deploy/k8s/cloud-demo/autoscaling.yaml`
- `edge-nginx`：2 到 6 个副本
- `gateway-service`、`user-service`、`activity-service`、`announcement-service`、`feedback-service`、`mcp-service`：3 到 8 个副本
- `monitor-service`：2 到 6 个副本
- 触发条件：CPU 平均利用率 70%，内存平均利用率 80%

HPA 依赖 Metrics Server。`deploy/k8s/scripts/apply-all.ps1` 和 `apply-all.sh` 会在缺失时安装 Metrics Server，并默认为本地演示集群追加 `--kubelet-insecure-tls`。如果你的生产集群 kubelet 证书完整，可以在执行脚本前设置 `CLOUD_DEMO_METRICS_SERVER_INSECURE_TLS=false`。

验证：

```powershell
kubectl top nodes
kubectl -n cloud-demo get hpa
kubectl -n cloud-demo describe hpa activity-service
```

中间件 MySQL、Redis、RabbitMQ、MinIO、Nacos 不做 HPA，它们仍按 StatefulSet/PVC 的方式部署。
