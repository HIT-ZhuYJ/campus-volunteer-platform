# Kubernetes 部署

本目录只保存 Kubernetes 部署需要的资源。

## 文件

- `namespaces.yaml`：命名空间
- `kind-config.yaml`：1 个 control-plane + 3 个 worker 的 kind 集群配置
- `cloud-demo/configmap.yaml`：共享配置
- `cloud-demo/secret.example.yaml`：密钥模板
- `cloud-demo/middleware.yaml`：MySQL、Redis、RabbitMQ、MinIO、Nacos
- `cloud-demo/apps.yaml`：后端微服务
- `cloud-demo/edge-nginx.yaml`：前端和项目边缘代理
- `cloud-demo/ingress.yaml`：Ingress
- `cloud-demo/autoscaling.yaml`：HPA 自动扩容
- `observability/*`：Prometheus、Grafana、Loki、Tempo、OTel Collector
- `scripts/*`：部署、清理、初始化数据库

## kind 多节点部署

```powershell
powershell -ExecutionPolicy Bypass -File deploy\k8s\scripts\kind-up.ps1
```

脚本会创建 `cloud-demo` kind 集群、安装 kind 专用 ingress-nginx、加载项目镜像、部署资源并初始化数据库。

查看节点：

```powershell
kubectl get nodes -o wide
```

## 已有集群部署

```powershell
Copy-Item deploy\k8s\cloud-demo\secret.example.yaml deploy\k8s\cloud-demo\secret.yaml
powershell -ExecutionPolicy Bypass -File deploy\k8s\scripts\apply-all.ps1
powershell -ExecutionPolicy Bypass -File deploy\k8s\scripts\init-db.ps1
```

## 访问

```powershell
kubectl -n ingress-nginx port-forward svc/ingress-nginx-controller 18081:80
```

hosts：

```text
127.0.0.1 cloud-demo.local grafana.cloud-demo.local prometheus.cloud-demo.local
```

地址：

- `http://cloud-demo.local:18081/`
- `http://grafana.cloud-demo.local:18081/`
- `http://prometheus.cloud-demo.local:18081/`

## 自动扩容

HPA 配置在 `cloud-demo/autoscaling.yaml`。它会根据 CPU 和内存利用率扩缩无状态服务副本。

`scripts/apply-all.ps1` 和 `scripts/apply-all.sh` 会在缺失时安装 Metrics Server。默认会为本地演示集群追加 `--kubelet-insecure-tls`；如果你的生产集群 kubelet 证书完整，可以在执行脚本前设置 `CLOUD_DEMO_METRICS_SERVER_INSECURE_TLS=false`。

查看：

```powershell
kubectl -n cloud-demo get hpa
kubectl -n cloud-demo describe hpa activity-service
```

如果 `TARGETS` 显示 `<unknown>`，说明集群缺少 Metrics Server 或指标还没有采集完成。
