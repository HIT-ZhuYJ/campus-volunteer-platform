# Kubernetes 单节点部署完成后的系统架构说明



## 1. 整体架构

Kubernetes 单节点部署完成后，系统运行在一台主机上的一个 Kubernetes Node 内。该 Node 上同时运行三个主要命名空间：

第一，`ingress-nginx` 命名空间。该命名空间运行 `ingress-nginx-controller`，负责接收集群外部 HTTP 请求，并根据 Ingress 规则把请求转发到项目内部入口服务。

第二，`cloud-demo` 命名空间。该命名空间承载项目核心业务系统，包括 `edge-nginx`、`gateway-service`、四个业务微服务、`monitor-service`、`mcp-service`，以及 MySQL、Redis、RabbitMQ、MinIO、Nacos 等基础中间件。

第三，`observability` 命名空间。该命名空间承载 Prometheus、Grafana、Loki、Tempo、OpenTelemetry Collector 等可观测组件，用于指标采集、日志聚合、链路追踪和运行状态展示。

从架构层次看，部署完成后的系统可以分为五层：外部接入层、边缘代理层、网关与服务治理层、业务微服务层、平台支撑与可观测层。所有这些层都运行在同一个 Kubernetes Node 上，但在 Kubernetes 内部通过 Namespace、Service、Deployment、StatefulSet 和 ConfigMap 等资源进行逻辑隔离。



## 8. 文字版架构图



用户或 MCP 客户端首先访问 Kubernetes 集群外部入口。请求进入 `ingress-nginx-controller` 后，由 `cloud-demo-edge` Ingress 根据 `cloud-demo.local` 域名和路径规则转发到 `cloud-demo` 命名空间中的 `edge-nginx` Service。

`edge-nginx` 是项目统一边缘入口。它一方面提供前端静态资源，另一方面根据路径把请求分流到内部服务。普通业务 API 请求进入 `gateway-service`；监控后台请求进入 `monitor-service`；MCP 协议相关请求进入 `mcp-service`；Grafana 页面请求进入 `observability` 命名空间中的 Grafana。

`gateway-service` 收到业务 API 请求后，根据网关路由规则把请求转发到 `user-service`、`activity-service`、`announcement-service` 或 `feedback-service`。这些业务服务通过 Kubernetes Service 名称互相访问，并通过 Nacos 保持服务注册发现能力。

业务服务处理请求时，会访问平台中间件。需要读写结构化业务数据时访问 MySQL；需要缓存、限流或库存状态时访问 Redis；需要异步通知其他服务时访问 RabbitMQ；需要保存图片等二进制文件时访问 MinIO。

与此同时，Prometheus、Loki、Tempo 和 OpenTelemetry Collector 在旁路持续采集指标、日志和链路信息。Grafana 读取这些可观测数据并提供展示界面。

## 9. 文字版流量路径

普通前端页面访问路径如下：

用户浏览器发送页面请求，请求先进入 `ingress-nginx-controller`。Ingress Controller 根据 `cloud-demo.local` 的 Ingress 规则把请求转发给 `edge-nginx`。`edge-nginx` 判断该请求是静态资源或前端路由请求后，直接返回前端构建产物中的页面和静态文件。

普通业务 API 请求路径如下：

用户浏览器发起 `/api/` 请求。请求先到达 `ingress-nginx-controller`，然后进入 `edge-nginx`。`edge-nginx` 根据 `/api/` 路径把请求代理给 `gateway-service`。`gateway-service` 再根据具体 API 路径和网关路由配置，把请求转发给对应的业务服务。业务服务处理完成后，响应沿原路径返回，即业务服务返回给 `gateway-service`，`gateway-service` 返回给 `edge-nginx`，`edge-nginx` 返回给 Ingress Controller，最后返回给用户浏览器。

监控后台请求路径如下：

用户访问 `/monitor/`。请求进入 Ingress Controller 后到达 `edge-nginx`。`edge-nginx` 根据 `/monitor/` 路径把请求代理给 `monitor-service`。`monitor-service` 展示 Spring Boot Admin 监控后台，并通过服务发现和 Actuator 信息查看各 Spring Boot 服务状态。

MCP 请求路径如下：

MCP 客户端访问 `/mcp`、`/mcp/`、`/.well-known/`、`/authorize`、`/token` 或 `/register` 等路径。请求先经过 Ingress Controller 和 `edge-nginx`，再由 `edge-nginx` 转发给 `mcp-service`。`mcp-service` 在内部根据需要调用 `gateway-service` 或其他项目能力。

Grafana 请求路径如下：

用户访问 `/grafana/`。请求经过 Ingress Controller 后进入 `edge-nginx`。`edge-nginx` 将该路径代理到 `grafana.observability.svc.cluster.local:3000`。Grafana 再读取 Prometheus、Loki、Tempo 等数据源展示监控信息。

异步事件路径如下：

业务服务在处理活动、反馈、用户状态等业务变化时，将事件写入或发布到 RabbitMQ。其他业务服务从 RabbitMQ 消费事件并更新自身数据或投影状态。该路径不经过 Ingress、`edge-nginx` 或 `gateway-service`，属于服务内部的异步协作链路。

可观测数据路径如下：

业务服务和网关在运行过程中暴露 Actuator 指标并输出日志、链路信息。Prometheus 采集指标，Loki 聚合日志，Tempo 保存链路追踪数据，OpenTelemetry Collector 接收和转发追踪数据。Grafana 作为展示层读取这些数据源。

## 10. 单节点部署下的负载均衡含义

在单节点 Kubernetes 中，所有 Pod 都运行在同一个 Node 上，因此不存在跨机器的节点级负载均衡。但是 Kubernetes Service 仍然会在同一 Node 内的多个 Pod 副本之间进行服务级负载均衡。

例如，`edge-nginx`、`gateway-service` 和各业务服务在 YAML 中以 Deployment 形式部署，并通过 Service 暴露稳定访问名称。当某个 Service 背后存在多个 Ready Pod 时，Kubernetes 会维护对应的 Endpoint 列表，并把进入该 Service 的请求转发到不同 Pod。

因此，单节点部署下的负载均衡主要体现为 Pod 级请求分摊。它可以验证微服务拆分、Ingress 转发、Service 发现、网关路由、Pod 副本和 HPA 等 Kubernetes 能力，但它不能提供节点宕机后的高可用，也不能突破单台机器的 CPU、内存和磁盘 IO 上限。
