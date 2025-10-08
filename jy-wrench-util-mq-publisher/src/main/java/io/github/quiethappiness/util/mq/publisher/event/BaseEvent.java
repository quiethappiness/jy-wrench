package io.github.quiethappiness.util.mq.publisher.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;

@Data
public abstract class BaseEvent<T> {

    public MessageEvent<T> buildMessageEvent(T data)
    {
        return MessageEvent.<T>builder()
            .id(RandomStringUtils.randomAlphanumeric(11))
            .timestamp(new Date())
            .data(data)
            .build();
    }

    public abstract String topic();
    public abstract String exchange();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MessageEvent<T> {
        private String id;
        private Date timestamp;
        private T data;
    }

}