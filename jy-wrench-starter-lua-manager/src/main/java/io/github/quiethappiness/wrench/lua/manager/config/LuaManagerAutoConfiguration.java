package io.github.quiethappiness.wrench.lua.manager.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableAspectJAutoProxy
@Import({LuaManagerConfiguration.class})
@Slf4j
public class LuaManagerAutoConfiguration
{

}