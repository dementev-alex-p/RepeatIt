package com.github.dementev_alex_p.repeatit.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
public enum CommandEnum {
    MAIN_MENU("start", "Главнове меню", 0),
    TRAINING("training", "\uD83C\uDFC6 Тренировка", 1),
    VIEW_CARD_MENU("view_card_menu", "📘 Карточки",  1),
    VIEW_CARD("view_card", "✍ Изменить", 4),
    SEARCH("search_card", "🔍︎ Поиск", -1),
    CREATE_CARD("create_card", "➕ Создать", 5),
    DELETE_CARD("delete_card", "❌ Удалить", 5),
    GENERATE_CARDS("import_cards", "✨ Генерация", 5),
    EDIT_CARD_FRONT_SIDE("edit_card_front_side", "📘", 5),
    EDIT_CARD_BACK_SIDE("edit_card_back_side", "📖", 5),
    EDIT_CARD_COLLECTION("edit_card_collection", "📚", 5),
    DELETE_CARD_BACK_SIDE("delete_card_back_side", "Удалить содержание", 5),
    VIEW_COLLECTION_LIST("view_collection_list", "📚 Коллекции", 1),
    VIEW_PUBLIC_COLLECTION_LIST("view_public_collection_list", "📚 Публичные коллекции", 2),
    VIEW_COLLECTION("view_collection", "Просмотр коллекции", 3),
    CREATE_COLLECTION("create_collection", "➕ Создать коллекцию", 3),
    DELETE_COLLECTION("delete_collection", "❌ Удалить", 4),
    ADD_PUBLIC_COLLECTION("add_cards_from_collection", "📚 Коллекции", 4),
    EDIT_COLLECTION_TITLE("edit_collection_title", "✍ Изменить название", 4),
    EXCLUDE_COLLECTION_FROM_TRAINING("exclude", "⏸ Исключить из тренировок", 4),
    REMOVE_COLLECTION_EXCLUSION("remove_exclusion", "▶ Снять исключение из тренировок", 4),
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
