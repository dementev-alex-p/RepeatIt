package com.github.dementev_alex_p.repeatit.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
public enum CommandEnum {
    START("/start", "Запуск"),
    CREATE_CARD("/create_card", "Создать новую карточку"),
    VIEW_CARDS("/view_cards", "Посмотреть карточки"),
    TRAINING("/training", "Изучение кароточек"),
    ;

    @Getter
    private final String code;
    @Getter
    private final String description;

    public static CommandEnum findCommandByCode(final String code) {
        return Stream.of(values())
                .filter(c -> c.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Unknown command: " + code));
    }
}
