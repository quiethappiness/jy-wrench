package io.github.quiethappiness.wrench.util.web.config.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({WebConfiguration.class})
public class WebAutoConfiguration
{
}
