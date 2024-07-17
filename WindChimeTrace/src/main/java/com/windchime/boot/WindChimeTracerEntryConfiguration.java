package com.windchime.boot;

import com.windchime.boot.config.aspect.WindChimePackageExpressionConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@ComponentScan({"com.windchime.boot"})
@Import({WindChimePackageExpressionConfig.class})
@Configuration
public class WindChimeTracerEntryConfiguration {
    public WindChimeTracerEntryConfiguration() {
    }
}
