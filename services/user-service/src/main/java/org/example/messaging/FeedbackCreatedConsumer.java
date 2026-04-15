package org.example.messaging;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.mapper.MqConsumeRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class FeedbackCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(FeedbackCreatedConsumer.class);
    private static final String CONSUMER_NAME = "user-service.feedback-created-consumer";

    private final MqConsumeRecordMapper mqConsumeRecordMapper;

    public FeedbackCreatedConsumer(MqConsumeRecordMapper mqConsumeRecordMapper) {
        this.mqConsumeRecordMapper = mqConsumeRecordMapper;
    }

    @RabbitListener(queues = MessagingConstants.QUEUE_FEEDBACK_CREATED)
    @Transactional(rollbackFor = Exception.class)
    public void onFeedbackCreated(DomainEvent event) {
        if (event == null || event.getMessageId() == null) {
            return;
        }
        boolean duplicated = mqConsumeRecordMapper.selectCount(new LambdaQueryWrapper<MqConsumeRecord>()
                .eq(MqConsumeRecord::getMessageId, event.getMessageId())
                .eq(MqConsumeRecord::getConsumerName, CONSUMER_NAME)) > 0;
        if (duplicated) {
            log.info("skip duplicated event stackId={} serviceName={} eventType={} messageId={} consumerName={}",
                    System.getenv().getOrDefault("STACK_ID", "single"),
                    System.getenv().getOrDefault("SERVICE_NAME", "user-service"),
                    event.getEventType(),
                    event.getMessageId(),
                    CONSUMER_NAME);
            return;
        }

        MqConsumeRecord consumeRecord = new MqConsumeRecord();
        consumeRecord.setMessageId(event.getMessageId());
        consumeRecord.setConsumerName(CONSUMER_NAME);
        consumeRecord.setStatus("CONSUMED");
        consumeRecord.setConsumedAt(LocalDateTime.now());
        mqConsumeRecordMapper.insert(consumeRecord);

        log.info("consumed event stackId={} serviceName={} eventType={} messageId={} consumerName={} aggregateId={}",
                System.getenv().getOrDefault("STACK_ID", "single"),
                System.getenv().getOrDefault("SERVICE_NAME", "user-service"),
                event.getEventType(),
                event.getMessageId(),
                CONSUMER_NAME,
                event.getAggregateId());
    }
}
