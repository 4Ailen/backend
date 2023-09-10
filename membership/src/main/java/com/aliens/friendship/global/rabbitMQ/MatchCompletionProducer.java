package com.aliens.friendship.global.rabbitMQ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchCompletionProducer {

    private final AmqpTemplate amqpTemplate;
    private final String exchangeName = "matching.exchange"; // Exchange 이름 설정
    private final String routingKey = "matching.completion"; // 라우팅 키 설정

    public void sendMatchCompletionMessage(String message) {
        amqpTemplate.convertAndSend(exchangeName, routingKey, message);
        log.info("매칭 완료 정보 보냄: " + message);
    }
}
