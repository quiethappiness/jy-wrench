package com.jingyue.wrench.dynamic.config.center.types.common;

public class Constants {

    public final static String DYNAMIC_CONFIG_CENTER_REDIS_TOPIC = "DYNAMIC_CONFIG_CENTER_REDIS_TOPIC_";

    public final static String SYMBOL_COLON = ":";

    public final static String LINE = "_";

    public static String getTopic(String application) {
        return DYNAMIC_CONFIG_CENTER_REDIS_TOPIC + application;
    }

}