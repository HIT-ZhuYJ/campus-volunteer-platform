SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS event_outbox (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Outbox ID',
    message_id      VARCHAR(64)  NOT NULL COMMENT '业务消息ID',
    event_type      VARCHAR(100) NOT NULL COMMENT '事件类型',
    aggregate_type  VARCHAR(100) NOT NULL COMMENT '聚合类型',
    aggregate_id    VARCHAR(100) NOT NULL COMMENT '聚合ID',
    payload_json    LONGTEXT     NOT NULL COMMENT '事件负载JSON',
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/SENT/FAILED',
    retry_count     INT          NOT NULL DEFAULT 0 COMMENT '重试次数',
    next_retry_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下次重试时间',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at         DATETIME     NULL,
    UNIQUE KEY uk_message_id (message_id),
    KEY idx_status_next_retry (status, next_retry_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事务外箱事件表';

CREATE TABLE IF NOT EXISTS mq_consume_record (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    message_id    VARCHAR(64)  NOT NULL COMMENT '消息ID',
    consumer_name VARCHAR(120) NOT NULL COMMENT '消费者名称',
    status        VARCHAR(20)  NOT NULL DEFAULT 'CONSUMED' COMMENT '消费状态',
    consumed_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_message_consumer (message_id, consumer_name),
    KEY idx_consumer_time (consumer_name, consumed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MQ消费幂等记录';
