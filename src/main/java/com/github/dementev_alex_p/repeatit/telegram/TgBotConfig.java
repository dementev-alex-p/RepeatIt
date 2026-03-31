package com.github.dementev_alex_p.repeatit.telegram;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "telegram.bot")
public class TgBotConfig {
    private String name;
    private String token;
}

