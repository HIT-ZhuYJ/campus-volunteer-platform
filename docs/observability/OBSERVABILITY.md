# 可观测栈说明

## 1. 目标

本项目当前已经接入完整的基础可观测栈，用来解决 3 类问题：

1. **服务是否存活、是否健康**
2. **请求/消息是否变慢、失败、重试或熔断**
3. **某个具体实例到底打印了什么日志、产生了什么链路**

---

## 2. 当前组成

shared 层当前包含以下观测组件：

- `otel-collector`：OpenTelemetry Collector，负责接收应用 trace
- `prometheus`：指标抓取与时序数据存储
- `grafana`：统一观测入口
- `loki`：日志存储
- `promtail`：日志采集器
- `tempo`：链路追踪存储

相关配置文件：

- `compose.shared.yml`
- `deploy/observability/otel-collector-config.yml`
- `deploy/observability/prometheus.yml`
- `deploy/observability/promtail-config.yml`
- `deploy/observability/loki-config.yml`
- `deploy/observability/tempo.yml`
- `deploy/observability/grafana/provisioning/datasources/datasources.yml`

---

## 3. 访问地址

当前宿主机可直接访问：

- Grafana：`http://localhost:3000`
- Prometheus：`http://localhost:9090`

Grafana 默认账号：

- 用户名：`admin`
- 密码：`admin`

Grafana 当前已自动接入 3 个数据源：

- `Prometheus`
- `Loki`
- `Tempo`

---

## 4. 数据流说明

### 4.1 指标链路

1. 各 Spring Boot 服务通过 `Actuator + Micrometer` 暴露 `/actuator/prometheus`
2. Prometheus 通过 `docker_sd_configs` 从 Docker 容器发现目标
3. Grafana 从 Prometheus 读取指标并展示

### 4.2 日志链路

1. 业务服务日志会同时输出到容器 stdout/stderr
2. Promtail 通过 `docker_sd_configs` 发现容器并采集实例日志
3. Loki 保存日志流
4. Grafana Explore 通过 Loki 查询日志

补充说明：

- `edge-nginx` 额外保留了 `log/edge/edge-nginx/*.log` 的文件采集兜底
- 业务服务宿主机目录 `log/` 仍保留，便于离线排障

### 4.3 链路追踪

1. 应用通过 `Micrometer Tracing + OpenTelemetry` 发 OTLP trace
2. OTel Collector 接收并转发到 Tempo
3. Grafana 中通过 Tempo 查看 trace

---

## 5. 按容器发现的实现结果

### 5.1 Prometheus：按副本抓取指标

Prometheus 已经不再只按服务别名抓取，例如：

- 旧方式：`user-service-a:8100`

而是按容器实例抓取，例如：

- `stack-a-user-service-2`
- `stack-a-user-service-3`
- `stack-a-activity-service-1`
- `stack-a-activity-service-2`

这意味着 A/B 栈的多副本实例会分别显示为独立 target。

### 5.2 Loki：按实例查询日志

Promtail 已按容器实例写入 Loki 标签，当前可用标签包括：

- `instance`
- `compose_project`
- `compose_service`
- `compose_container_number`
- `stack`
- `stream`

典型 `instance` 值：

- `stack-a-user-service-2`
- `stack-a-user-service-3`
- `stack-a-activity-service-1`
- `stack-a-activity-service-2`
- `shared-mcp-service-1`
- `edge-edge-nginx-1`

---

## 6. Grafana / Loki 常用查询示例

在 Grafana -> Explore -> 选择 `Loki` 后，可以直接使用：

### 按具体实例查日志

```logql
{instance="stack-a-user-service-2"}
```

### 按服务查日志

```logql
{compose_service="user-service", stack="a"}
```

### 查 MCP 服务日志

```logql
{instance="shared-mcp-service-1"}
```

### 查 edge-nginx 日志

```logql
{instance="edge-edge-nginx-1"}
```

### 只看错误关键字

```logql
{compose_service="gateway-service", stack="a"} |= "ERROR"
```

---

## 7. 验证方式

### 7.1 Prometheus Targets

打开：

- `http://localhost:9090/targets`

重点确认：

- `stack-a-user-service-2`
- `stack-a-user-service-3`
- `stack-b-user-service-2`
- `stack-b-user-service-3`

都分别为 `UP`。

### 7.2 Grafana 数据源

打开 Grafana 后检查：

- `Prometheus`
- `Loki`
- `Tempo`

都已加载。

### 7.3 Loki 实例标签

如果要确认实例标签是否已经写入 Loki，可在 Explore 中尝试：

```logql
{instance="stack-a-user-service-2"}
```

若能返回结果，说明实例级日志采集已生效。

---

## 8. 当前安全与运维注意事项

### 8.1 Prometheus

Prometheus 当前为了实现 Docker 容器发现：

- 挂载了 `/var/run/docker.sock`
- 以 `root` 用户运行

这适合当前本地 / 内网环境，但如果未来进入更严格的生产环境，应进一步收紧权限。

### 8.2 Promtail

Promtail 当前为了按容器实例采集日志：

- 也挂载了 `/var/run/docker.sock`
- 也以 `root` 用户运行
- 已通过 `promtail-positions` volume 持久化采集进度，避免每次重启都重扫旧日志

### 8.3 宿主机日志目录

虽然 Loki 已经支持按实例查日志，但宿主机 `log/` 目录仍然有价值：

- 适合离线查看
- 适合不经过 Grafana 的快速排障
- 保留 edge nginx 文件日志兜底

---

## 9. 与其他文档的关系

- 部署细节：见 [../deploy/DEPLOY.md](../deploy/DEPLOY.md)
- 总体架构：见 [../architecture/ARCHITECTURE.md](../architecture/ARCHITECTURE.md)
- 技术作用总览：见 [../overview/TECHNOLOGY_STACK.md](../overview/TECHNOLOGY_STACK.md)
