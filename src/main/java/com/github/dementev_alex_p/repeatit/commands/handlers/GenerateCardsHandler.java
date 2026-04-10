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
import com.github.dementev_alex_p.repeatit.message_context.MessageContext;
import com.github.dementev_alex_p.repeatit.utils.CardTextConverter;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

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

    private static final String STEP_1_TEXT = """
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

    private static final String STEP_2_TEXT = """
            <strong>Генерация карточек</strong>
            —————————————————————
            Этап 2. Создание карточек
            
            Отлично! Я создал для вас коллекцию с названием <code>%s</code>
            Теперь давайте наполним ее карточками!
            
            Для этого нам понадобится любой ИИ-агент (ChatGPT, DeepSeek, GigaChat, Алиса AI и другие)
            Перейдите в ИИ-агента и вставьте следующий запрос
            <code>%s</code>
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
            Карточки уже доступны для изучения и начнут появляться в тренировке.
            Отредактировать, заменить и удалить карточки вы можете перейдя в коллекцию.
            """;

    private static final String SERIALIZATION_ERROR_TEXT = """
            <strong>Генерация карточек</strong>
            —————————————————————
            Не удалось создать карточки на основании переданного текста.
            
            Проверьте, соответствует ли текст формату JSON.
            Формат JSON, подразумевает, что информация по карточкам соответствует следующему шаблоку
            <code>[{"front": "Столица Франции", "back": "Париж"},{"front": "Столица Германии", "back": "Берлин"}]</code>
            Возможно ваш текст не соответствует этому формату или содержит лишнюю информацию (комментарии, пояснения).
            Проверьте, при необходимости повторите генерацию и попробуйте прислать еще раз
            
            Ваш текст
            <code>%s</code>
            """;

    private static final String PROMPT = """
            <code>
            Ты — ассистент для генерации учебных карточек для интервального повторения (алгоритм SM-2).
            Пользователь задаст тему. Твоя задача — создать от 10 до 100 карточек (количество определи самостоятельно, исходя из полноты темы)
            Тема:{%s}. Карточки для SM-2: краткие вопросы и ответы.
            Ответ дай на русском языке в JSON формате [{front:"вопрос", back:"ответ"}] без лишних комментариев.</code>
            """;
    public static final String START_ACTION = "start";
    public static final String STEP_1_ACTION = "step_1";
    public static final String VIEW_EXAMPLES = "view_examples";
    public static final String STEP_2_ACTION = "step_2";
    public static final String FINISH_ACTION = "finish";
    private final CardCollectionService cardCollectionService;
    private final ObjectMapper objectMapper;
    private final CardService cardService;

    @Override
    public CommandEnum getCommand() {
        return CommandEnum.GENERATE_CARDS;
    }

    @Override
    public CommandResponse processCommand(final MessageContext context) {
        return switch (determinateStep(context)) {
            case START_ACTION -> startGeneration();
            case STEP_1_ACTION -> processFirstStep();
            case VIEW_EXAMPLES -> viewExamples();
            case STEP_2_ACTION -> processSecondStep(context);
            case FINISH_ACTION -> finishGeneration(context);
            default -> throw new RuntimeException("Unknown command");
        };
    }

    private CommandResponse startGeneration() {
        final List<CommandLine> commandLines = Stream.of(
                new CommandButton(getCommand(), "\uD83E\uDD16 Начать", CommandParameterUtils.createAction(STEP_1_ACTION)),
                new BackButton()
        ).map(CommandLine::new).toList();
        return CommandResponse
                .builder()
                .text(TITLE_TEXT_TEXT)
                .availableCommands(commandLines)
                .build();
    }

    private CommandResponse processFirstStep() {
        final List<CommandLine> commandLines = Stream.of(
                new CommandButton(getCommand(), "\uD83D\uDCCB Примеры", CommandParameterUtils.createAction(VIEW_EXAMPLES)),
                new BackButton()
        ).map(CommandLine::new).toList();
        return CommandResponse
                .builder()
                .text(String.format(STEP_1_TEXT))
                .availableCommands(commandLines)
                .isAnswerExcepted(true)
                .build();
    }

    private CommandResponse viewExamples() {
        final List<CommandLine> commandLines = Stream.of(
                new CommandButton(getCommand(), "↩ Вернуться назад", CommandParameterUtils.createAction(STEP_1_ACTION))
        ).map(CommandLine::new).toList();
        return CommandResponse
                .builder()
                .text(String.format(VIEW_EXAMPLES_TEXT))
                .availableCommands(commandLines)
                .isAnswerExcepted(true)
                .build();
    }

    private CommandResponse processSecondStep(final MessageContext context) {
        final String collectionName = context.message().orElseThrow();
        final CardCollection collection = cardCollectionService.createCollection(context.userId(), collectionName);
        context.commandParameters().put(CommandParameterUtils.COLLECTION_PARAMETER_CODE, String.valueOf(collection.getId()));
        context.commandParameters().put(CommandParameterUtils.ACTION_PARAMETER_CODE, STEP_2_ACTION);

        final List<CommandLine> commands = Stream.of(
                new CommandButton(getCommand(), "↩ Вернуться назад", CommandParameterUtils.createAction(STEP_1_ACTION))
        ).map(CommandLine::new).toList();

        final String promptText = String.format(PROMPT, CardTextConverter.escapeForHtml(collectionName));
        return CommandResponse
                .builder()
                .text(String.format(STEP_2_TEXT, CardTextConverter.escapeForHtml(collectionName), promptText))
                .isAnswerExcepted(true)
                .availableCommands(commands)
                .build();
    }

    private CommandResponse finishGeneration(final MessageContext context) {
        final String message = context.message().orElseThrow();
        final List<GeneratedCard> generatedCards = extractGeneratedCardsFromMessage(message);
        if (generatedCards.isEmpty()) {
            final List<CommandLine> commands = Stream.of(
                    new CommandButton(getCommand(), "↩ Вернуться назад", CommandParameterUtils.createAction(STEP_1_ACTION))
            ).map(CommandLine::new).toList();
            return CommandResponse
                .builder()
                .text(String.format(SERIALIZATION_ERROR_TEXT, message))
                .availableCommands(commands)
                .isAnswerExcepted(true)
                .build();
        }
        final CardCollection collection = cardCollectionService.findById(
                CommandParameterUtils.extractCollectionId(context)
        );
        cardService.createCards(context.userId(), collection, generatedCards);
        final List<CommandLine> commandLines = Stream.of(
                new ViewCardsInCollectionButton(collection.getId()),
                new CommandButton(getCommand(), "\uD83E\uDD16 Сгенерировать еще", CommandParameterUtils.createAction(START_ACTION)),
                new BackButton()
        ).map(CommandLine::new).toList();
        context.commandParameters().put(CommandParameterUtils.ACTION_PARAMETER_CODE, FINISH_ACTION);

        return CommandResponse
                .builder()
                .text(String.format(SUCCESS_CREATION_TEXT, CardTextConverter.escapeForHtml(collection.getName()), generatedCards.size()))
                .availableCommands(commandLines)
                .build();
    }

    private List<GeneratedCard> extractGeneratedCardsFromMessage(final String message) {
        try {
             return objectMapper.readValue(
                     message,
                     new TypeReference<>() {
                     }
            );
        } catch (Exception e) {
            return List.of();
        }
    }

    private String determinateStep(final MessageContext context) {
        final String action = CommandParameterUtils
                .extractNullableAction(context)
                .orElseThrow();
        if (context.message().isPresent()) {
            return switch (action) {
                case STEP_1_ACTION, VIEW_EXAMPLES -> STEP_2_ACTION;
                case STEP_2_ACTION -> FINISH_ACTION;
                default -> throw new IllegalStateException("Unexpected value: " + action);
            };
        }
        return action;
    }

    public record GeneratedCard(String front, String back) {
    }

}
