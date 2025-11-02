package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.utils.CommandButtonUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
public enum CommandEnum {
    START("start", "Главнове меню", null),
    TRAINING("training", "Тренировка", CommandButtonUtils.createActionParameter("start")),
    CARDS("cards", "Карточки", new CommandParameter("page", "1")),
    ADD_CARD("add_card", "➕ Добавить", null),
    SEARCH("search_card", "\uD83D\uDD0D\uFE0E Поиск", null),
    CREATE_CARD("create_card", "➕ Создать", new CommandParameter("action", "start")),
    ADD_CARDS_FROM_COLLECTION("add_cards_from_collection", "📚 Коллекции", null),
    IMPORT_CARDS("import_cards", "📥 Импорт", null),
    EDIT_CARD("edit_card", "Редактирование карточки", new CommandParameter("action", "start_edition")),
    ADD_CARD_TO_COLLECTION("add_card_to_collection", "Добавить в коллекцию", null),
    COLLECTIONS("collections", "Коллекции карточек", null),
    SETTINGS("settings", "Настройки", null),
    DELETE_CARD("delete_card", "Удалить карточку", null);

    @Getter
    private final String code;
    @Getter
    private final String description;
    @Getter
    private final CommandParameter defaultParameter;

    public static CommandEnum findCommandByCode(final String code) {
        return Stream.of(values())
                .filter(c -> c.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Unknown command: " + code));
    }
}
