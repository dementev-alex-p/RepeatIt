package com.github.dementev_alex_p.repeatit.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
public enum CommandEnum {
    MAIN_MENU("start", "Главнове меню", 0),
    TRAINING("training", "\uD83C\uDFC6 Тренировка", 1),
    VIEW_CARD_LIST("view_card_list", "📘 Карточки",  1),
    VIEW_CARD("view_card", "✍ Изменить", 2),
    ADD_CARD("add_card", "➕ Добавить", 2),
    SEARCH("search_card", "🔍︎ Поиск", 2),
    CREATE_CARD("create_card", "➕ Создать", 3),
    DELETE_CARD("delete_card", "❌ Удалить", 3),
    IMPORT_CARDS("import_cards", "📥 Импорт", 3),
    EDIT_CARD_FRONT_SIDE("edit_card_front_side", "📘", 3),
    EDIT_CARD_BACK_SIDE("edit_card_back_side", "📖", 3),
    EDIT_CARD_COLLECTION("edit_card_collection", "📚", 3),
    DELETE_CARD_BACK_SIDE("delete_card_back_side", "Удалить содержание", 3),
    VIEW_COLLECTION_LIST("view_collection_list", "📚 Коллекции", 1),
    VIEW_COLLECTION("view_collection", "Просмотр коллекции", 2),
    CREATE_COLLECTION("create_collection", "➕ Создать коллекцию", 2),
    DELETE_COLLECTION("delete_collection", "❌ Удалить", 2),
    ADD_PUBLIC_COLLECTION("add_cards_from_collection", "📚 Коллекции", 3),
    EDIT_COLLECTION_TITLE("edit_collection_title", "✍ Изменить название", 3),
    PAUSE_STUDYING_COLLECTION("pause_study", "⏸ Приостановить изучение", 3),
    CONTINUE_STUDYING_COLLECTION("continue_study", "▶ Возобновить изучение", 3),
    SETTINGS("settings", "Настройки", 1),
    RETURN_BACK("back", "↩ Вернуться назад",0)
    ;

    @Getter
    private final String code;
    @Getter
    private final String description;
    @Getter
    private final int hierarchyLevel;

    public static CommandEnum findCommandByCode(final String code) {
        return Stream.of(values())
                .filter(c -> c.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Unknown command: " + code));
    }
}
