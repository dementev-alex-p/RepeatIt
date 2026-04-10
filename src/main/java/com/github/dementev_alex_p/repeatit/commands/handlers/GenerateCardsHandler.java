package com.github.dementev_alex_p.repeatit.commands.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.collections.CardCollection;
import com.github.dementev_alex_p.repeatit.collections.CardCollectionService;
import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.buttons.BackButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.CommandButton;
import com.github.dementev_alex_p.repeatit.commands.buttons.ViewCardsInCollectionButton;
import com.github.dementev_alex_p.repeatit.commands.result.CommandLine;
import com.github.dementev_alex_p.repeatit.commands.result.CommandResponse;
import com.github.dementev_alex_p.repeatit.gigachat.GigaChatService;
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CardTextConverter;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenerateCardsHandler implements CommandHandler {

    private static final String TITLE_TEXT_TEXT = """
            <strong>Генерация карточек</strong>
            —————————————————————
            Данный раздел позволяет создать множество карточек всего в пару кликов.
            
            С использованием ИИ вы можете быстро сгенерировать любое количество карточек на интересующую вас тему.
            Бот их сохранит, а его алгоритмы помогут вам быстро и надолго запомнить все карточки.
            
            Нажимайте <i><strong>Начать</strong></i> и мы вместе с вами выберем тему и создадим карточки.
            """;

    private static final String TOPIC_SELECTION_TEXT = """
            <strong>Генерация карточек</strong>
            —————————————————————
            Этап 1. Тематика для карточек
            
            Определитесь с интересующей вас темой (например "Самые употребительные фразы для путешествий на английском языке")
            
            Тематика может быть абсолютно любой, для вдохновения можете посмотреть на примеры тем, нажав <i><strong>Примеры</strong></i>
            
            Как определитесь, присылайте мне тему, я создам для нее коллекцию и мы приступим к генерации карточек...
            """;

    private static final String VIEW_EXAMPLES_TEXT = """
            <strong>Генерация карточек</strong>
            —————————————————————
            Примеры тематик
            
            🌍 География и путешествия
            • Столицы самых популярных стран
            • Самые длинные реки и высокие горы (Нил, Амазонка, Эверест, К2)
            
            📚 Иностранные языки
            • Неправильные глаголы английского языка (go – went – gone)
            • Самые употребительные фразы для путешествий (английский/испанский/французский)
            
            🧠 Наука и медицина
            • Периодическая таблица (H → водород, O → кислород)
            • Анатомия человека: основные кости скелета (где находится бедренная, локтевая)
            • Единицы измерения в физике (Ньютон, Паскаль, Джоуль — что измеряют)
            • Термины из психологии (когнитивный диссонанс, эффект Даннинга-Крюгера)
            
            💰 Финансы и бизнес
            • Основные инвестиционные термины (акции, облигации, дивиденды, купон)
            • Ключевые показатели бизнеса (EBITDA, ROI, CAC, LTV)
            • Налоговые вычеты в РФ (на лечение, обучение, имущество — условия)
            
            🎨 Искусство и культура
            • Знаменитые картины и их авторы ("Мона Лиза" → Леонардо да Винчи)
            • Классическая музыка: композитор → произведение (Бетховен → "Лунная соната")
            • Архитектурные стили и их черты (барокко, модерн, конструктивизм)
            
            💻 Программирование и IT
            • Основные команды Git (git commit -m "message" → что делает)
            • SQL-запросы для начинающих (SELECT, JOIN, GROUP BY синтаксис)
            • HTTP-статусы и их значения (200, 404, 500, 418)
            • Команды Linux/Unix для работы с файлами (ls, cd, grep, chmod)
            
            💡 Как определитесь, присылайте мне тему, я создам для нее коллекцию и мы приступим к генерации карточек...
            """;

    private static final String CREATE_COLLECTION_TEXT = """
            <strong>Генерация карточек</strong>
            —————————————————————
            Этап 2. Подготовка к генерации
            
            Отлично! Я создал для вас коллекцию с названием <code>%s</code>
            
            Теперь давайте наполним ее карточками!
            • Вы можете нажать "Сгенерировать" и я сам создам карточки с помощью ИИ
            • Вы можете нажать "Получить промпт", что бы самостоятельно подготовить карточки (продвинутый режим)
            """;

    private static final String VIEW_PROMPT_TEXT =
            """
            <strong>Генерация карточек</strong>
            —————————————————————
            Этап 3. Ручная генерация
            
            Для этого нам понадобится любой ИИ-агент (ChatGPT, DeepSeek, GigaChat, Алиса AI и другие)
            Перейдите в ИИ-агента и вставьте следующий запрос
            <code>%s Разбей ответ на несколько JSON, длина каждого не должна превышать 4096 символов.
            </code>
            После того, как ИИ агент сгенерирует ответ, проверьте результат и при необходимости попросите агента внести корректировки
            Когда ответ будет готов, вам нужно будет прислать его мне
            Важно, что бы он был в формате JSON (начинался с [{ и заканчивался }], без лишних слов)
            """;

    private static final String SUCCESS_CREATION_TEXT = """
            <strong>Генерация карточек</strong>
            —————————————————————
            Поздравляю! Карточки успешно сохранены!
            
            Коллекция: <strong>%s</strong>
            Количество карточек: <strong>%d</strong>
            Карточки уже доступны для изучения и начнут появляться в тренировке
            Просмотреть карточки, отредактировать, заменить и удалить вы можете перейдя в коллекцию
            """;

    private static final String SUCCESS_JSON_PARSE_TEXT = """
            <strong>Генерация карточек</strong>
            —————————————————————
            Отлично! Карточки получены и сохранены!
            Коллекция: <strong>%s</strong>
            Количество карточек: <strong>%d</strong>
            
            Вы можете прислать еще JSON или нажать "Завершить"
            """;

    private static final String SERIALIZATION_ERROR_TEXT = """
            <strong>Генерация карточек</strong>
            —————————————————————
            Не удалось сгенерировать карточки по техническим причинам
            """;

    private static final String PROMPT = """
            Ты — ассистент для генерации учебных карточек для интервального повторения (алгоритм SM-2). Твоя задача — создать 20-50 карточек на русском языке на тему {%s}. Лицевая сторона карточки - это термин или вопрос. Обратная сторона - это определение или ответ. Размер до 500 символов. Формат ответа - простой текст (без оформления) в JSON формате [{"front":"вопрос", "back":"ответ"}] без лишних комментариев и уточнений - только JSON в формате простого текста. JSON должен быть валидный, в вопросах и ответах избегай двойных кавычек.
            """;

    public static final String START_ACTION = "start";
    public static final String SELECT_TOPIC = "step_1";
    public static final String VIEW_EXAMPLES = "view_examples";
    public static final String CREATE_COLLECTION = "step_2";
    public static final String GENERATE = "generate";
    public static final String FINISH = "finish";
    public static final String GET_PROMPT = "prompt";
    public static final String PARSE_JSON = "parse_json";
    private final CardCollectionService cardCollectionService;
    private final ObjectMapper objectMapper;
    private final CardService cardService;
    private final GigaChatService gigaChatService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.GENERATE_CARDS;
    }

    @Override
    public CommandResponse processCommand(final MessageContext context) {
        return switch (determinateStep(context)) {
            case START_ACTION -> startGeneration();
            case SELECT_TOPIC -> viewTopicSelectionMessage();
            case VIEW_EXAMPLES -> viewExamples();
            case CREATE_COLLECTION -> createCollection(context);
            case GENERATE -> generateCards(context);
            case GET_PROMPT -> viewPrompt(context);
            case PARSE_JSON -> createCardsFromJson(context);
            case FINISH -> finishGeneration(context);
            default -> throw new RuntimeException("Unknown command");
        };
    }

    private CommandResponse createCardsFromJson(final MessageContext context) {
        final long collectionId = CommandParameterUtils.extractCollectionId(context);
        final List<GeneratedCard> generatedCards = extractCardsFromJson(context.message().orElseThrow());
        final CardCollection collection = cardCollectionService.findById(collectionId);

        cardService.createCards(context.userId(), collection, generatedCards);

        final Integer totalCardCount = cardService.findCardCountByCollectionId(collectionId);

        final List<CommandLine> commandLines = List.of(new CommandLine(new CommandButton(
                getCommand(),
                "Завершить",
                CommandParameterUtils.createAction(FINISH),
                CommandParameterUtils.createCollectionIdParameter(collectionId)
        )));

        context.commandParameters().put(CommandParameterUtils.ACTION_PARAMETER_CODE, PARSE_JSON);

        return CommandResponse
                .builder()
                .text(String.format(SUCCESS_JSON_PARSE_TEXT, CardTextConverter.escapeForHtml(collection.getName()), totalCardCount))
                .availableCommands(commandLines)
                .isAnswerExcepted(true)
                .build();
    }

    private CommandResponse viewPrompt(final MessageContext context) {
        final CardCollection collection = cardCollectionService.findById(CommandParameterUtils.extractCollectionId(context));
        final String text = String.format(
                VIEW_PROMPT_TEXT,
                String.format(PROMPT, CardTextConverter.escapeForHtml(collection.getName()))
        );
        final List<CommandLine> commandLines = List.of(new CommandLine(
                new CommandButton(getCommand(), "↩ Вернуться назад", CommandParameterUtils.createAction(SELECT_TOPIC))
        ));

        return CommandResponse
                .builder()
                .text(text)
                .availableCommands(commandLines)
                .isAnswerExcepted(true)
                .build();
    }

    private CommandResponse startGeneration() {
        final List<CommandLine> commandLines = Stream.of(
                new CommandButton(getCommand(), "\uD83E\uDD16 Начать", CommandParameterUtils.createAction(SELECT_TOPIC)),
                new BackButton()
        ).map(CommandLine::new).toList();
        return CommandResponse
                .builder()
                .text(TITLE_TEXT_TEXT)
                .availableCommands(commandLines)
                .build();
    }

    private CommandResponse viewTopicSelectionMessage() {
        final List<CommandLine> commandLines = Stream.of(
                new CommandButton(getCommand(), "\uD83D\uDCCB Примеры", CommandParameterUtils.createAction(VIEW_EXAMPLES)),
                new BackButton()
        ).map(CommandLine::new).toList();
        return CommandResponse
                .builder()
                .text(String.format(TOPIC_SELECTION_TEXT))
                .availableCommands(commandLines)
                .isAnswerExcepted(true)
                .build();
    }

    private CommandResponse viewExamples() {
        final List<CommandLine> commandLines = Stream.of(
                new CommandButton(getCommand(), "↩ Вернуться назад", CommandParameterUtils.createAction(SELECT_TOPIC))
        ).map(CommandLine::new).toList();
        return CommandResponse
                .builder()
                .text(String.format(VIEW_EXAMPLES_TEXT))
                .availableCommands(commandLines)
                .isAnswerExcepted(true)
                .build();
    }

    private CommandResponse createCollection(final MessageContext context) {
        final String collectionName = context.message().orElseThrow();
        final CardCollection collection = cardCollectionService.createCollection(context.userId(), collectionName);
        context.commandParameters().put(CommandParameterUtils.COLLECTION_PARAMETER_CODE, String.valueOf(collection.getId()));
        context.commandParameters().put(CommandParameterUtils.ACTION_PARAMETER_CODE, CREATE_COLLECTION);

        final List<CommandLine> commands = Stream.of(
                new CommandButton(
                        getCommand(),
                        "\uD83E\uDD16 Сгенерировать",
                        CommandParameterUtils.createAction(GENERATE),
                        CommandParameterUtils.createCollectionIdParameter(collection.getId())
                ),
                new CommandButton(
                        getCommand(),
                        "Получить промпт",
                        CommandParameterUtils.createAction(GET_PROMPT),
                        CommandParameterUtils.createCollectionIdParameter(collection.getId())
                ),
                new CommandButton(
                        getCommand(), "↩ Вернуться назад", CommandParameterUtils.createAction(SELECT_TOPIC)
                )
        ).map(CommandLine::new).toList();

        return CommandResponse
                .builder()
                .text(String.format(CREATE_COLLECTION_TEXT, CardTextConverter.escapeForHtml(collectionName)))
                .isAnswerExcepted(true)
                .availableCommands(commands)
                .build();
    }

    private CommandResponse finishGeneration(final MessageContext context) {
        final long collectionId = CommandParameterUtils.extractCollectionId(context);
        final CardCollection collection = cardCollectionService.findById(collectionId);
        final Integer totalCardCount = cardService.findCardCountByCollectionId(collectionId);
        return finishGeneration(collection, totalCardCount);
    }

    private CommandResponse finishGeneration(final CardCollection collection, final int totalCardCount) {
        final List<CommandLine> commandLines = Stream.of(
                new ViewCardsInCollectionButton(collection.getId()),
                new CommandButton(getCommand(), "\uD83E\uDD16 Сгенерировать еще", CommandParameterUtils.createAction(START_ACTION)),
                new BackButton()
        ).map(CommandLine::new).toList();

        return CommandResponse
                .builder()
                .text(String.format(SUCCESS_CREATION_TEXT, CardTextConverter.escapeForHtml(collection.getName()), totalCardCount))
                .availableCommands(commandLines)
                .build();
    }

    private CommandResponse generateCards(final MessageContext context) {
        final CardCollection collection = cardCollectionService.findById(CommandParameterUtils.extractCollectionId(context));
        final String prompt = String.format(PROMPT, collection.getName());
        final String json = gigaChatService.send(prompt);
        final List<GeneratedCard> generatedCards = extractCardsFromJson(json);
        if (generatedCards.isEmpty()) {
            final List<CommandLine> commands = Stream.of(
                    new CommandButton(getCommand(), "↩ Вернуться назад", CommandParameterUtils.createAction(SELECT_TOPIC))
            ).map(CommandLine::new).toList();
            return CommandResponse
                    .builder()
                    .text(String.format(SERIALIZATION_ERROR_TEXT))
                    .availableCommands(commands)
                    .isAnswerExcepted(true)
                    .build();
        }

        cardService.createCards(context.userId(), collection, generatedCards);
        return finishGeneration(collection, generatedCards.size());
    }

    private List<GeneratedCard> extractCardsFromJson(final String message) {
        try {
            return objectMapper.readValue(
                    message,
                    new TypeReference<>() {
                    }
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            return List.of();
        }
    }

    private String determinateStep(final MessageContext context) {
        final String action = CommandParameterUtils
                .extractNullableAction(context)
                .orElseThrow();
        if (context.message().isPresent()) {
            return switch (action) {
                case SELECT_TOPIC, VIEW_EXAMPLES -> CREATE_COLLECTION;
                case GET_PROMPT, PARSE_JSON -> PARSE_JSON;
                default -> throw new IllegalStateException("Unexpected value: " + action);
            };
        }
        return action;
    }

    public record GeneratedCard(String front, String back) {
    }

}
