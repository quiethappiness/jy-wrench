package io.github.quiethappiness.wrench.lua.manager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jy.wrench.config.lua", ignoreInvalidFields = true)
@Data
public class LuaManagerAutoProperties
{
	private String path="script";
}
