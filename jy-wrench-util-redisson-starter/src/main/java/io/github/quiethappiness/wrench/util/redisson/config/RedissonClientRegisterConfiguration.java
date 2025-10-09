package io.github.quiethappiness.wrench.util.redisson.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * DCCAutoConfiguration
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 动态配置-注册
 * @date 2025/9/10 16:31
 */
@Configuration
@EnableConfigurationProperties(value = {RedissonClientRegisterProperties.class})
@Slf4j
public class RedissonClientRegisterConfiguration
{
	@Bean("redissonClient")
	public RedissonClient redissonClient(
		RedissonClientRegisterProperties properties
	)
	{
		Config config = new Config();
		// 根据需要可以设定编解码器；https://github.com/redisson/redisson/wiki/4.-%E6%95%B0%E6%8D%AE%E5%BA%8F%E5%88%97%E5%8C%96
		// 使用更宽松的 Jackson 配置
		// setCustomCodeC(config);
		SingleServerConfig singleServerConfig = config.useSingleServer()
			.setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
			// .setPassword(properties.getPassword())
			.setConnectionPoolSize(properties.getPoolSize())
			.setConnectionMinimumIdleSize(properties.getMinIdleSize())
			.setIdleConnectionTimeout(properties.getIdleTimeout())
			.setConnectTimeout(properties.getConnectTimeout())
			.setRetryAttempts(properties.getRetryAttempts())
			.setRetryInterval(properties.getRetryInterval())
			.setPingConnectionInterval(properties.getPingInterval())
			.setKeepAlive(properties.isKeepAlive());
		if (StringUtils.hasText(properties.getPassword()))
		{
			singleServerConfig.setPassword(properties.getPassword());
		}
		RedissonClient redissonClient = Redisson.create(config);
		log.info("jy-wrench，注册器（RedissonClient）链接初始化完成。{} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());
		return redissonClient;
	}
	
	private static void setCustomCodeC(Config config)
	{
		// config.setCodec(JsonJacksonCodec.INSTANCE);
		config.setCodec(new RedisCodec());
		// config.setCodec(new JSONBCodec( ));
	}
	
	private static void setFlutterCodeC(Config config)
	{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 设置自定义 Codec
		config.setCodec(new JsonJacksonCodec(objectMapper));
	}
	
	static class RedisCodec extends BaseCodec
	{
		
		private final Encoder encoder = in ->
		{
			ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
			try
			{
				ByteBufOutputStream os = new ByteBufOutputStream(out);
				JSON.writeJSONString(os, in, SerializerFeature.WriteClassName);
				return os.buffer();
			}
			catch (IOException e)
			{
				out.release();
				throw e;
			}
			catch (Exception e)
			{
				out.release();
				throw new IOException(e);
			}
		};
		
		private final Decoder<Object> decoder =
			(buf, state) -> JSON.parseObject(new ByteBufInputStream(buf), Object.class);
		
		@Override
		public Decoder<Object> getValueDecoder()
		{
			return decoder;
		}
		
		@Override
		public Encoder getValueEncoder()
		{
			return encoder;
		}
	}
}