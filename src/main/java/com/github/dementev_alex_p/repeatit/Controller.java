package com.github.dementev_alex_p.repeatit;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class Controller {

    private final TgBotConfig tgBotConfig;

    @GetMapping
    public String test() {
        return tgBotConfig.getName();
    }
}
