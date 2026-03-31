package com.github.dementev_alex_p.repeatit;

import com.github.dementev_alex_p.repeatit.telegram.TgBotConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(TgBotConfig.class)
@SpringBootApplication
public class RepeatItApplication {

	public static void main(String[] args) {
		SpringApplication.run(RepeatItApplication.class, args);
	}

}
