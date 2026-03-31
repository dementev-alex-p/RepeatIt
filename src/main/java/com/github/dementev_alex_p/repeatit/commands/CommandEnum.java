package com.github.dementev_alex_p.repeatit.commands;

import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
public enum CommandEnum {
    START("start", "Главнове меню", null),
    TRAINING("training", "\uD83C\uDFC6 Тренировка", null),
    VIEW_CARD_LIST("view_card_list", "📘 Карточки",  CommandParameterUtils.createPageParameter(1)),
    VIEW_CARD("view_card", "✍ Изменить", null),
    ADD_CARD("add_card", "➕ Добавить", null),
    SEARCH("search_card", "🔍︎ Поиск", null),
    CREATE_CARD("create_card", "➕ Создать", CommandParameterUtils.createActionParameter("start")),
    IMPORT_CARDS("import_cards", "📥 Импорт", null),
    EDIT_CARD_FRONT_SIDE("edit_card_front_side", "📘", null),
    EDIT_CARD_BACK_SIDE("edit_card_back_side", "📖", null),
    EDIT_CARD_COLLECTION("edit_card_collection", "📚", null),
    DELETE_CARD_BACK_SIDE("delete_card_back_side", "Удалить содержание", null),
    VIEW_COLLECTION_LIST("view_collection_list", "📚 Коллекции", CommandParameterUtils.createPageParameter(1)),
    VIEW_COLLECTION("view_collection", "Просмотр коллекции", null),
    CREATE_COLLECTION("create_collection", "➕ Создать коллекцию", null),
    ADD_PUBLIC_COLLECTION("add_cards_from_collection", "📚 Коллекции", null),
    SETTINGS("settings", "Настройки", null),
    DELETE_CARD("delete_card", "❌ Удалить", null),
    EDIT_COLLECTION("edit_collection_title", "✍ Изменить название", null),
    DELETE_COLLECTION("delete_collection", "Удалить коллекцию", null),
    ;

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
